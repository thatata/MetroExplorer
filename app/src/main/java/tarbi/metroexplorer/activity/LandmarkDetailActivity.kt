package tarbi.metroexplorer.activity

import android.graphics.drawable.BitmapDrawable
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.widget.ProgressBar
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_landmark_details.*
import tarbi.metroexplorer.R
import tarbi.metroexplorer.util.Landmark
import tarbi.metroexplorer.util.PersistanceManager

class LandmarkDetailActivity : AppCompatActivity() {

    private lateinit var progressBar : ProgressBar
    private lateinit var landmark    : Landmark
    private lateinit var persistanceManager : PersistanceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set content view
        setContentView(R.layout.activity_landmark_details)

        progressBar = findViewById(R.id.landmarkdetailProgressBar)
        progressBar.visibility = ProgressBar.VISIBLE

        // initialize persistance manager
        persistanceManager = PersistanceManager(this)

        // setup toolbar
        //setSupportActionBar(landmark_detail_toolbar)

        // get parcelized landmark
        landmark = intent.getParcelableExtra<Landmark>("landmark")

        // set values in the view
        viewLandmarkDetails(landmark)
    }

    fun shareLandmark(): Boolean {
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

        return true
    }

    fun getDirections(): Boolean {
        // explicit intent to launch walking directions
        if (landmark?.address != null) {
            val uriString: String = "google.navigation:q=${landmark.address.replace(" ", "+", false)}&mode=w"
            val intentUri: Uri = Uri.parse(uriString)

            val intent = Intent(Intent.ACTION_VIEW, intentUri)
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)

        }

        return true
    }

    fun saveToFavorites(): Boolean {
        persistanceManager.saveFavorite(landmark)
        return true
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
        // Set menu item action
        directionIcon.setOnMenuItemClickListener {
            getDirections()
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
        // Set menu item action
        shareIcon.setOnMenuItemClickListener {
            shareLandmark()
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
        // Set menu item action
        favoriteIcon.setOnMenuItemClickListener {
            saveToFavorites()
        }

        return true
    }

    private fun viewLandmarkDetails(landmark: Landmark?) {
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
