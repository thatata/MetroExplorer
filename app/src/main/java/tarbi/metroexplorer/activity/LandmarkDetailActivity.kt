package tarbi.metroexplorer.activity

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
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
        progressBar.visibility = ProgressBar.VISIBLE

        // setup toolbar
        //setSupportActionBar(landmark_detail_toolbar)

        // get parcelized landmark
        val landmark = intent.getParcelableExtra<Landmark>("landmark")

        // set values in the view
        viewLandmarkDetails(landmark)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.landmarkdetails_menu, menu)
        val shareIcon         = menu.findItem(R.id.shareIcon)
        val shareIconURL      = "https://d30y9cdsu7xlg0.cloudfront.net/png/3893-200.png"
        val favoriteIcon      = menu.findItem(R.id.favoriteIcon)
        val favoriteIconURL   = "https://d30y9cdsu7xlg0.cloudfront.net/png/1308-200.png"
        val directionIcon     = menu.findItem(R.id.directionIcon)
        val directionIconURL  = "https://d30y9cdsu7xlg0.cloudfront.net/png/40014-200.png"

        Ion.with(this)
                .load(directionIconURL)
                .asBitmap()
                .setCallback { error, r ->
                    if (error == null && r != null) {
                        val drawMe = BitmapDrawable(this.resources, r)
                        directionIcon.icon = drawMe
                    }
                }

        Ion.with(this)
                .load(shareIconURL)
                .asBitmap()
                .setCallback { error, r ->
                    if (error == null && r != null) {
                        val drawMe = BitmapDrawable(this.resources, r)
                        shareIcon.icon = drawMe
                    }
                }

        Ion.with(this)
                .load(favoriteIconURL)
                .asBitmap()
                .setCallback { error, r ->
                    if (error == null && r != null) {
                        val drawMe = BitmapDrawable(this.resources, r)
                        favoriteIcon.icon = drawMe
                    }
                }

        return true
    }

    private fun viewLandmarkDetails(landmark: Landmark?) {
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