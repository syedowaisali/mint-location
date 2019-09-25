package net.crystalapps.mint.location.library.core

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.*
import net.crystalapps.mint.location.library.callbacks.CurrentLocationCallback
import net.crystalapps.mint.location.library.callbacks.EasyLocationCallback
import net.crystalapps.mint.location.library.callbacks.InitializationCallback
import net.crystalapps.mint.location.library.defaults.LocationPermissionResultCallback
import net.crystalapps.mint.location.library.listeners.LocationUpdatesListener
import net.crystalapps.permission.runtime.library.core.RuntimePermission
import java.lang.reflect.Proxy

/**
 * Created by Syed Owais Ali on 10/20/2018.
 */

object EasyLocation {

    /*--------------------------------------------*/
    // HELPERS
    /*--------------------------------------------*/

    val defaultLocationRequest: LocationRequest
        get() {
            val locationRequest = LocationRequest()
            locationRequest.interval = 10000
            locationRequest.fastestInterval = 5000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            return locationRequest
        }

    /*--------------------------------------------*/
    // REQUEST CURRENT LOCATION
    /*--------------------------------------------*/

    fun <T : FragmentActivity> requestCurrentLocation(caller: T, callback: CurrentLocationCallback) {
        requestCurrentLocation(caller, defaultLocationRequest, callback)
    }

    fun <T : FragmentActivity> requestCurrentLocation(caller: T, locationRequest: LocationRequest, callback: CurrentLocationCallback) {
        RuntimePermission.requestPermission(caller, Manifest.permission.ACCESS_FINE_LOCATION, LocationPermissionResultCallback(locationRequest, callback))
    }

    fun requestCurrentLocation(context: Context, callback: EasyLocationCallback) {
        requestCurrentLocation(context, defaultLocationRequest, callback)
    }

    @SuppressLint("MissingPermission")
    fun requestCurrentLocation(context: Context, locationRequest: LocationRequest, callback: EasyLocationCallback) {

        if (ActivityCompat.checkSelfPermission(context.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback.onFailed("Location permission not found")
            return
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(context.applicationContext)
        client.checkLocationSettings(builder.build())
                .addOnSuccessListener { locationSettingsResponse ->
                    val locationService = LocationServices.getFusedLocationProviderClient(context.applicationContext)
                    locationService.requestLocationUpdates(locationRequest, object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            if (locationResult!!.lastLocation != null) {
                                callback.onResult(locationResult.lastLocation)
                                locationService.removeLocationUpdates(this)
                            }
                        }
                    }, null)
                }
                .addOnFailureListener { e -> callback.onFailed(e.message!!) }

    }

    /*--------------------------------------------*/
    // REQUEST LOCATION UPDATES
    /*--------------------------------------------*/

    fun <T : FragmentActivity> init(caller: T, callback: InitializationCallback) {
        RuntimePermission.requestPermission(caller, Manifest.permission.ACCESS_FINE_LOCATION, LocationPermissionResultCallback(defaultLocationRequest, callback))
    }

    fun subscribeForUpdates(context: Context, listener: LocationUpdatesListener): Subscription {
        return subscribeForUpdates(context, defaultLocationRequest, listener)
    }

    fun subscribeForUpdates(context: Context, locationRequest: LocationRequest, listener: LocationUpdatesListener): Subscription {
        return Proxy.newProxyInstance(Subscription::class.java.classLoader, arrayOf(Subscription::class.java), SubscriptionProxyInvocationHandler(context, locationRequest, listener)) as Subscription
    }
}