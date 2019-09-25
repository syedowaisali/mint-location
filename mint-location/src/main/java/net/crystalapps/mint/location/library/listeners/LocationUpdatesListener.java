package net.crystalapps.mint.location.library.listeners;

import android.location.Location;

import androidx.annotation.NonNull;

/**
 * Created by Syed Owais Ali on 10/21/2018.
 */

@FunctionalInterface
public interface LocationUpdatesListener {
    void onResult(@NonNull Location location);
}
