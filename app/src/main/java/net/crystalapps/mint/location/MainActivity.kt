package net.crystalapps.mint.location

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import net.crystalapps.mint.location.library.callbacks.CurrentLocationCallback
import net.crystalapps.mint.location.library.callbacks.InitializationCallback
import net.crystalapps.mint.location.library.core.EasyLocation
import net.crystalapps.mint.location.library.core.Subscription
import net.crystalapps.mint.location.library.listeners.LocationUpdatesListener


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var tvCurrentLocation: TextView? = null
    private var tvUpdatedLocation: TextView? = null
    private var btnGetCurrentLocation: Button? = null
    private var btnStartLocationUpdates: Button? = null
    private var btnStopLocationUpdates: Button? = null
    private var btnStartForegroundService: Button? = null
    private var btnStopForegroundService: Button? = null
    private var subscription: Subscription? = null

    /*---------------------------------------------------*/
    // SETUP
    /*---------------------------------------------------*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCurrentLocation = findViewById(R.id.tv_current_location)
        tvUpdatedLocation = findViewById(R.id.tv_updated_location)
        btnGetCurrentLocation = findViewById(R.id.btn_get_current_location)
        btnStartLocationUpdates = findViewById(R.id.btn_start_location_updates)
        btnStopLocationUpdates = findViewById(R.id.btn_stop_location_updates)
        btnStartForegroundService = findViewById(R.id.btn_start_foreground_service)
        btnStopForegroundService = findViewById(R.id.btn_stop_foreground_service)

        btnGetCurrentLocation!!.setOnClickListener(this)
        btnStartLocationUpdates!!.setOnClickListener(this)
        btnStopLocationUpdates!!.setOnClickListener(this)
        btnStartForegroundService!!.setOnClickListener(this)
        btnStopForegroundService!!.setOnClickListener(this)
    }

    /*---------------------------------------------------*/
    // HANDLE CLICK EVENTS
    /*---------------------------------------------------*/

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_get_current_location -> getCurrentLocation()
            R.id.btn_start_location_updates -> startLocationUpdates()
            R.id.btn_stop_location_updates -> stopLocationUpdates()
            R.id.btn_start_foreground_service -> startForegroundService()
            R.id.btn_stop_foreground_service -> stopForegroundService()
        }
    }

    /*---------------------------------------------------*/
    // GET CURRENT LOCATION
    /*---------------------------------------------------*/

    private fun getCurrentLocation() {
        EasyLocation.requestCurrentLocation(this, object : CurrentLocationCallback {
            override fun onResult(location: Location) {
                tvCurrentLocation!!.text = "Current Location : " + location.latitude + "," + location.longitude
            }

            override fun onPermissionDenied() {
                Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_LONG).show()
            }

            override fun onFailed(error: String) {
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_LONG).show()
            }
        })
    }

    /*---------------------------------------------------*/
    // START / STOP LOCATION UPDATES
    /*---------------------------------------------------*/

    private fun startLocationUpdates() {
        EasyLocation.init(this, object: InitializationCallback{
            override fun onSuccess() {
                btnStartLocationUpdates!!.isEnabled = false
                btnStopLocationUpdates!!.isEnabled = true

                if (subscription == null) {
                    subscription = EasyLocation.subscribeForUpdates(applicationContext, object: LocationUpdatesListener{
                        override fun onResult(location: Location) {
                            tvUpdatedLocation!!.text = "Updated Location : " + location.getLatitude() + "," + location.getLongitude()
                        }
                    })
                }

                subscription!!.start()
            }
        })
    }

    private fun stopLocationUpdates() {
        btnStopLocationUpdates!!.isEnabled = false
        btnStartLocationUpdates!!.isEnabled = true
        subscription!!.stop()
    }

    /*---------------------------------------------------*/
    // START / STOP FOREGROUND SERVICE
    /*---------------------------------------------------*/

    private fun startForegroundService() {
        EasyLocation.init(this, object: InitializationCallback{
            override fun onSuccess() {
                btnStartForegroundService!!.isEnabled = false
                btnStopForegroundService!!.isEnabled = true

                startService(Intent(this@MainActivity, ForegroundLocationService::class.java))
            }
        })
    }

    private fun stopForegroundService() {
        btnStopForegroundService!!.isEnabled = false
        btnStartForegroundService!!.isEnabled = true
        stopService(Intent(this, ForegroundLocationService::class.java))
    }

    /*---------------------------------------------------*/
    // ACTIVITY DESTROY
    /*---------------------------------------------------*/

    override fun onDestroy() {
        super.onDestroy()
        if (subscription != null) {
            subscription!!.stop()
        }
    }
}