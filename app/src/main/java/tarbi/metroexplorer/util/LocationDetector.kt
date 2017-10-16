package tarbi.metroexplorer.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.content.ContextCompat
import android.widget.ProgressBar
import com.google.android.gms.location.*
import java.util.*
import kotlin.concurrent.timerTask

class LocationDetector(private val context : Context, val locationDetectorListener: LocationDetectorListener) {

    // member vars for Fused Location Provider and "listener" interface
    private val locationClient : FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

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

    fun detectLocation(progressBar: ProgressBar) {
        // show progress bar indicating "loading"
        showLoading(true, progressBar)
        locate()
    }

    fun showLoading(show : Boolean, progressBar: ProgressBar) {
        // function to show or remove progress bar
        if (show) {
            // set visibility to visible
            progressBar.visibility = ProgressBar.VISIBLE
        } else {
            // set visibility to invisible
            progressBar.visibility = ProgressBar.INVISIBLE
        }
    }

    // function to handle location request
    private fun locate() {
        val permissionResult = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionResult == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest()
            locationRequest.interval = 0L
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

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
                    locationDetectorListener.locationFound(locationResult.locations.first())
                }
            }

            // schedule timer to remove location updates if it times out
            timer.schedule(timerTask {
                // if timer expires, stop location updates and fire callback
                locationClient.removeLocationUpdates(locationCallback)
                locationDetectorListener.locationNotFound(FailureReason.TIMEOUT)

            }, 10000) // 10 second timer

            // start location updates with request and callback
            locationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        } else {
            // otherwise, call locationNotFound with NO_PERMISSION failure reason
            locationDetectorListener.locationNotFound(FailureReason.NO_PERMISSION)
        }
    }
}