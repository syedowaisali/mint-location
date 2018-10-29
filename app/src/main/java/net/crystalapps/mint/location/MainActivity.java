package net.crystalapps.mint.location;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.crystalapps.mint.location.library.callbacks.CurrentLocationCallback;
import net.crystalapps.mint.location.library.core.EasyLocation;
import net.crystalapps.mint.location.library.core.Subscription;

import crystalapps.net.mint.tools.binder.view.BindView;
import crystalapps.net.mint.tools.binder.view.OnClick;
import crystalapps.net.mint.tools.binder.view.ViewBinder;

@SuppressWarnings({"SetTextI18n", "unused"})
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_current_location)
    private TextView tvCurrentLocation;

    @BindView(R.id.tv_updated_location)
    private TextView tvUpdatedLocation;

    @BindView(R.id.btn_start_location_updates)
    private Button btnStartLocationUpdates;

    @BindView(R.id.btn_stop_location_updates)
    private Button btnStopLocationUpdates;

    @BindView(R.id.btn_start_foreground_service)
    private Button btnStartForegroundService;

    @BindView(R.id.btn_stop_foreground_service)
    private Button btnStopForegroundService;

    private Subscription subscription;

    /*---------------------------------------------------*/
    // SETUP
    /*---------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBinder.bind(this);
    }

    /*---------------------------------------------------*/
    // GET CURRENT LOCATION
    /*---------------------------------------------------*/

    @OnClick(R.id.btn_get_current_location)
    private void getCurrentLocation() {
        EasyLocation.requestCurrentLocation(this, new CurrentLocationCallback() {
            @Override
            public void onResult(Location location) {
                tvCurrentLocation.setText("Current Location : " + location.getLatitude() + "," + location.getLongitude());
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*---------------------------------------------------*/
    // START / STOP LOCATION UPDATES
    /*---------------------------------------------------*/

    @OnClick(R.id.btn_start_location_updates)
    private void startLocationUpdates() {
        EasyLocation.init(this, () -> {

            btnStartLocationUpdates.setEnabled(false);
            btnStopLocationUpdates.setEnabled(true);

            if (subscription == null) {
                subscription = EasyLocation.subscribeForUpdates(getApplicationContext(), location -> {
                    tvUpdatedLocation.setText("Updated Location : " + location.getLatitude() + "," + location.getLongitude());
                });
            }

            subscription.start();
        });
    }

    @OnClick(R.id.btn_stop_location_updates)
    private void stopLocationUpdates() {
        btnStopLocationUpdates.setEnabled(false);
        btnStartLocationUpdates.setEnabled(true);
        subscription.stop();
    }

    /*---------------------------------------------------*/
    // START / STOP FOREGROUND SERVICE
    /*---------------------------------------------------*/

    @OnClick(R.id.btn_start_foreground_service)
    private void startForegroundService() {
        EasyLocation.init(this, () -> {

            btnStartForegroundService.setEnabled(false);
            btnStopForegroundService.setEnabled(true);

            startService(new Intent(MainActivity.this, ForegroundLocationService.class));

        });
    }

    @OnClick(R.id.btn_stop_foreground_service)
    private void stopForegroundService() {
        btnStopForegroundService.setEnabled(false);
        btnStartForegroundService.setEnabled(true);
        stopService(new Intent(this, ForegroundLocationService.class));
    }

    /*---------------------------------------------------*/
    // ACTIVITY DESTROY
    /*---------------------------------------------------*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.stop();
        }
    }
}