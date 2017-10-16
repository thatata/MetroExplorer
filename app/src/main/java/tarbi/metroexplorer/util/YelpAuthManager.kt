package tarbi.metroexplorer.util

import android.content.Context
import android.util.Log
import android.widget.ProgressBar
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import org.json.JSONObject

/**
 * Created by hobbes on 9/26/17.
 */
class YelpAuthManager(private val lat : Double, private val lon : Double,
                      private val context: Context, private val listener : FetchYelpListener) {

    // interface to talk to Activity
    interface FetchYelpListener {
        fun landmarksFound(landmarks : List<Landmark>?)
        fun landsmarksNotFound()
    }

    fun getLandmarks() { getData() }

    private fun getData() {
        // request url
        var url = "https://api.yelp.com/v3/businesses/search"

        // unique access token for authentication
        val accessToken = "Bearer uKUN0Bg6YV-HBe7EYVed6rH6bHRgd1WGgW_p9Cv5UcZTLE4wigkNqvERHoyvourWUwqI4VcFw8S6d9XqsBaoMYyPEJoYDv550mQ0dhQKq7gJM1JNJczyzJ_E7lnIWXYx"

        // add values to query string
        url += "?latitude=$lat"
        url += "&longitude=$lon"
        url += "&categories=landmarks"
        url += "&limit=10" // just get the top 10 landmarks

        // fetch data with api call using Ion
        Ion.with(context)
                .load(url)
                .setHeader("Authorization", accessToken)
                .asJsonObject()
                .setCallback { _: Exception?, result: JsonObject? ->
                    if (result != null) {
                        listener.landmarksFound(parse(result))
                    } else {
                        listener.landsmarksNotFound()
                    }
                }
    }

    private fun parse(jsonString : JsonObject?) : MutableList<Landmark>? {
        // check if json string is null, if so return null
        if (jsonString == null) {
            return null
        }

        // use Gson to parse through json string
        val jsonArray = jsonString.getAsJsonArray("businesses")
        val landmarkList : MutableList<Landmark> = mutableListOf()

        // parse the string
        for (business in jsonArray) {
            val id : String = business.asJsonObject.get("id").asString
            val name : String = business.asJsonObject.get("name").asString
            val address : String = business.asJsonObject.get("location").asJsonObject.get("address1").asString +
                    business.asJsonObject.get("location").asJsonObject.get("city").asString
            val imageUrl : String = business.asJsonObject.get("image_url").asString
            val distance : Double = business.asJsonObject.get("distance").asDouble
            val landmark = Landmark(id, name, address, imageUrl, distance)

            Log.d("Yelp Test", landmark.toString())

            landmarkList.add(landmark)
        }

        return landmarkList
    }
}

data class Landmark(val id: String,
                    val name: String,
                    val address: String,
                    val imageUrl: String,
                    val distance: Double)