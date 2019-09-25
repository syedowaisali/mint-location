package net.crystalapps.mint.location.library.callbacks

/**
 * Created by Syed Owais Ali on 10/21/2018.
 */
interface InitializationCallback {
    fun onSuccess()
    fun onFailed(error: String) {}
    fun onPermissionDenied() {}
}
