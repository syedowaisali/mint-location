package net.crystalapps.mint.location;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.crystalapps.mint.location.library.callbacks.CurrentLocationCallback;
import net.crystalapps.mint.location.library.callbacks.InitializationCallback;
import net.crystalapps.mint.location.library.core.EasyLocation;
import net.crystalapps.mint.location.library.core.Subscription;


@SuppressWarnings({"SetTextI18n", "unused"})
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvCurrentLocation;
    private TextView tvUpdatedLocation;
    private Button btnGetCurrentLocation;
    private Button btnStartLocationUpdates;
    private Button btnStopLocationUpdates;
    private Button btnStartForegroundService;
    private Button btnStopForegroundService;
    private Subscription subscription;

    /*---------------------------------------------------*/
    // SETUP
    /*---------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCurrentLocation = findViewById(R.id.tv_current_location);
        tvUpdatedLocation = findViewById(R.id.tv_updated_location);
        btnGetCurrentLocation = findViewById(R.id.btn_get_current_location);
        btnStartLocationUpdates = findViewById(R.id.btn_start_location_updates);
        btnStopLocationUpdates = findViewById(R.id.btn_stop_location_updates);
        btnStartForegroundService = findViewById(R.id.btn_start_foreground_service);
        btnStopForegroundService = findViewById(R.id.btn_stop_foreground_service);

        btnGetCurrentLocation.setOnClickListener(this);
        btnStartLocationUpdates.setOnClickListener(this);
        btnStopLocationUpdates.setOnClickListener(this);
        btnStartForegroundService.setOnClickListener(this);
        btnStopForegroundService.setOnClickListener(this);
    }

    /*---------------------------------------------------*/
    // HANDLE CLICK EVENTS
    /*---------------------------------------------------*/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get_current_location: getCurrentLocation(); break;
            case R.id.btn_start_location_updates: startLocationUpdates(); break;
            case R.id.btn_stop_location_updates: stopLocationUpdates(); break;
            case R.id.btn_start_foreground_service: startForegroundService(); break;
            case R.id.btn_stop_foreground_service: stopForegroundService(); break;
        }
    }

    /*---------------------------------------------------*/
    // GET CURRENT LOCATION
    /*---------------------------------------------------*/

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

    private void startLocationUpdates() {
        EasyLocation.init(this, () -> {
            {
                btnStartLocationUpdates.setEnabled(false);
                btnStopLocationUpdates.setEnabled(true);

                if (subscription == null) {
                    subscription = EasyLocation.subscribeForUpdates(getApplicationContext(), location -> {
                        tvUpdatedLocation.setText("Updated Location : " + location.getLatitude() + "," + location.getLongitude());
                    });
                }

                subscription.start();
            }
        });
    }

    private void stopLocationUpdates() {
        btnStopLocationUpdates.setEnabled(false);
        btnStartLocationUpdates.setEnabled(true);
        subscription.stop();
    }

    /*---------------------------------------------------*/
    // START / STOP FOREGROUND SERVICE
    /*---------------------------------------------------*/

    private void startForegroundService() {
        EasyLocation.init(this, () -> {

            btnStartForegroundService.setEnabled(false);
            btnStopForegroundService.setEnabled(true);

            startService(new Intent(MainActivity.this, ForegroundLocationService.class));

        });
    }

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