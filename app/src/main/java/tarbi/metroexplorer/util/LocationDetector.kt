package tarbi.metroexplorer.util

import android.content.Context
import android.location.Location
import android.nfc.Tag
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener

class LocationDetector(val context : Context) {
    private val TAG = "LocationDetector"
    private lateinit var client : FusedLocationProviderClient
    var locationDetectorListener : LocationDetectorListener? = null

    interface LocationDetectorListener {
        fun locationDetected(location : Location)
        fun locationNotDetected()
    }

    fun getLocation() {
        Log.d("getting location", "getting location")
        client = LocationServices.getFusedLocationProviderClient(context)

        //client.lastLocation?.let {}

    }
}