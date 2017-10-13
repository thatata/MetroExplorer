package tarbi.metroexplorer.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import tarbi.metroexplorer.R

/* This is the main activity where the application starts */

class MenuActivity : AppCompatActivity() {

    // request code for location permission request
    val ACCESS_LOCATION_REQUEST_CODE: Int = 415

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val button1 = findViewById<TextView>(R.id.button1)
        val button2 = findViewById<TextView>(R.id.button2)
        val button3 = findViewById<TextView>(R.id.button3)

        button1.setOnClickListener {
            val intent = Intent(this@MenuActivity, LandmarksActivity::class.java)

            // put extra flag attribute to find nearest location
            intent.putExtra("findLocation", true)
            startActivity(intent)
        }

        button2.setOnClickListener {
            val intent = Intent(this@MenuActivity, MetroStationActivity::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener {
            val intent = Intent(this@MenuActivity, LandmarksActivity::class.java)
            startActivity(intent)
        }

        requestPermissionsIfNecessary()
    }

    private fun requestPermissionsIfNecessary() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        // check if permission was granted
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted, so request permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), ACCESS_LOCATION_REQUEST_CODE)

        } // otherwise permission was granted, so no need to request it
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // check request code of permission result
        if(requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            // if request is cancelled, request it again! (until "don't ask again")
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermissionsIfNecessary()
            }
        }
    }
}
