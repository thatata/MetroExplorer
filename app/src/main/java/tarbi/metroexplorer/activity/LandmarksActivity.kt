package tarbi.metroexplorer.activity

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import tarbi.metroexplorer.R
import tarbi.metroexplorer.util.LocationDetector

class LandmarksActivity : AppCompatActivity(), LocationDetector.LocationDetectorListener {
    // Tag
    private val TAG = "LandmarksActivity"

    // black box class
    private lateinit var locationDetector : LocationDetector

    // latitude and longitude values for current location (set default values)
    var lat : Double = -1.0
    var long : Double = -1.0

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

         // check extra attribute from intent to determine whether to find location
         if (intent.hasExtra("findLocation")) {
             initLocationDetection()
         } else {
             // SELECT METRO STATION FROM LIST OF STATIONS
         }
    }

    fun initLocationDetection() {
        // initialize black box class and its interface
        locationDetector = LocationDetector(this)
        locationDetector.locationDetectorListener = this

        locationDetector.getLocation()
    }

    override fun locationFound(location: Location) {
        // update the latitude and longitude values
        lat = location.latitude
        long = location.longitude

        Log.i("lat/lon", "lat: ${location.latitude} long: ${location.longitude}")
    }

    override fun locationNotFound(reason: LocationDetector.FailureReason) {
        when(reason) {
            LocationDetector.FailureReason.TIMEOUT -> {
                Log.d(TAG, "Location timed out.")
            }
            LocationDetector.FailureReason.NO_PERMISSION -> {
                Log.d(TAG, "No location permission granted.")
            }
        }
    }
}