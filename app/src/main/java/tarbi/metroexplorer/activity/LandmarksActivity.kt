package tarbi.metroexplorer.activity

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import org.jetbrains.anko.activityUiThread
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.yesButton
import tarbi.metroexplorer.R
import tarbi.metroexplorer.util.FetchMetroStationsManager
import tarbi.metroexplorer.util.Station
import tarbi.metroexplorer.util.LocationDetector

class LandmarksActivity : AppCompatActivity(), LocationDetector.LocationDetectorListener {

    private lateinit var locationDetector : LocationDetector
    private val          progressBar      : ProgressBar = ProgressBar(applicationContext)
    private var          lastLocation     : Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

         // check extra attribute from intent to determine whether to find location
         if (intent.hasExtra("findLocation")) {
             locationDetector = LocationDetector(applicationContext, this)
             locationDetector.detectLocation(progressBar)


             /* Use FetchMetroStationsManager to get closest station */
             val stationManager = FetchMetroStationsManager(38.8978168, -77.0404246, 500.0,
              applicationContext)
             val nearestStation: Station? = stationManager.getNearestStation()
         } else {
             // otherwise, SELECT METRO STATION FROM LIST OF STATIONS
         }
    }

    override fun locationFound(location: Location) {
        // remove progress bar
        locationDetector.showLoading(false, progressBar)

        // update the last location in memory
        lastLocation = location
    }

    fun alertUser(alertTitle : String, alertMessage : String) {
        // alert user in an asynchronous task
        doAsync {
            // within an activity UI thread
            activityUiThread {
                // create alert (with Anko)
                alert {
                    // set attributes
                    title = alertTitle
                    message = alertMessage
                    yesButton { }
                }.show() // show alert
            }
        }
    }

    override fun locationNotFound(reason: LocationDetector.FailureReason) {
        // remove progress bar
        locationDetector.showLoading(false, progressBar)

        // check if last location exists, if so ignore
        if (lastLocation != null) return

        // show corresponding reason to user
        when(reason) {
            // show alert with proper message
            LocationDetector.FailureReason.TIMEOUT -> {
                alertUser("Location Detection Failed","Location timed out.")
            }
            LocationDetector.FailureReason.NO_PERMISSION -> {
                alertUser("Location Detection Failed","No location permission granted.")
            }
        }
    }
}