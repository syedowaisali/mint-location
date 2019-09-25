package net.crystalapps.mint.location.library.shadow

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient

import net.crystalapps.mint.location.library.callbacks.CurrentLocationCallback
import net.crystalapps.mint.location.library.callbacks.InitializationCallback

/**
 * Created by Syed Owais Ali on 10/13/2018.
 */

@SuppressLint("MissingPermission")
class LocationSettingFragment : Fragment() {

    var resolvableApiException: ResolvableApiException? = null
        private set
    private var locationRequest: LocationRequest? = null
    private var currentLocationCallback: CurrentLocationCallback? = null
    private var initializationCallback: InitializationCallback? = null

    override fun onSaveInstanceState(outState: Bundle) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            requestLocation()
        } else {
            val error = if (data != null)
                if (data.getStringExtra("error") != null)
                    data.getStringExtra("error")
                else
                    "failed to get location"
            else
                "failed to get location"

            if (currentLocationCallback != null) {
                currentLocationCallback!!.onFailed(error)
            } else if (initializationCallback != null) {
                initializationCallback!!.onFailed(error)
            }
            removeFragment()
        }
    }

    fun requestLocation() {

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        val client = LocationServices.getSettingsClient(activity!!.applicationContext)
        client.checkLocationSettings(builder.build())
                .addOnSuccessListener { locationSettingsResponse -> getLocation() }
                .addOnFailureListener { e ->
                    if (e is ResolvableApiException) {
                        this.resolvableApiException = e
                        val intent = Intent(activity, ShadowLocationActivity::class.java)
                        startActivityForResult(intent, REQUEST_CHECK_SETTINGS)
                    } else {
                        if (currentLocationCallback != null) {
                            currentLocationCallback!!.onFailed(e.message!!)
                        } else if (initializationCallback != null) {
                            initializationCallback!!.onFailed(e.message!!)
                        }
                        removeFragment()
                    }
                }
    }

    private fun getLocation() {

        if (currentLocationCallback != null) {
            val locationService = LocationServices.getFusedLocationProviderClient(activity!!.applicationContext)
            locationService.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    if (locationResult!!.lastLocation != null) {
                        currentLocationCallback!!.onResult(locationResult.lastLocation)
                        locationService.removeLocationUpdates(this)
                        removeFragment()
                    }
                }
            }, null)
        } else if (initializationCallback != null) {
            initializationCallback!!.onSuccess()
            removeFragment()
        }
    }

    private fun removeFragment() {

        try {
            val fragmentManager = fragmentManager
            fragmentManager?.beginTransaction()?.remove(this)?.commit()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    companion object {

        var instance: LocationSettingFragment? = null
            private set

        private val REQUEST_CHECK_SETTINGS = 2016

        fun newInstance(locationRequest: LocationRequest, currentLocationCallback: CurrentLocationCallback): LocationSettingFragment {
            instance = LocationSettingFragment()
            val fragment = instance
            fragment!!.locationRequest = locationRequest
            fragment.currentLocationCallback = currentLocationCallback
            return fragment
        }

        fun newInstance(locationRequest: LocationRequest, initializationCallback: InitializationCallback): LocationSettingFragment {
            instance = LocationSettingFragment()
            val fragment = instance
            fragment!!.locationRequest = locationRequest
            fragment.initializationCallback = initializationCallback
            return fragment
        }
    }
}