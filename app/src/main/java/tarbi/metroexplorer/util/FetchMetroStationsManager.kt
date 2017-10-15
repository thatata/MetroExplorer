package tarbi.metroexplorer.util

import android.content.Context
import android.util.Log
import android.widget.ProgressBar
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion

/**
 * This uses the WMATA API
 */
class FetchMetroStationsManager(private val lat: Double?, private val lon: Double?,
                                private val radius: Double, private val context: Context,
                                private val progressBar: ProgressBar,
                                private val listener: FetchMetroListener) {

    // interface to talk to Activity
    interface FetchMetroListener {
        fun stationsFound(stationList: List<Station>?)
        fun stationsNotFound()
    }

    fun getNearestStation() : Station? {
        getData()
        // TODO do math for getting nearest station
        return null
    }

    fun getStations() : List<Station>? {
        getData()
        return null
    }

    private fun parse(jsonString: JsonObject?) : MutableList<Station>? {
        if (jsonString == null) {
            return null
        }

        val jsonArray = jsonString.getAsJsonArray("Entrances")
        val stationList: MutableList<Station> = mutableListOf()

        for (entrance in jsonArray) {
            Log.d("MyTag", entrance.toString())
            val id: Int = entrance.asJsonObject.get("ID").asInt
            val lat: Double = entrance.asJsonObject.get("Lat").asDouble
            val lon: Double = entrance.asJsonObject.get("Lon").asDouble
            val name: String = entrance.asJsonObject.get("Name").asString
            val description: String = entrance.asJsonObject.get("Description").asString
            val stationCode1: String = entrance.asJsonObject.get("StationCode1").asString
            val stationCode2: String = entrance.asJsonObject.get("StationCode2").asString
            val station = Station(id, lat, lon, description, name, stationCode1, stationCode2)
            Log.d("MyTag", station.toString())
            stationList.add(station)
        }

        return stationList
    }

    /* Function looks syncronous but it is actually async */
    private fun getData() {
        /* TODO remove the api key into a more private way of storing it */
        var url: String = "https://api.wmata.com/Rail.svc/json/jStationEntrances"
        val key: String = "e825c39a57db43a7a1b23206529caab4"
        /*
        * Request parameters:
        *  Lat
        *  Lon
        *  Radius
        * Request headers:
        *  api_key
        */
        url += "?Lat=" + lat.toString()
        url += "&Lon=" + lon.toString()
        url += "&Radius=" + radius.toString()
        Ion.with(context)
                .load(url)
                .setHeader("api_key", key)
                .progressBar(progressBar)
                .asJsonObject()
                .setCallback { _: Exception?, result: JsonObject? ->
                    if (result != null) {
                        listener.stationsFound(parse(result))
                    } else {
                        listener.stationsNotFound()
                    }
                }
    }
}

data class Station(
        val id: Int,
        val lat: Double,
        val lon: Double,
        val description: String,
        val name: String,
        val stationCode1: String,
        val stationCode2: String)
