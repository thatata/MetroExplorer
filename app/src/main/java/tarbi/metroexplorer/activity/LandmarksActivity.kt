package tarbi.metroexplorer.activity

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_landmarks.*
import org.jetbrains.anko.activityUiThread
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.yesButton
import tarbi.metroexplorer.R
import tarbi.metroexplorer.util.*

/* Select the nearest station */
class LandmarksActivity : AppCompatActivity(), LocationDetector.LocationDetectorListener,
        FetchMetroStationsManager.FetchMetroListener, YelpAuthManager.FetchYelpListener {

    private lateinit var locationDetector : LocationDetector
    private lateinit var progressBar      : ProgressBar
    private var          lastLocation     : Location? = null
    private var          myStations       : List<Station>? = null
    private var          myLandmarks        : List<Landmark>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

        // We initialize late because applicationContext can only be supplied after onCreate
        progressBar = findViewById(R.id.landmarkProgressBar)

        // check extra attribute from intent to determine whether to find location
        if (intent.hasExtra("findLocation")) {
            fetchLandmarks()
        } else {
            // otherwise, SELECT METRO STATION FROM LIST OF STATIONS
            Log.d("MyTag", "findLocation was not provided")
        }
    }

    private fun fetchLandmarks() {
        // if we don't have a location, try to get one
        if (lastLocation == null) {

            locationDetector = LocationDetector(applicationContext, this)
            locationDetector.detectLocation(progressBar)
            // once as we have location fetchLandmarks will be called again
            return
        }

        if (myStations == null)  {
            // once as we have a list of stations fetchLandmarks will be called again
            val locationNow = lastLocation
            val stationManager = FetchMetroStationsManager(locationNow?.latitude,
                    locationNow?.longitude,1609.34, applicationContext, this)
            progressBar.visibility = ProgressBar.VISIBLE
            // turn off progressbar when stationList is available in callback
            doAsync {
                stationManager.getStations()
            }
            // once we have stations fetchLandmarks will be called again
            return
        }

        // TODO select the closest station

        if (myLandmarks == null) {
            // fetch top 10 landmarks (will use real station location when ready)
            fetchTopTenLandmarks(38.9074690, -77.0618080)

            return
        }

        // initialize adapter
        val landmarkAdapter = LandmarksAdapter(myLandmarks)

        // set up recycler view
        landmark_recycler_view.layoutManager = LinearLayoutManager(this)
        landmark_recycler_view.adapter = landmarkAdapter
    }

    private fun fetchTopTenLandmarks(latitude: Double, longitude: Double) {
        val landmarkManager = YelpAuthManager(latitude, longitude, applicationContext, this)
        doAsync {
            landmarkManager.getLandmarks()
        }
    }

    private fun alertUser(alertTitle : String, alertMessage : String) {
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

    /* ---------------------------- Callbacks ---------------------------- */
    override fun stationsFound(stationList: List<Station>?) {
        myStations = stationList
        fetchLandmarks()
    }

    override fun stationsNotFound() {
        // TODO tell user that stations were not found
    }

    override fun landmarksFound(landmarks: List<Landmark>?) {
        myLandmarks = landmarks
        progressBar.visibility = ProgressBar.INVISIBLE
        fetchLandmarks()
    }

    override fun landsmarksNotFound() {
        // TODO tell user that landmarks were not found
    }

    override fun locationFound(location: Location) {
        // remove progress bar
        locationDetector.showLoading(false, progressBar)
        // update the last location in memory
        lastLocation = location
        fetchLandmarks()
    }

    override fun locationNotFound(reason: LocationDetector.FailureReason) {
        Log.d("MyTag", "Location NOT found")
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