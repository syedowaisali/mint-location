package net.crystalapps.mint.location.library.core

import android.annotation.SuppressLint
import android.content.Context

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import net.crystalapps.mint.location.library.listeners.LocationUpdatesListener

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Created by Syed Owais Ali on 10/21/2018.
 */

internal class SubscriptionProxyInvocationHandler(context: Context, private val locationRequest: LocationRequest, private val listener: LocationUpdatesListener) : InvocationHandler {
    private val locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context.applicationContext)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult.lastLocation != null) {
                listener.onResult(locationResult.lastLocation)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun invoke(o: Any?, method: Method?, objects: Array<out Any>?): Any {

        if (method?.name == "start") {
            locationService.requestLocationUpdates(locationRequest, locationCallback, null)
        } else if (method?.name == "stop") {
            locationService.removeLocationUpdates(locationCallback)
        }

        return o!!
    }
}
