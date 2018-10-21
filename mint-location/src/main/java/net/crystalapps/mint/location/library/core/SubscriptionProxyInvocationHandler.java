package net.crystalapps.mint.location.library.core;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import net.crystalapps.mint.location.library.listeners.LocationUpdatesListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by Syed Owais Ali on 10/21/2018.
 */

@SuppressWarnings("MissingPermission")
class SubscriptionProxyInvocationHandler implements InvocationHandler {


    @NonNull private final LocationRequest locationRequest;
    @NonNull private final LocationUpdatesListener listener;
    @NonNull private final FusedLocationProviderClient locationService;

    SubscriptionProxyInvocationHandler(@NonNull Context context, @NonNull LocationRequest locationRequest, @NonNull LocationUpdatesListener listener) {

        this.locationRequest = locationRequest;
        this.listener = listener;

        locationService = LocationServices.getFusedLocationProviderClient(context.getApplicationContext());
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) {

        if (method.getName().equals("start")) {
            locationService.requestLocationUpdates(locationRequest, locationCallback, null);
        }

        else if (method.getName().equals("stop")) {
            locationService.removeLocationUpdates(locationCallback);
        }

        return null;
    }

    private final LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult.getLastLocation() != null) {
                listener.onResult(locationResult.getLastLocation());
            }
        }
    };
}
