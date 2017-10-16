package tarbi.metroexplorer.activity

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_landmarks.*
import kotlinx.android.synthetic.main.activity_landmarks_favorites.*
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
    private lateinit var persistanceManager : PersistanceManager
    private var          myFavorites      : List<Landmark>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize persistance manager
        persistanceManager = PersistanceManager(this)

        // check if station was selected
        if (intent.hasExtra("station")) {
            // set activity landmarks content view
            setContentView(R.layout.activity_landmarks)

            // set action bar title
            actionBar.title = "Landmarks"

            // We initialize late because applicationContext can only be supplied after onCreate
            progressBar = findViewById(R.id.landmarkProgressBar)

            // grab station from the intent extra
            val station = intent.getParcelableExtra<Station>("station")

            // initialize location variable
            initializeLocation(station)

            // no need to get station info, so fetch landmarks
            fetchLandmarks()

        }
        // check extra attribute from intent to determine whether to find location
        else if (intent.hasExtra("findLocation")) {
            // set activity landmarks content view
            setContentView(R.layout.activity_landmarks)

            // set action bar title
            actionBar.title = "Landmarks"

            // We initialize late because applicationContext can only be supplied after onCreate
            progressBar = findViewById(R.id.landmarkProgressBar)

            fetchStations()
        } else {  // otherwise, display favorite landmarks
            // set favorites content view
            setContentView(R.layout.activity_landmarks_favorites)

            // set action bar title
            actionBar.title = "Favorites"

            // fetch favorites from shared preferences
            getFavorites()
        }
    }

    private fun fetchStations() {
        // if we don't have a location, try to get one
        if (lastLocation == null) {

            locationDetector = LocationDetector(applicationContext, this)
            locationDetector.detectLocation(progressBar)
            // once as we have location fetchStations will be called again
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
                stationManager.getAllStations()
            }
            // once we have stations fetchLandmarks will be called again
            return
        }

        // TODO select the closest station

        // once closest station is detected, initialize lastLocation
        // initializeLocation(closestStation)

        // now fetch landmarks based on that location
        fetchLandmarks()
    }

    private fun getFavorites() {
        // fetch favorite landmarks with persistance manager
        val favorites : List<Landmark> = persistanceManager.fetchFavorites()

        // check if favorites list is empty
        if (favorites.isNotEmpty()) {
            // save favorites in its own variable
            myFavorites = favorites
        }

        // initialize landmark list
        initializeFavoritesList()
    }

    private fun initializeLocation(station: Station) {
        // initialize lastLocation and set lat and lon values
        lastLocation = Location("") // no need to have a provider name
        lastLocation!!.latitude = station.lat
        lastLocation!!.longitude = station.lon
    }

    private fun initializeLandmarkList() {
        if (myLandmarks == null) return

        // initialize adapter
        val landmarkAdapter = LandmarksAdapter(myLandmarks, this@LandmarksActivity)

        // set up recycler view
        landmark_recycler_view.layoutManager = LinearLayoutManager(this)
        landmark_recycler_view.adapter = landmarkAdapter
    }

    private fun initializeFavoritesList() {
        if (myFavorites == null) {
            // initialize empty list
            myFavorites = arrayListOf()
        }

        // initialize adapter
        val landmarkAdapter = LandmarksAdapter(myFavorites, this@LandmarksActivity)

        // set up recycler view
        favorites_recycler_view.layoutManager = LinearLayoutManager(this)
        favorites_recycler_view.adapter = landmarkAdapter
    }

    private fun fetchLandmarks() {
        if (lastLocation == null) return

        // create Yelp manager to get landmark data
        val landmarkManager = YelpAuthManager(lastLocation!!.latitude, lastLocation!!.longitude, applicationContext, this)
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
        fetchStations()
    }

    override fun stationsNotFound() {
        // TODO tell user that stations were not found
    }

    override fun landmarksFound(landmarks: List<Landmark>?) {
        myLandmarks = landmarks
        progressBar.visibility = ProgressBar.INVISIBLE
        initializeLandmarkList()
    }

    override fun landsmarksNotFound() {
        // TODO tell user that landmarks were not found
    }

    override fun locationFound(location: Location) {
        // remove progress bar
        locationDetector.showLoading(false, progressBar)
        // update the last location in memory
        lastLocation = location
        fetchStations()
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
