package net.crystalapps.mint.location.library.core;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import net.crystalapps.mint.location.library.defaults.LocationPermissionResultCallback;
import net.crystalapps.mint.location.library.callbacks.CurrentLocationCallback;
import net.crystalapps.mint.location.library.callbacks.EasyLocationCallback;
import net.crystalapps.mint.location.library.callbacks.InitializationCallback;
import net.crystalapps.mint.location.library.listeners.LocationUpdatesListener;
import net.crystalapps.permission.runtime.library.core.RuntimePermission;

import java.lang.reflect.Proxy;

/**
 * Created by Syed Owais Ali on 10/20/2018.
 */

@SuppressWarnings({"WeakerAccess", "MissingPermission", "unused"})
public class EasyLocation {

    /*--------------------------------------------*/
    // REQUEST CURRENT LOCATION
    /*--------------------------------------------*/

    public static <T extends FragmentActivity> void requestCurrentLocation(@NonNull T caller, CurrentLocationCallback callback) {
        requestCurrentLocation(caller, getDefaultLocationRequest(), callback);
    }

    public static <T extends FragmentActivity> void requestCurrentLocation(@NonNull T caller, @NonNull LocationRequest locationRequest, @NonNull CurrentLocationCallback callback) {
        RuntimePermission.requestPermission(caller, Manifest.permission.ACCESS_FINE_LOCATION, new LocationPermissionResultCallback(locationRequest, callback));
    }

    public static void requestCurrentLocation(@NonNull Context context, @NonNull EasyLocationCallback callback) {
        requestCurrentLocation(context, getDefaultLocationRequest(), callback);
    }

    public static void requestCurrentLocation(@NonNull Context context, @NonNull LocationRequest locationRequest, @NonNull EasyLocationCallback callback) {

        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback.onFailed("Location permission not found");
            return;
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(context.getApplicationContext());
        client.checkLocationSettings(builder.build())
                .addOnSuccessListener(locationSettingsResponse -> {
                    FusedLocationProviderClient locationService = LocationServices.getFusedLocationProviderClient(context.getApplicationContext());
                    locationService.requestLocationUpdates(locationRequest, new LocationCallback(){
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult.getLastLocation() != null) {
                                callback.onResult(locationResult.getLastLocation());
                                locationService.removeLocationUpdates(this);
                            }
                        }
                    }, null);
                })
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));

    }

    /*--------------------------------------------*/
    // REQUEST LOCATION UPDATES
    /*--------------------------------------------*/

    public static <T extends FragmentActivity> void init(@NonNull T caller, @NonNull InitializationCallback callback){
        RuntimePermission.requestPermission(caller, Manifest.permission.ACCESS_FINE_LOCATION, new LocationPermissionResultCallback(getDefaultLocationRequest(), callback));
    }

    public static Subscription subscribeForUpdates(@NonNull Context context, @NonNull LocationUpdatesListener listener) {
        return subscribeForUpdates(context, getDefaultLocationRequest(), listener);
    }

    public static Subscription subscribeForUpdates(@NonNull Context context, @NonNull LocationRequest locationRequest, @NonNull LocationUpdatesListener listener) {
        return (Subscription) Proxy.newProxyInstance(Subscription.class.getClassLoader(), new Class[]{Subscription.class}, new SubscriptionProxyInvocationHandler(context, locationRequest, listener));
    }

    /*--------------------------------------------*/
    // HELPERS
    /*--------------------------------------------*/

    public static LocationRequest getDefaultLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}