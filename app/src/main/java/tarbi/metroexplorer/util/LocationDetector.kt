package tarbi.metroexplorer.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.content.ContextCompat
import com.google.android.gms.location.*
import java.util.*
import kotlin.concurrent.timerTask

class LocationDetector(val context : Context) {
    // member vars for Fused Location Provider and "listener" interface
    private val locationClient : FusedLocationProviderClient
    var locationDetectorListener : LocationDetectorListener? = null

    init {
        // initialize fused location provider client
        locationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    // interface to talk to Activity
    interface LocationDetectorListener {
        fun locationFound(location : Location)
        fun locationNotFound(reason : FailureReason)
    }

    // enum to describe reasons why location wasn't found
    enum class FailureReason {
        TIMEOUT,
        NO_PERMISSION
    }

    // function to handle location request
    fun getLocation() {
        // check if location permission has been granted
        val permissionResult = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)

        // if permission granted, get location
        if (permissionResult == PackageManager.PERMISSION_GRANTED) {
            // create location request
            val locationRequest = LocationRequest()
            locationRequest.interval = 0L

            // create timer to timeout if location cannot be found
            val timer = Timer()

            // create location callback when location is found
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    // stop location updates
                    locationClient.removeLocationUpdates(this)

                    //cancel timer
                    timer.cancel()

                    // fire callback with the latest location
                    locationDetectorListener?.locationFound(locationResult.locations.first())
                }
            }

            // schedule timer to remove location updates if it times out
            timer.schedule(timerTask {
                // if timer expires, stop location updates and fire callback
                locationClient.removeLocationUpdates(locationCallback)
                locationDetectorListener?.locationNotFound(FailureReason.TIMEOUT)

            }, 10000) // 10 second timer

            // start location updates with request and callback
            locationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        } else {
            // otherwise, call locationNotFound with NO_PERMISSION failure reason
            locationDetectorListener?.locationNotFound(FailureReason.NO_PERMISSION)
        }
    }
}