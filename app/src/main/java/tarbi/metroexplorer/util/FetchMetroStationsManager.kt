package tarbi.metroexplorer.util

import android.content.Context
import android.util.Log
import android.widget.ProgressBar
import com.google.gson.JsonObject
import com.koushikdutta.async.future.FutureCallback
import com.koushikdutta.ion.Ion

/**
 * Created by hobbes on 9/26/17.
 * This uses the WMATA API
 */
class FetchMetroStationsManager(val lat: Double, val lon: Double, val radius: Double, val context: Context) {

    fun getNearestStation() : Station? {
        /* TODO use a separate thread when downloading information */
        getData()
        return null
    }

    fun getStations() : List<Station>? {
        /* TODO used for MetroStationActivity */
        return null
    }

    private fun parse(jsonString: JsonObject) : List<Station>? {
        return null
    }

    /* Function looks syncronous but it is actually async */
    private fun getData(): Array<JsonObject>? {
        /* TODO make this an async task */
        /* TODO remove the api key into a more private way of storing it */
        /* TODO read what object : does */
        /* TODO add progress bar */
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
        url += "?Lat="    + lat.toString()
        url += "&Lon="    + lon.toString()
        url += "&Radius=" + radius.toString()
        Ion.with(context)
                .load(url)
                .setHeader("api_key", key)
                .progressBar(ProgressBar(context))
                .asJsonObject()
                .setCallback(object : FutureCallback<JsonObject> {
                    override fun onCompleted(e: Exception?, result: JsonObject?) {
                        Log.d("MyTag", result.toString())
                        Log.d("MyTag", e.toString())
                    }
                })
        return null
    }
}

class Station(
        val lat: Double,
        val lon: Double,
        val description: String,
        val name: String,
        val stationCode1: String,
        val stationCode2: String)
