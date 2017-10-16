package tarbi.metroexplorer.util

import android.content.Context
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Thread.sleep

/**
 * This uses the WMATA API
 */
class FetchMetroStationsManager(private val phoneLat: Double?, private val phoneLon: Double?,
                                private val radius: Double, private val context: Context,
                                private val listener: FetchMetroListener) {

    /*
     * This is a bit of a hack.
     * We want to only notify the Activity that station info is ready after we have made query calls
     * to search for stations as well as query each station for additional information such as
     * station name. The additional query causes a problem because we make x number of queries for
     * x number of stations around the user and as these queries' callbacks arrive in an unknown
     * order there is no good way to know when we have received all of the additional information
     * and can thus activate the Activity's callback.
     *
     * We thus use a global count to keep track of the number of times the call back has activated
     * and after the callback has activated x (number of station) times, we callback to the Activity
     */
    private var stationCallBackCount: Int = 0

    // interface to talk to Activity
    interface FetchMetroListener {
        fun stationsFound(stationList: List<Station>?)
        fun stationsNotFound()
    }

    fun getNearestStation() : Station? {
        getEntranceData()
        // TODO do math for getting nearest station
        return null
    }

    fun getStations() { getEntranceData() }
    fun getAllStations() { getAllData() }

    private fun parseEntrances(jsonString: JsonObject?) : MutableList<Station>? {
        if (jsonString == null) {
            return null
        }

        val jsonArray = jsonString.getAsJsonArray("Entrances")
        val stationList: MutableList<Station> = mutableListOf()

        for (entrance in jsonArray) {
            val id: Int = entrance.asJsonObject.get("ID").asInt
            val lat: Double = entrance.asJsonObject.get("Lat").asDouble
            val lon: Double = entrance.asJsonObject.get("Lon").asDouble
            val entranceName: String = entrance.asJsonObject.get("Name").asString
            val description: String = entrance.asJsonObject.get("Description").asString
            val stationCode1: String = entrance.asJsonObject.get("StationCode1").asString
            val stationCode2: String = entrance.asJsonObject.get("StationCode2").asString
            val station = Station(id, lat, lon, description, entranceName,
                    "", stationCode1, stationCode2)
            stationList.add(station)
        }

        return stationList
    }

    private fun parseStation(stationList: MutableList<Station>?, jsonString: JsonObject?):
            List<Station>? {
        if (jsonString == null) {
            return null
        }
        stationList?.filter { it.stationCode1 == jsonString.get("Code").asString }?.
                forEach { it.stationName = jsonString.get("Name").asString }
        return stationList
    }

    private fun parseAllStations(jsonString: JsonObject?): List<Station>? {
        if (jsonString == null) {
            return null
        }
        val stationList: MutableList<Station> = mutableListOf()

        val jsonAarray: JsonArray = jsonString.getAsJsonArray("Stations")

        for (entrance in jsonAarray) {
            val lat: Double = entrance.asJsonObject.get("Lat").asDouble
            val lon: Double = entrance.asJsonObject.get("Lon").asDouble
            val stationName: String = entrance.asJsonObject.get("Name").asString
            val stationCode1: String = entrance.asJsonObject.get("Code").asString
            val station = Station(-1, lat, lon, "", "",
                    stationName, stationCode1, "")
            stationList.add(station)
        }

        return stationList
    }

    private fun getEntranceData() {
        /* TODO remove the api key into a more private way of storing it */
        var url = "https://api.wmata.com/Rail.svc/json/jStationEntrances"
        val key = "e825c39a57db43a7a1b23206529caab4"
        /*
        * Request parameters:
        *  Lat
        *  Lon
        *  Radius
        * Request headers:
        *  api_key
        */
        url += "?Lat=" + phoneLat.toString()
        url += "&Lon=" + phoneLon.toString()
        url += "&Radius=" + radius.toString()
        Ion.with(context)
                .load(url)
                .setHeader("api_key", key)
                .asJsonObject()
                .setCallback { _: Exception?, result: JsonObject? ->
                    doAsync {
                        if (result != null) {
                            val stationList = parseEntrances(result)
                            getStationData(stationList)
                        } else {
                            uiThread {
                                listener.stationsNotFound()
                            }
                        }
                    }
                }
    }

    // used to fetch extra information about the stations themselves, not just entrances
    private fun getStationData(stationList: List<Station>?) {
        /* TODO remove the api key into a more private way of storing it */
        if (stationList == null) {
            // TODO tell user there is a problem
            return
        }
        val key = "e825c39a57db43a7a1b23206529caab4"
        stationCallBackCount = 0
        for (station in stationList) {
            // Sleep to keep rate limit from being exceeded
            // Ideally we would just send as fast as we can and just retry the requests that failed
            // but we don't have access to that info in ion from the callback that is activated
            sleep(400, 0)
            var url = "https://api.wmata.com/Rail.svc/json/jStationInfo"
            url += "?StationCode=" + station.stationCode1
                    Ion.with(context)
                            .load(url)
                            .setHeader("api_key", key)
                            .asJsonObject()
                            .setCallback { _: Exception?, result: JsonObject? ->
                                doAsync {
                                    if (result != null) {
                                        parseStation(stationList as MutableList<Station>, result)
                                    } else {
                                        uiThread {
                                            listener.stationsNotFound()
                                        }
                                    }
                                    stationCallBackCount++
                                    assert(stationCallBackCount <= stationList.size)
                                    if (stationCallBackCount == stationList.size) {
                                        // remove duplicates based off of Station Code
                                        Log.d("MyTag", "All stations fetched")
                                        val stationListNoDups = stationList.distinctBy { it.stationCode1 }
                                        uiThread {
                                            listener.stationsFound(stationListNoDups)
                                        }
                                    }
                                }
                            }
        }
    }

    private fun getAllData() {
        /* TODO remove the api key into a more private way of storing it */
        val url = "https://api.wmata.com/Rail.svc/json/jStations"
        val key = "e825c39a57db43a7a1b23206529caab4"
        Ion.with(context)
                .load(url)
                .setHeader("api_key", key)
                .asJsonObject()
                .setCallback { _: Exception?, result: JsonObject? ->
                    doAsync {
                        if (result != null) {
                            val stationList = parseAllStations(result)
                            uiThread {
                                listener.stationsFound(stationList)
                            }
                        } else {
                            uiThread {
                                listener.stationsNotFound()
                            }
                        }
                    }
                }
    }
}

data class Station(
        val id: Int,
        val lat: Double,
        val lon: Double,
        val description: String,
        val entranceName: String,
        var stationName: String,
        val stationCode1: String,
        val stationCode2: String)
