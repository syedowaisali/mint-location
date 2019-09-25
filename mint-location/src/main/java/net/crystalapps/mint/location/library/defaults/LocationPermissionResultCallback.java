package net.crystalapps.mint.location.library.defaults;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.LocationRequest;

import net.crystalapps.mint.location.library.shadow.LocationSettingFragment;
import net.crystalapps.mint.location.library.callbacks.CurrentLocationCallback;
import net.crystalapps.mint.location.library.callbacks.InitializationCallback;
import net.crystalapps.mint.location.library.config.EasyLocationConfig;
import net.crystalapps.permission.runtime.library.callbacks.PermissionCallback;
import net.crystalapps.permission.runtime.library.callbacks.PermissionSettingCallback;
import net.crystalapps.permission.runtime.library.callbacks.SettingOpener;
import net.crystalapps.permission.runtime.library.core.RuntimePermission;
import net.crystalapps.permission.runtime.library.models.Perm;

/**
 * Created by Syed Owais Ali on 10/21/2018.
 */

@SuppressWarnings("ConstantConditions")
public class LocationPermissionResultCallback implements PermissionCallback<FragmentActivity>, SettingOpener<FragmentActivity> {

    @NonNull private final LocationRequest locationRequest;
    @Nullable private final CurrentLocationCallback currentLocationCallback;
    @Nullable private final InitializationCallback initializationCallback;

    public LocationPermissionResultCallback(@NonNull LocationRequest locationRequest, @NonNull CurrentLocationCallback currentLocationCallback) {
        this.locationRequest = locationRequest;
        this.currentLocationCallback = currentLocationCallback;
        this.initializationCallback = null;
    }

    public LocationPermissionResultCallback(@NonNull LocationRequest locationRequest, @NonNull InitializationCallback initializationCallback) {
        this.locationRequest = locationRequest;
        this.initializationCallback = initializationCallback;
        currentLocationCallback = null;
    }

    @Override
    public void onGranted(Perm<FragmentActivity> perm) {
        if (currentLocationCallback != null) {
            requestCurrentLocation(perm);
        }
        else if (initializationCallback != null) {
            requestLocationUpdates(perm);
        }

    }

    @Override
    public void onDenied(Perm<FragmentActivity> perm) {
        if (currentLocationCallback != null) {
            currentLocationCallback.onPermissionDenied();
        }
        else if (initializationCallback != null) {
            initializationCallback.onPermissionDenied();
        }
    }

    @Override
    public void onPermanentDenied(Perm<FragmentActivity> perm) {

        EasyLocationConfig
                .getInstance()
                .getLocationPermissionHandler()
                .handlePermanentDenied(perm, this);
    }

    @Override
    public void open(@NonNull FragmentActivity caller, @NonNull String permission) {
        RuntimePermission.openAppSettingIntent(caller, permission, permissionSettingCallback);
    }

    @Override
    public void doNothing(@NonNull FragmentActivity caller, @NonNull String permission) {
        if (currentLocationCallback != null) {
            currentLocationCallback.onPermissionDenied();
        }
        else if (initializationCallback != null) {
            initializationCallback.onPermissionDenied();
        }
    }

    private final PermissionSettingCallback<FragmentActivity> permissionSettingCallback = new PermissionSettingCallback<FragmentActivity>() {

        @Override
        public void onGranted(@NonNull Perm<FragmentActivity> perm) {
            if (currentLocationCallback != null) {
                requestCurrentLocation(perm);
            }
            else if (initializationCallback != null) {
                requestLocationUpdates(perm);
            }
        }

        @Override
        public void onDenied(@NonNull Perm<FragmentActivity> perm) {
            if (currentLocationCallback != null) {
                currentLocationCallback.onPermissionDenied();
            }
            else if (initializationCallback != null) {
                initializationCallback.onPermissionDenied();
            }
        }
    };

    private void requestCurrentLocation(@NonNull Perm<FragmentActivity> perm) {
        FragmentManager fragmentManager = perm.getCaller().getSupportFragmentManager();
        LocationSettingFragment fragment = LocationSettingFragment.newInstance(locationRequest, currentLocationCallback);
        fragmentManager.beginTransaction().add(fragment, "LOCATION_SETTING_FRAGMENT").commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();

        fragment.requestLocation();
    }

    private void requestLocationUpdates(@NonNull Perm<FragmentActivity> perm) {
        FragmentManager fragmentManager = perm.getCaller().getSupportFragmentManager();
        LocationSettingFragment fragment = LocationSettingFragment.newInstance(locationRequest, initializationCallback);
        fragmentManager.beginTransaction().add(fragment, "LOCATION_SETTING_FRAGMENT").commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();

        fragment.requestLocation();
    }
}
