package tarbi.metroexplorer.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_landmark_details.*
import tarbi.metroexplorer.R
import tarbi.metroexplorer.util.Landmark

/**
 * Created by hobbes on 9/26/17.
 */
class LandmarkDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set content view
        setContentView(R.layout.activity_landmark_details)

        // setup toolbar
        setSupportActionBar(landmark_detail_toolbar)

        // get parcelized landmark
        val landmark = intent.getParcelableExtra<Landmark>("landmark")

        // set values in the view
        viewLandmarkDetails(landmark)
    }

    private fun viewLandmarkDetails(landmark: Landmark?) {
        // set text details
        landmarkName.text = landmark?.name
        landmarkAddress.text = landmark?.address
        landmarkDistance.text = "${landmark?.distance} meters"

        // use Ion to load image, if url exists
        if (landmark?.imageUrl != null && landmark.imageUrl.isNotEmpty()) {
            Ion.with(landmarkImage).load(landmark.imageUrl).setCallback { error, result ->
                if(error != null) {
                    //fail - just display empty bitmap
                    landmarkImage.setImageBitmap(null)
                }
                // otherwise, successful so do nothing
            }
        }
    }
}