package tarbi.metroexplorer.activity

import android.content.Intent
import android.net.Uri
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

        // testing
        shareLandmark(landmark)
    }

    fun shareLandmark(landmark: Landmark?) {
        // implicit intent to share
        if (landmark != null) {
            // create intent and set action to ACTION_SEND
            val intent = Intent()
            intent.setAction(Intent.ACTION_SEND)

            // construct the message to attach
            val message : String = "${resources.getString(R.string.checkout_text)} ${landmark.name} ${resources.getString(R.string.at_address)} ${landmark.address}"

            // put extra and set plain text
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.setType("text/plain")

            // start activity
            startActivity(intent)
        }
    }

    fun getDirections(landmark: Landmark?) {
        // explicit intent to launch walking directions
        if (landmark?.address != null) {
            val uriString: String = "google.navigation:q=${landmark.address.replace(" ", "+", false)}&mode=w"
            val intentUri: Uri = Uri.parse(uriString)

            val intent = Intent(Intent.ACTION_VIEW, intentUri)
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }
    }

    private fun viewLandmarkDetails(landmark: Landmark?) {
        progressBar.visibility = ProgressBar.VISIBLE
        // set text details
        landmarkName.text = landmark?.name
        landmarkAddress.text = landmark?.address

        // convert distance to miles
        if (landmark?.distance != null) {
            // convert meters to miles
            val distanceInMiles : Double = landmark.distance * 0.000621371

            // present on screen
            landmarkDistance.text = "${getResources().getString(R.string.distance_with_colon)} $distanceInMiles ${getResources().getString(R.string.miles)}"
        }

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