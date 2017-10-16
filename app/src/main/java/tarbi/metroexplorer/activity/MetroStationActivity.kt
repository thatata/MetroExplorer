package tarbi.metroexplorer.activity

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_metrostation.*
import org.jetbrains.anko.doAsync
import tarbi.metroexplorer.R
import tarbi.metroexplorer.util.FetchMetroStationsManager
import tarbi.metroexplorer.util.LocationDetector
import tarbi.metroexplorer.util.MetroStationsAdapter
import tarbi.metroexplorer.util.Station

/* Select a station from a list of ALL stations */
class MetroStationActivity : AppCompatActivity(), LocationDetector.LocationDetectorListener,
    FetchMetroStationsManager.FetchMetroListener {

    private lateinit var locationDetector : LocationDetector
    private lateinit var progressBar      : ProgressBar
    private var          lastLocation     : Location? = null
    private var          myStations       : List<Station>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metrostation)

        progressBar = findViewById(R.id.metrostationProgressBar)
        fetchStations()
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
            // once as we have a list of stations fetchStations will be called again
            val locationNow = lastLocation
            val stationManager = FetchMetroStationsManager(locationNow?.latitude,
                    locationNow?.longitude,80467.2, applicationContext, this)
            progressBar.visibility = ProgressBar.VISIBLE
            // turn off progressbar when stationList is available in callback
            doAsync {
                stationManager.getAllStations()
            }
        }

        // initialize adapter with stations
        val stationAdapter = MetroStationsAdapter(myStations, this)
        // set up recycler view
        metro_station_recycler_view.layoutManager = LinearLayoutManager(this)
        metro_station_recycler_view.adapter = stationAdapter
    }

    /* ---------------------------- Callbacks ---------------------------- */
    override fun stationsFound(stationList: List<Station>?) {
        myStations = stationList
        progressBar.visibility = ProgressBar.INVISIBLE
        fetchStations()
    }

    override fun stationsNotFound() {
        // TODO tell user that stations were not found
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
                //alertUser("Location Detection Failed","Location timed out.")
            }
            LocationDetector.FailureReason.NO_PERMISSION -> {
                //alertUser("Location Detection Failed","No location permission granted.")
            }
        }
    }
}