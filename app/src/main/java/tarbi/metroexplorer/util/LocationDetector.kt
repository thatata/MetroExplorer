package tarbi.metroexplorer.util

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.nfc.Tag
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import tarbi.metroexplorer.activity.LandmarksActivity
import java.lang.Exception

class LocationDetector(val context : Context) {
    private val TAG = "LocationDetector"
    private lateinit var locationClient : FusedLocationProviderClient
    var locationDetectorListener : LocationDetectorListener? = null
    private var locationReady : Boolean = false

    interface LocationDetectorListener {
        fun locationDetected(location : Location)
        fun locationNotDetected()
    }

    fun getLocation() {
        Log.d("getting location", "getting location")

        checkPermissions()
    }

    private fun checkPermissions() {
        // create location request
        var locationRequest : LocationRequest = createLocationRequest()

        // get current location settings of the device
        var settingsRequestBuilder : LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        // check if current location settings are satisfied
        var settingsClient : SettingsClient = LocationServices.getSettingsClient(context)
        var settingsResponseTask : Task<LocationSettingsResponse> = settingsClient.checkLocationSettings(settingsRequestBuilder.build())

        // add success listener
        settingsResponseTask.addOnSuccessListener {
            // settings are satisfied
            // locationReady = true

            Log.d("message", "location settings satisfied")
        }

        // add failure listener
        settingsResponseTask.addOnFailureListener {
            // settings are not satisifed
            Log.d("message", "location settings NOT satisfied")

            // check status code of the exception
            when ((it as ApiException).statusCode) {
                CommonStatusCodes.RESOLUTION_REQUIRED -> {
                    // location settings are not satisfied, but this can be fixed by showing user a dialog
                    try {
                        // show dialog
                        var resolvable : ResolvableApiException = it as ResolvableApiException
                        resolvable.startResolutionForResult(context as Activity, 0x1)

                        // if (resolvable.statusCode == 0x1) locationReady = true

                    } catch (e : IntentSender.SendIntentException) {
                        // ignore error
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    // can't change settings, so do nothing
                }
            }
        }
    }

    private fun createLocationRequest(): LocationRequest {
        // create location request
        var locationRequest : LocationRequest = LocationRequest()

        // set attributes (to display location in real time)
        locationRequest.apply {
            setInterval(10000)
            setFastestInterval(5000)
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        }
        return locationRequest
    }
}