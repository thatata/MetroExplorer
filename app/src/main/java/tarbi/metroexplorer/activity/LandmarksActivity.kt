package tarbi.metroexplorer.activity

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_landmarks.*
import org.jetbrains.anko.activityUiThread
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.yesButton
import tarbi.metroexplorer.R
import tarbi.metroexplorer.util.LocationDetector

class LandmarksActivity : AppCompatActivity(), LocationDetector.LocationDetectorListener {
    // tag for this activity
    private val TAG = "LandmarksActivity"

    // black box class for detecting location
    private lateinit var locationDetector : LocationDetector

    // store last location in memory
    private var lastLocation : Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

         // check extra attribute from intent to determine whether to find location
         if (intent.hasExtra("findLocation")) {
            // if true, get location
             detectLocation()
         } else {
             // otherwise, SELECT METRO STATION FROM LIST OF STATIONS
         }
    }

    fun detectLocation() {
        // initialize black box class and interface
        initLocationDetection()

        // show progress bar indicating "loading"
        showLoading(true)

        // get location
        locationDetector.getLocation()
    }

    fun showLoading(show : Boolean) {
        // function to show or remove progress bar
        if (show) {
            // set visibility to visible
            progressBar.visibility = ProgressBar.VISIBLE
        } else {
            // set visibility to invisible
            progressBar.visibility = ProgressBar.INVISIBLE
        }
    }

    fun initLocationDetection() {
        // initialize black box class and its interface
        locationDetector = LocationDetector(this)
        locationDetector.locationDetectorListener = this
    }

    override fun locationFound(location: Location) {
        // remove progress bar
        showLoading(false)

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
        showLoading(false)

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