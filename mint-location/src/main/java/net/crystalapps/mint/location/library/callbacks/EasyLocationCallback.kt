package net.crystalapps.mint.location.library.callbacks

import android.location.Location

/**
 * Created by Syed Owais Ali on 10/20/2018.
 */

interface EasyLocationCallback {
    fun onResult(location: Location)
    fun onFailed(error: String) {}
}
