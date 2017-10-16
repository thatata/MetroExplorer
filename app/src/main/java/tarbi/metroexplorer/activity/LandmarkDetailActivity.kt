package tarbi.metroexplorer.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ProgressBar
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_landmark_details.*
import tarbi.metroexplorer.R
import tarbi.metroexplorer.util.Landmark

class LandmarkDetailActivity : AppCompatActivity() {

    private lateinit var progressBar      : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set content view
        setContentView(R.layout.activity_landmark_details)

        progressBar = findViewById(R.id.landmarkdetailProgressBar)

        // setup toolbar
        //setSupportActionBar(landmark_detail_toolbar)

        // get parcelized landmark
        val landmark = intent.getParcelableExtra<Landmark>("landmark")

        // set values in the view
        viewLandmarkDetails(landmark)
    }

    private fun viewLandmarkDetails(landmark: Landmark?) {
        progressBar.visibility = ProgressBar.VISIBLE
        // set text details
        landmarkName.text = landmark?.name
        landmarkAddress.text = landmark?.address
        landmarkDistance.text = "${landmark?.distance} meters"

        // use Ion to load image, if url exists
        if (landmark?.imageUrl != null && landmark.imageUrl.isNotEmpty()) {
            Ion.with(landmarkImage).load(landmark.imageUrl).setCallback { error, _ ->
                if (error != null) {
                    //fail - just display empty bitmap
                    landmarkImage.setImageBitmap(null)
                }
                progressBar.visibility = ProgressBar.INVISIBLE
                // otherwise, successful so do nothing
            }
        }
    }
}