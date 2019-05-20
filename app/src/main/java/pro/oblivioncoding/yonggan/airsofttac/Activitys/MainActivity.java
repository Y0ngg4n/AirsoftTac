package pro.oblivioncoding.yonggan.airsofttac.Activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.GameData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.OrgaAddMarkerDialogFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.MapFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.PlayerFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;
import pro.oblivioncoding.yonggan.airsofttac.Services.GoogleLocationService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MapFragment.OnFragmentInteractionListener, PlayerFragment.OnFragmentInteractionListener {

    private Fragment currentFragment;
    private MapFragment mapFragment;
    private PlayerFragment playerFragment;

    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private Criteria locationManagerCriteria;

    public static LocationListener getLocationListener() {
        return locationListener;
    }

    public static LocationManager getLocationManager() {
        return locationManager;
    }

    private static long updateTime = 5000;
    private static float minDistance = 5;

    public static float getUpdateTime() {
        return updateTime;
    }

    public static float getMinDistance() {
        return minDistance;
    }

    @NonNull
    private GoogleLocationService googleLocationService = new GoogleLocationService();

    private static FloatingActionButton hitfb, underfirefb, supportfb, missionfb;

    private static FloatingActionButton reloadfb, setMarkerfb, removeMarkerfb, swapFlagfb;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            requestLocationPermissions();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Create new Object of Fragment
        mapFragment = new MapFragment();
        playerFragment = new PlayerFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.content_main, mapFragment);
        fragmentTransaction.add(R.id.content_main, playerFragment);

        fragmentTransaction.detach(playerFragment);
        fragmentTransaction.commit();

        currentFragment = mapFragment;

        FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        updateData(documentSnapshot);
                        documentSnapshot.getReference().addSnapshotListener((documentSnapshot1, e) -> {
                            updateData(documentSnapshot1);
                        });
                    } else {
                        Log.d("UpdateDB", "Current data: null");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn´t find Document with GameID!",
                            Toast.LENGTH_LONG);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Couldn´t query Database!",
                        Toast.LENGTH_LONG);
            }
        });

        requestLocationPermissions();

        locationManager = (LocationManager) this.

                getSystemService(Context.LOCATION_SERVICE);

        locationManagerCriteria = new Criteria();
        locationManagerCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationManager.getBestProvider(locationManagerCriteria, true);
        locationListener = googleLocationService;

        startService(new Intent(getApplicationContext(), googleLocationService.getClass()));

        hitfb = findViewById(R.id.hitfb);
        underfirefb = findViewById(R.id.underfirefb);
        supportfb = findViewById(R.id.supportfb);
        missionfb = findViewById(R.id.missionfb);

        reloadfb = findViewById(R.id.reloadfb);
        setMarkerfb = findViewById(R.id.setMarker);
        removeMarkerfb = findViewById(R.id.removeMarker);
        swapFlagfb = findViewById(R.id.swapFlagMarker);

        hitfb.setOnClickListener(v -> {
            FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        final DocumentReference documentReference = FirebaseDB.getGames().document(task.getResult().getDocuments().get(0).getId());
                        FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).setAlive(!FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isAlive());
                        FirebaseDB.updateObject(documentReference, "users",
                                FirebaseDB.getGameData().getUsers());
                        if (FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isAlive()) {
                            missionfb.show();
                            supportfb.show();
                            underfirefb.show();
                            hitfb.setImageResource(R.drawable.ic_fb_hit);
                            hitfb.hide();
                            hitfb.show();
                        } else {
                            missionfb.hide();
                            supportfb.hide();
                            underfirefb.hide();
                            hitfb.setImageResource(R.drawable.ic_fb_healed);
                            hitfb.hide();
                            hitfb.show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Couldn´t find Document with GameID!",
                                Toast.LENGTH_LONG);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG);
                }
            });
        });

        underfirefb.setOnClickListener(v -> {
            FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        final DocumentReference documentReference = FirebaseDB.getGames().document(task.getResult().getDocuments().get(0).getId());
                        FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).setUnderfire(!FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isUnderfire());
                        FirebaseDB.updateObject(documentReference, "users",
                                FirebaseDB.getGameData().getUsers());
                        if (FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isUnderfire()) {
                            missionfb.hide();
                            underfirefb.setImageResource(R.drawable.ic_fb_not_underfire);
                            underfirefb.hide();
                            underfirefb.show();
                        } else {
                            missionfb.show();
                            underfirefb.setImageResource(R.drawable.ic_fb_under_fire);
                            underfirefb.hide();
                            underfirefb.show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Couldn´t find Document with GameID!",
                                Toast.LENGTH_LONG);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG);
                }
            });
        });

        supportfb.setOnClickListener(v -> {
            FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        final DocumentReference documentReference = FirebaseDB.getGames().document(task.getResult().getDocuments().get(0).getId());
                        FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).setSupport(!FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isSupport());
                        FirebaseDB.updateObject(documentReference, "users",
                                FirebaseDB.getGameData().getUsers());
                        if (FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isSupport()) {
                            missionfb.hide();
                            supportfb.setImageResource(R.drawable.ic_fb_no_support);
                            supportfb.hide();
                            supportfb.show();
                        } else {
                            missionfb.show();
                            supportfb.setImageResource(R.drawable.ic_fb_support);
                            supportfb.hide();
                            supportfb.show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Couldn´t find Document with GameID!",
                                Toast.LENGTH_LONG);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG);
                }
            });
        });

        missionfb.setOnClickListener(v -> {
            FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        final DocumentReference documentReference = FirebaseDB.getGames().document(task.getResult().getDocuments().get(0).getId());
                        FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).setMission(!FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isMission());
                        FirebaseDB.updateObject(documentReference, "users",
                                FirebaseDB.getGameData().getUsers());
                        if (FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isMission()) {
                            underfirefb.hide();
                            supportfb.hide();
                            hitfb.hide();
                            missionfb.setImageResource(R.drawable.ic_fb_mission_success);
                            missionfb.hide();
                            missionfb.show();
                        } else {
                            underfirefb.show();
                            supportfb.show();
                            hitfb.show();
                            missionfb.setImageResource(R.drawable.ic_fb_mission);
                            missionfb.hide();
                            missionfb.show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Couldn´t find Document with GameID!",
                                Toast.LENGTH_LONG);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG);
                }
            });
        });

        reloadfb.setOnClickListener(v -> {
            reloadfb.setEnabled(false);
            FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            updateData(documentSnapshot);
                        } else {
                            Log.d("UpdateDB", "Current data: null");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Couldn´t find Document with GameID!",
                                Toast.LENGTH_LONG);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG);
                }
            });
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        reloadfb.setEnabled(true);
                    });
                }
            }, 30000L);
        });

        if(FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isOrga()) {
            setMarkerfb.setOnClickListener(v -> {
                OrgaAddMarkerDialogFragment orgaAddMarkerDialogFragment = OrgaAddMarkerDialogFragment.newInstance("New Marker");
                orgaAddMarkerDialogFragment.show(fragmentManager, "orga_add_marker_dialog");
            });

            removeMarkerfb.setOnClickListener(v -> {

            });

            swapFlagfb.setOnClickListener(v -> {

            });
        }else{
            setMarkerfb.setEnabled(false);
            removeMarkerfb.setEnabled(false);
            swapFlagfb.setEnabled(false);
        }
    }

    private void updateData(DocumentSnapshot documentSnapshot) {
        Log.d("UpdateDB", "Current data: " + documentSnapshot.getData());
        FirebaseDB.setGameData(documentSnapshot.toObject(GameData.class));
        mapFragment.setMarker();
    }

    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (currentFragment != null) fragmentTransaction.detach(currentFragment);

        if (mapFragment == null) mapFragment = new MapFragment();

        if (id == R.id.nav_map) {
            fragmentTransaction.attach(mapFragment);
            currentFragment = mapFragment;
            Log.i("Nav", "Map selected");
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
