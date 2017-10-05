package tarbi.metroexplorer.activity

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import tarbi.metroexplorer.R
import tarbi.metroexplorer.util.LocationDetector

class LandmarksActivity : AppCompatActivity() {

    private lateinit var locationDetector : LocationDetector

    // request code for location permission request
    val ACCESS_LOCATION_REQUEST_CODE: Int = 0

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

         // check extra attribute from intent to determine whether to find location
         if (intent.getBooleanExtra("findLocation", false)) {
             getNearestLocation()
         }
    }

    fun getNearestLocation() {
        locationDetector = LocationDetector(this)
        locationDetector.requestPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // function to handle response of location permission request
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // check request code
        when(requestCode) {
            ACCESS_LOCATION_REQUEST_CODE -> {
                // if request is cancelled, the results array is empty
                if (grantResults != null && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.i("Permission", "GRANTED!!!!")
                } else {
                    // permission denied
                    Log.i("Permission", "DENIED!!!!!")
                }
                return
            }
        }
    }
}