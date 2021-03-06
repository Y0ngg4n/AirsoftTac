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
import pro.oblivioncoding.yonggan.airsofttac.R;

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
    private static MainActivity mainActivity;

    public GoogleLocationService() {
    }

    public GoogleLocationService(final MainActivity mainActivity) {
        GoogleLocationService.mainActivity = mainActivity;
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
            final GeomagneticField field = new GeomagneticField(
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
        if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(mainActivity.getString(R.string.code_google_location_service_log_tag), mainActivity.getString(R.string.code_google_location_service_starting_request_of_location));
            updateLastKnowLocation();
            locationManager.requestLocationUpdates(locationManager.getBestProvider(
                    locationManagerCriteria, true),
                    updateTime, minDistance, locationListener
            );
        } else {
            Log.d(mainActivity.getString(R.string.code_google_location_service_log_tag), mainActivity.getString(R.string.code_google_location_service_requesting_location_permissions));
            MainActivity.getInstance().requestLocationPermissions();
        }
    }

    @Override
    public void onProviderEnabled(final String provider) {
        if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(mainActivity.getString(R.string.code_google_location_service_log_tag), mainActivity.getString(R.string.code_google_location_service_starting_request_of_location));
            updateLastKnowLocation();
            locationManager.requestLocationUpdates(locationManager.getBestProvider(
                    locationManagerCriteria, true),
                    updateTime, minDistance, locationListener
            );
        } else {
            Log.d(mainActivity.getString(R.string.code_google_location_service_log_tag), mainActivity.getString(R.string.code_google_location_service_requesting_location_permissions));
            MainActivity.getInstance().requestLocationPermissions();

        }
    }

    @Override
    public void onProviderDisabled(final String provider) {
        if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(mainActivity.getString(R.string.code_google_location_service_log_tag), mainActivity.getString(R.string.code_google_location_service_starting_request_of_location));
            updateLastKnowLocation();
            locationManager.requestLocationUpdates(locationManager.getBestProvider(
                    locationManagerCriteria, true),
                    updateTime, minDistance, locationListener
            );
        } else {
            Log.d(mainActivity.getString(R.string.code_google_location_service_log_tag), mainActivity.getString(R.string.code_google_location_service_requesting_location_permissions));
            MainActivity.getInstance().requestLocationPermissions();

        }
    }


    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        setLocationManager();
        startForeground();
        requestLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        notificationIntent = new Intent(this, MainActivity.class);

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        if (Build.VERSION.SDK_INT >= 26) {
            final NotificationManager notificationManager =
                    (NotificationManager) mainActivity.getSystemService(
                            NOTIFICATION_SERVICE);
            final NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID,
                    NOTIF_CHANNEL_ID,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(mainActivity.getString(R.string.code_google_location_service_notification_channel_description));
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
                NOTIF_CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(pro.oblivioncoding.yonggan.airsofttac.R.drawable.ic_pin_drop_black_24dp)
                .setContentTitle(mainActivity.getString(R.string.code_google_location_service_notification_title))
                .setContentText(mainActivity.getString(R.string.code_google_location_service_notification_content_text))
                .setContentIntent(pendingIntent)
                .build());
    }

    public void stopService() {
        PendingIntent.getService(this, 0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }


    private void setLocationManager() {
        if (this == null) return;
        locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        locationManagerCriteria = new Criteria();
        locationManagerCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationManager.getBestProvider(locationManagerCriteria, true);
        locationListener = this;
    }

    public void requestLocation() {
        if (locationManager == null || locationManagerCriteria == null || locationListener == null)
            setLocationManager();
        if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(mainActivity.getString(R.string.code_google_location_service_log_tag), mainActivity.getString(R.string.code_google_location_service_starting_request_of_location));
            updateLastKnowLocation();
            locationManager.requestLocationUpdates(locationManager.getBestProvider(
                    locationManagerCriteria, true),
                    updateTime, minDistance, locationListener
            );

            sensorManager = (SensorManager) mainActivity.getSystemService(Context.SENSOR_SERVICE);
            gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            startRotation();
            Log.d(mainActivity.getString(R.string.code_google_location_service_log_tag), mainActivity.getString(R.string.code_google_location_service_starting_rotation));
        } else {
            Log.d(mainActivity.getString(R.string.code_google_location_service_log_tag), mainActivity.getString(R.string.code_google_location_service_requesting_location_permissions));
            MainActivity.getInstance().requestLocationPermissions();
        }
    }

    private void updateLocationData(final Location location) {
        if (location == null) return;
        final UserData ownUserData = FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail());
        Log.d(mainActivity.getString(R.string.code_google_location_service_log_tag), mainActivity.getString(R.string.code_google_location_service_location_changed));
        if (ownUserData != null) {
            ownUserData.setPositionLat(location.getLatitude());
            ownUserData.setPositionLong(location.getLongitude());
        }
        if (gameDocumentReference != null) {
            FirebaseDB.updateObject(gameDocumentReference, mainActivity.getString(R.string.firebase_firestore_variable_games_users),
                    FirebaseDB.getGameData().getUsers());
            Log.d(mainActivity.getString(R.string.code_google_location_service_log_tag), mainActivity.getString(R.string.code_google_location_service_updated_firestore));
        } else {
            FirebaseDB.getGames().whereEqualTo(mainActivity.getString(R.string.firebase_firestore_variable_games_gameID), FirebaseDB.getGameData().getGameID())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        gameDocumentReference = FirebaseDB.getGames().document(task.getResult().getDocuments().get(0).getId());
                        FirebaseDB.updateObject(gameDocumentReference, mainActivity.getString(R.string.firebase_firestore_variable_games_users),
                                FirebaseDB.getGameData().getUsers());
                    }
                } else {
                    Toast.makeText(this, R.string.firebase_firestore_could_not_query_database,
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void updateLastKnowLocation() {
        if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManagerCriteria == null) return;
            final Location lastKnownLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(locationManagerCriteria, true));
            if (lastKnownLocation != null)
                updateLocationData(lastKnownLocation);
        }
    }

    public void startRotation() {
        sensorManager.registerListener(this, gsensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR && mapsRotate) {
            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix, event.values);
            final float[] orientation = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);
            final float bearing = (float) Math.toDegrees(orientation[0]);
            updateCamera(bearing + mDeclination);
        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {

    }

    private void updateCamera(final float bearing) {
        if (MapFragment.getGoogleMap() != null && mapsRotate) {
            final CameraPosition oldPos = MapFragment.getGoogleMap().getCameraPosition();
            final CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
            MapFragment.getGoogleMap().moveCamera(CameraUpdateFactory.newCameraPosition(pos));
            MapFragment.getRotationDegrees().setText(Math.round(bearing < 0 ? 360 + bearing : bearing) + "°");
        }

    }


}