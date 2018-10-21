package net.crystalapps.mint.location.library.shadow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import net.crystalapps.mint.location.library.callbacks.CurrentLocationCallback;
import net.crystalapps.mint.location.library.callbacks.InitializationCallback;

/**
 * Created by Syed Owais Ali on 10/13/2018.
 */

@SuppressLint("MissingPermission")
public class LocationSettingFragment extends Fragment {

    private static LocationSettingFragment INSTANCE;
    private static final int REQUEST_CHECK_SETTINGS = 2016;

    private ResolvableApiException e;
    private LocationRequest locationRequest;
    private CurrentLocationCallback currentLocationCallback;
    private InitializationCallback initializationCallback;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            requestLocation();
        }
        else {
            String error = data != null
                    ? data.getStringExtra("error") != null
                        ? data.getStringExtra("error")
                        : "failed to get location"
                    : "failed to get location";

            if (currentLocationCallback != null) {
                currentLocationCallback.onFailed(error);
            }
            else if (initializationCallback != null) {
                initializationCallback.onFailed(error);
            }
            removeFragment();
        }
    }

    public static LocationSettingFragment newInstance(@NonNull LocationRequest locationRequest, @NonNull CurrentLocationCallback currentLocationCallback) {
        LocationSettingFragment.INSTANCE = new LocationSettingFragment();
        LocationSettingFragment fragment = LocationSettingFragment.INSTANCE;
        fragment.locationRequest = locationRequest;
        fragment.currentLocationCallback = currentLocationCallback;
        return fragment;
    }

    public static LocationSettingFragment newInstance(@NonNull LocationRequest locationRequest, @NonNull InitializationCallback initializationCallback) {
        LocationSettingFragment.INSTANCE = new LocationSettingFragment();
        LocationSettingFragment fragment = LocationSettingFragment.INSTANCE;
        fragment.locationRequest = locationRequest;
        fragment.initializationCallback = initializationCallback;
        return fragment;
    }

    public static LocationSettingFragment getInstance() {
        return LocationSettingFragment.INSTANCE;
    }

    public void requestLocation() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(getActivity().getApplicationContext());
        client.checkLocationSettings(builder.build())
                .addOnSuccessListener(locationSettingsResponse -> getLocation())
                .addOnFailureListener(e -> {
                    if (e instanceof ResolvableApiException) {
                        this.e = (ResolvableApiException) e;
                        Intent intent = new Intent(getActivity(), ShadowLocationActivity.class);
                        startActivityForResult(intent, REQUEST_CHECK_SETTINGS);
                    }
                    else {
                        if (currentLocationCallback != null) {
                            currentLocationCallback.onFailed(e.getMessage());
                        }
                        else if (initializationCallback != null) {
                            initializationCallback.onFailed(e.getMessage());
                        }
                        removeFragment();
                    }
                });
    }

    public ResolvableApiException getResolvableApiException() {
        return e;
    }

    private void getLocation() {

        if (currentLocationCallback != null) {
            FusedLocationProviderClient locationService = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());
            locationService.requestLocationUpdates(locationRequest, new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult.getLastLocation() != null) {
                        currentLocationCallback.onResult(locationResult.getLastLocation());
                        locationService.removeLocationUpdates(this);
                        removeFragment();
                    }
                }
            }, null);
        }
        else if (initializationCallback != null){
            initializationCallback.onSuccess();
            removeFragment();
        }
    }

    private void removeFragment() {

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction().remove(this).commit();
        }
    }
}