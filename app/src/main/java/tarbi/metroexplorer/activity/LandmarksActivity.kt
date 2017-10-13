package tarbi.metroexplorer.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import tarbi.metroexplorer.R
import tarbi.metroexplorer.util.FetchMetroStationsManager
import tarbi.metroexplorer.util.Station

/*
 * Created by hobbes on 9/26/17.
 */
class LandmarksActivity : AppCompatActivity() {

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_landmarks)

         /* Use FetchMetroStationsManager to get closest station */
         val stationManager = FetchMetroStationsManager(38.8978168, -77.0404246, 500.0,
                 applicationContext)
         val nearestStation: Station? = stationManager.getNearestStation()
    }
}