package pro.oblivioncoding.yonggan.airsofttac.Services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.function.Consumer;

import pro.oblivioncoding.yonggan.airsofttac.Activitys.MainActivity;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class GoogleLocationService extends Service implements LocationListener {
    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "AirsoftTac";

    private Intent notificationIntent;

    public GoogleLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.i("Location", "OnLocationChanged");
        final UserData ownUserData = FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail());
        if (ownUserData != null) {
            Log.i("Location", "sameEmail");
            ownUserData.setPositionLat(location.getLatitude());
            ownUserData.setPositionLong(location.getLongitude());
        }

        FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    final DocumentReference documentReference = FirebaseDB.getGames().document(task.getResult().getDocuments().get(0).getId());
                    if (documentReference != null)
                        FirebaseDB.updateObject(documentReference, "users",
                                FirebaseDB.getGameData().getUsers());
                }
            } else {
                Toast.makeText(getApplicationContext(), "Couldn´t query Database!",
                        Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();
        requestLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(
                            getApplicationContext().NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("airsofttaclocationservice",
                    NOTIF_CHANNEL_ID,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("This Notification Channel is for the ArisoftTacLocationService Notification");
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(NOTIF_ID, new NotificationCompat.Builder(getApplicationContext(),
                "airsofttaclocationservice") // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_pin_drop_black_24dp)
                .setContentTitle("AirsoftTac")
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build());
    }

    public void stopService() {
        PendingIntent.getService(this, 0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void requestLocation() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            MainActivity.getLocationManager().requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    (long) MainActivity.getUpdateTime(), MainActivity.getMinDistance(),
                    MainActivity.getLocationListener());
        }
    }
}
