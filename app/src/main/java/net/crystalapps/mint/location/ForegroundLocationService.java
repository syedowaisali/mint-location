package net.crystalapps.mint.location;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationRequest;

import net.crystalapps.mint.location.library.core.EasyLocation;
import net.crystalapps.mint.location.library.core.Subscription;
import net.crystalapps.mint.location.library.listeners.LocationUpdatesListener;

import java.util.Objects;

/**
 * Created by Syed Owais Ali on 10/21/2018.
 */
public class ForegroundLocationService extends Service implements LocationUpdatesListener {

    private static String CHANNEL_ID = BuildConfig.APPLICATION_ID + ".notification.channel.id";
    private static final int NOTIF_ID = 1989;

    private Subscription subscription;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("mint->", "FGS onCreated");
        createNotificationChannel();
        subscription = EasyLocation.subscribeForUpdates(this, getLocationRequest(), this);
        subscription.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("mint->", "FGS onStartCommand");

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onResult(@NonNull Location location) {
        startForeground(NOTIF_ID, buildNotification(location));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.stop();
        }
        Log.d("mint->", "FGS onDestroy");
    }

    public Notification buildNotification(@NonNull Location location) {

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Updated Location")
                .setContentText(location.getLatitude() + "," + location.getLongitude())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(new long[]{0})
                .build();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = BuildConfig.APPLICATION_ID;
            String description = "All Mint Location Notificaitons";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            channel.enableVibration(false);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

    }

    private LocationRequest getLocationRequest() {
        LocationRequest request = new LocationRequest();
        request.setInterval(20000);
        request.setFastestInterval(15000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return request;
    }
}