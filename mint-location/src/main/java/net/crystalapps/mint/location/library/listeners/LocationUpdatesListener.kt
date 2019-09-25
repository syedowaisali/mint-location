package net.crystalapps.mint.location.library.listeners

import android.location.Location

/**
 * Created by Syed Owais Ali on 10/21/2018.
 */

@FunctionalInterface
interface LocationUpdatesListener {
    fun onResult(location: Location)
}
