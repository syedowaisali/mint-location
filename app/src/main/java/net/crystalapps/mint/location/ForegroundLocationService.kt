package net.crystalapps.mint.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

import com.google.android.gms.location.LocationRequest

import net.crystalapps.mint.location.library.core.EasyLocation
import net.crystalapps.mint.location.library.core.Subscription
import net.crystalapps.mint.location.library.listeners.LocationUpdatesListener

import java.util.Objects

/**
 * Created by Syed Owais Ali on 10/21/2018.
 */
class ForegroundLocationService : Service(), LocationUpdatesListener {

    private var subscription: Subscription? = null

    private val locationRequest: LocationRequest
        get() {
            val request = LocationRequest()
            request.interval = 20000
            request.fastestInterval = 15000
            request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            return request
        }

    override fun onCreate() {
        super.onCreate()
        Log.d("mint->", "FGS onCreated")
        createNotificationChannel()
        subscription = EasyLocation.subscribeForUpdates(this, locationRequest, this)
        subscription!!.start()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        Log.d("mint->", "FGS onStartCommand")

        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onResult(location: Location) {
        startForeground(NOTIF_ID, buildNotification(location))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (subscription != null) {
            subscription!!.stop()
        }
        Log.d("mint->", "FGS onDestroy")
    }

    fun buildNotification(location: Location): Notification {

        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Updated Location")
                .setContentText(location.latitude.toString() + "," + location.longitude)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(longArrayOf(0))
                .build()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = BuildConfig.APPLICATION_ID
            val description = "All Mint Location Notificaitons"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            channel.setSound(null, null)
            channel.enableVibration(false)

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel)
        }

    }

    companion object {

        private val CHANNEL_ID = BuildConfig.APPLICATION_ID + ".notification.channel.id"
        private val NOTIF_ID = 1989
    }
}