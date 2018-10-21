package net.crystalapps.mint.location.library.callbacks;

import android.location.Location;

/**
 * Created by Syed Owais Ali on 10/20/2018.
 */

public interface EasyLocationCallback {
    void onResult(Location location);
    default void onFailed(String error){}
}
