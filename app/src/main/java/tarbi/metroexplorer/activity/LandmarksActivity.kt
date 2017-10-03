package tarbi.metroexplorer.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import tarbi.metroexplorer.R
import tarbi.metroexplorer.util.LocationDetector

class LandmarksActivity : AppCompatActivity() {

    private lateinit var locationDetector : LocationDetector

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
        locationDetector.getLocation()
    }
}