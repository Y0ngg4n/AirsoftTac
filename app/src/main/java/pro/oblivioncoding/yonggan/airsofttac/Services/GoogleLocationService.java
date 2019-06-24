package pro.oblivioncoding.yonggan.airsofttac.Services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.firebase.firestore.DocumentReference;

import pro.oblivioncoding.yonggan.airsofttac.Activitys.MainActivity;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.MapFragment;

public class GoogleLocationService extends Service implements LocationListener, SensorEventListener {
    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "AirsoftTac";

    private Intent notificationIntent;

    private DocumentReference gameDocumentReference;

    public static boolean mapsRotate = false;
    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private static long updateTime = 60000;
    private static float minDistance = 5;
    private Criteria locationManagerCriteria;
    private SensorManager sensorManager;
    private float[] mRotationMatrix = new float[16];
    private Sensor gsensor;
    private float mDeclination;

    public GoogleLocationService() {
    }

    @Override
    public IBinder onBind(final Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(@NonNull final Location location) {
        updateLocationData(location);
        if (mapsRotate) {
            GeomagneticField field = new GeomagneticField(
                    (float) location.getLatitude(),
                    (float) location.getLongitude(),
                    (float) location.getAltitude(),
                    System.currentTimeMillis()
            );
            mDeclination = field.getDeclination();
        }
    }

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("LocationService", "Starting request of Location");
            updateLocationData(locationManager.getLastKnownLocation(locationManager.getBestProvider(locationManagerCriteria, true)));
            locationManager.requestLocationUpdates(locationManager.getBestProvider(
                    locationManagerCriteria, true),
                    updateTime, minDistance, locationListener
            );
        } else {
            Log.d("LocationService", "Requesting Location Permissions");
            MainActivity.getInstance().requestLocationPermissions();
        }
    }

    @Override
    public void onProviderEnabled(final String provider) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("LocationService", "Starting request of Location");
            updateLocationData(locationManager.getLastKnownLocation(locationManager.getBestProvider(locationManagerCriteria, true)));
            locationManager.requestLocationUpdates(locationManager.getBestProvider(
                    locationManagerCriteria, true),
                    updateTime, minDistance, locationListener
            );
        } else {
            Log.d("LocationService", "Requesting Location Permissions");
            MainActivity.getInstance().requestLocationPermissions();

        }
    }

    @Override
    public void onProviderDisabled(final String provider) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("LocationService", "Starting request of Location");
            updateLocationData(locationManager.getLastKnownLocation(locationManager.getBestProvider(locationManagerCriteria, true)));
            locationManager.requestLocationUpdates(locationManager.getBestProvider(
                    locationManagerCriteria, true),
                    updateTime, minDistance, locationListener
            );
        } else {
            Log.d("LocationService", "Requesting Location Permissions");
            MainActivity.getInstance().requestLocationPermissions();

        }
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        startForeground();
        requestLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);

        if (Build.VERSION.SDK_INT >= 26) {
            final NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(
                            getApplicationContext().NOTIFICATION_SERVICE);
            final NotificationChannel channel = new NotificationChannel("airsofttaclocationservice",
                    NOTIF_CHANNEL_ID,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("This Notification Channel is for the AirsoftTacLocationService Notification");
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(NOTIF_ID, new NotificationCompat.Builder(getApplicationContext(),
                "airsofttaclocationservice") // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(pro.oblivioncoding.yonggan.airsofttac.R.drawable.ic_pin_drop_black_24dp)
                .setContentTitle("AirsoftTac")
                .setContentText("Updating your Position in Background.\nTo stop, kill the App in your Task-Manager.")
                .setContentIntent(pendingIntent)
                .build());
    }

    public void stopService() {
        PendingIntent.getService(this, 0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void requestLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManagerCriteria = new Criteria();
        locationManagerCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationManager.getBestProvider(locationManagerCriteria, true);
        locationListener = this;
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("LocationService", "Starting request of Location");
            updateLocationData(locationManager.getLastKnownLocation(locationManager.getBestProvider(locationManagerCriteria, true)));
            locationManager.requestLocationUpdates(locationManager.getBestProvider(
                    locationManagerCriteria, true),
                    updateTime, minDistance, locationListener
            );

            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            startRotation();
            Log.d("LocationService", "starte");
        } else {
            Log.d("LocationService", "Requesting Location Permissions");
            MainActivity.getInstance().requestLocationPermissions();

        }
    }

    private void updateLocationData(final Location location) {
        final UserData ownUserData = FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail());
        Log.d("LocationService", "OnLocationChanged");
        if (ownUserData != null) {
            ownUserData.setPositionLat(location.getLatitude());
            ownUserData.setPositionLong(location.getLongitude());
        }
        if (gameDocumentReference != null) {
            Log.d("LocationService", "Updated");
            FirebaseDB.updateObject(gameDocumentReference, "users",
                    FirebaseDB.getGameData().getUsers());
        } else {
            FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        gameDocumentReference = FirebaseDB.getGames().document(task.getResult().getDocuments().get(0).getId());
                        FirebaseDB.updateObject(gameDocumentReference, "users",
                                FirebaseDB.getGameData().getUsers());
                    }
                } else {
                    Log.d("LocationService", "GameID not found!");
                    Toast.makeText(getApplicationContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void startRotation() {
        sensorManager.registerListener(this, gsensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR && mapsRotate) {
            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);
            float bearing = (float) Math.toDegrees(orientation[0]) + mDeclination;
            updateCamera(bearing);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateCamera(float bearing) {
        MapFragment mapFragment = MainActivity.getInstance().getMapFragment();
        if (MapFragment.getGoogleMap() != null && mapsRotate) {
            CameraPosition oldPos = MapFragment.getGoogleMap().getCameraPosition();
            CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
            MapFragment.getGoogleMap().moveCamera(CameraUpdateFactory.newCameraPosition(pos));
            MapFragment.getRotationDegrees().setText(bearing + "°");
        }

    }


}