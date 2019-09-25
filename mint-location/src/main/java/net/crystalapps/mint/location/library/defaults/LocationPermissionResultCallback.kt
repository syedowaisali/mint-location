package net.crystalapps.mint.location.library.defaults


import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

import com.google.android.gms.location.LocationRequest

import net.crystalapps.mint.location.library.shadow.LocationSettingFragment
import net.crystalapps.mint.location.library.callbacks.CurrentLocationCallback
import net.crystalapps.mint.location.library.callbacks.InitializationCallback
import net.crystalapps.mint.location.library.config.EasyLocationConfig
import net.crystalapps.permission.runtime.library.callbacks.PermissionCallback
import net.crystalapps.permission.runtime.library.callbacks.PermissionSettingCallback
import net.crystalapps.permission.runtime.library.callbacks.SettingOpener
import net.crystalapps.permission.runtime.library.core.RuntimePermission
import net.crystalapps.permission.runtime.library.models.Perm

/**
 * Created by Syed Owais Ali on 10/21/2018.
 */

class LocationPermissionResultCallback : PermissionCallback<FragmentActivity>, SettingOpener<FragmentActivity> {

    private val locationRequest: LocationRequest
    private var currentLocationCallback: CurrentLocationCallback? = null
    private var initializationCallback: InitializationCallback? = null

    private val permissionSettingCallback = object : PermissionSettingCallback<FragmentActivity> {

        override fun onGranted(perm: Perm<FragmentActivity>) {
            if (currentLocationCallback != null) {
                requestCurrentLocation(perm)
            } else if (initializationCallback != null) {
                requestLocationUpdates(perm)
            }
        }

        override fun onDenied(perm: Perm<FragmentActivity>) {
            currentLocationCallback?.onPermissionDenied()
                    ?: initializationCallback?.onPermissionDenied()
        }
    }

    constructor(locationRequest: LocationRequest, currentLocationCallback: CurrentLocationCallback) {
        this.locationRequest = locationRequest
        this.currentLocationCallback = currentLocationCallback
        this.initializationCallback = null
    }

    constructor(locationRequest: LocationRequest, initializationCallback: InitializationCallback) {
        this.locationRequest = locationRequest
        this.initializationCallback = initializationCallback
        currentLocationCallback = null
    }

    override fun onGranted(perm: Perm<FragmentActivity>) {
        if (currentLocationCallback != null) {
            requestCurrentLocation(perm)
        } else if (initializationCallback != null) {
            requestLocationUpdates(perm)
        }

    }

    override fun onDenied(perm: Perm<FragmentActivity>) {
        currentLocationCallback?.onPermissionDenied()
                ?: initializationCallback?.onPermissionDenied()
    }

    override fun onPermanentDenied(perm: Perm<FragmentActivity>) {

        EasyLocationConfig.instance
                .locationPermissionHandler
                .handlePermanentDenied(perm, this)
    }

    override fun open(caller: FragmentActivity, permission: String) {
        RuntimePermission.openAppSettingIntent(caller, permission, permissionSettingCallback)
    }

    override fun doNothing(caller: FragmentActivity, permission: String) {
        currentLocationCallback?.onPermissionDenied()
                ?: initializationCallback?.onPermissionDenied()
    }

    private fun requestCurrentLocation(perm: Perm<FragmentActivity>) {
        val fragmentManager = perm.caller.supportFragmentManager
        val fragment = LocationSettingFragment.newInstance(locationRequest, currentLocationCallback!!)
        fragmentManager.beginTransaction().add(fragment, "LOCATION_SETTING_FRAGMENT").commitAllowingStateLoss()
        fragmentManager.executePendingTransactions()

        fragment.requestLocation()
    }

    private fun requestLocationUpdates(perm: Perm<FragmentActivity>) {
        val fragmentManager = perm.caller.supportFragmentManager
        val fragment = LocationSettingFragment.newInstance(locationRequest, initializationCallback!!)
        fragmentManager.beginTransaction().add(fragment, "LOCATION_SETTING_FRAGMENT").commitAllowingStateLoss()
        fragmentManager.executePendingTransactions()

        fragment.requestLocation()
    }
}
