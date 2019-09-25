package net.crystalapps.mint.location.library.config;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import net.crystalapps.mint.location.library.defaults.MintLocationPermissionHandler;
import net.crystalapps.mint.location.library.handlers.LocationPermissionHandler;

/**
 * Created by Syed Owais Ali on 10/21/2018.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class EasyLocationConfig {

    private static EasyLocationConfig INSTANCE;

    @NonNull
    private LocationPermissionHandler<FragmentActivity> locationPermissionHandler;

    public static EasyLocationConfig getInstance() {
        return INSTANCE == null ? INSTANCE = new EasyLocationConfig() : INSTANCE;
    }

    public EasyLocationConfig() {
        locationPermissionHandler = new MintLocationPermissionHandler();
    }

    public void setLocationPermissionHandler(@NonNull LocationPermissionHandler<FragmentActivity> locationPermissionHandler) {
        this.locationPermissionHandler = locationPermissionHandler;
    }

    @NonNull
    public LocationPermissionHandler<FragmentActivity> getLocationPermissionHandler() {
        return locationPermissionHandler;
    }
}