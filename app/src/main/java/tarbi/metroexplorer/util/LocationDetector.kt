package tarbi.metroexplorer.util

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import android.Manifest

class LocationDetector(val context : Context) : Activity() {
    // Tag
    private val TAG = "LocationDetector"

    // previous member vars
    private lateinit var locationClient : FusedLocationProviderClient
    var locationDetectorListener : LocationDetectorListener? = null
    private var locationReady : Boolean = false

    // request code for location permission request
    val ACCESS_LOCATION_REQUEST_CODE: Int = 0

    // interface to talk to Activity
    interface LocationDetectorListener {
        fun locationDetected(location : Location)
        fun locationNotDetected()
    }

    // location updates request?
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

    // runtime permissions request
    fun requestPermissions() {
        // save permission value
        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        // check if permission was granted
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

            // permission not granted, so ask if they want an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale((context as Activity), permission)) {

                // show an explanation (**REAL IMPLEMENTATION --> SHOW THIS ASYNCHRONOUSLY**)
                Log.i("Requesting Location", "Requesting your location so we can find the nearest metro location to you.")


                ActivityCompat.requestPermissions((context as Activity), Array(1, {i -> permission.toString()}), ACCESS_LOCATION_REQUEST_CODE)

            } else {

                // just request permission
                ActivityCompat.requestPermissions((context as Activity),
                        Array(1, {i -> permission.toString()}),
                        ACCESS_LOCATION_REQUEST_CODE)
            }
        }
    }
}