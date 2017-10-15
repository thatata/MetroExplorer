package tarbi.metroexplorer.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_metrostation.*
import tarbi.metroexplorer.R
import tarbi.metroexplorer.util.MetroStationsAdapter

/**
 * Created by hobbes on 9/26/17.
 */
class MetroStationActivity : AppCompatActivity() {

    private lateinit var stationAdapter : MetroStationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metrostation)

        /*           Just need a getStations method to return list of stations               */

        // initialize adapter with stations
        // var stations : List<Station> = getStations() OR SOMETHING LIKE THIS
        // stationAdapter = MetroStationsAdapter(stations)

        // set up recycler view
        metro_station_recycler_view.layoutManager = LinearLayoutManager(this)
        // metro_station_recycler_view.adapter = stationAdapter
    }
}