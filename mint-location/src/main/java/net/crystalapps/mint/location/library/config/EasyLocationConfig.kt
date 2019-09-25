package net.crystalapps.mint.location.library.config

import androidx.fragment.app.FragmentActivity

import net.crystalapps.mint.location.library.defaults.MintLocationPermissionHandler
import net.crystalapps.mint.location.library.handlers.LocationPermissionHandler

/**
 * Created by Syed Owais Ali on 10/21/2018.
 */

class EasyLocationConfig {

    var locationPermissionHandler: LocationPermissionHandler<FragmentActivity> = MintLocationPermissionHandler()

    companion object {

        private var INSTANCE: EasyLocationConfig? = null

        val instance: EasyLocationConfig
            get() {
                if (INSTANCE == null) {
                    INSTANCE = EasyLocationConfig()
                }
                return INSTANCE!!
            }

    }
}