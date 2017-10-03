package tarbi.metroexplorer.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import tarbi.metroexplorer.R

/* This is the main activity where the application starts */

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val button1 = findViewById<TextView>(R.id.button1)
        val button2 = findViewById<TextView>(R.id.button2)
        val button3 = findViewById<TextView>(R.id.button3)

        button1.setOnClickListener {
            val intent = Intent(this@MenuActivity, LandmarksActivity::class.java)
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
    }
}
