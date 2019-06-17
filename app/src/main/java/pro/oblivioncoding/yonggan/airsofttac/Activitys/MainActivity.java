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
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.GameData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.ChatFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.MapFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.PlayerFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.TeamFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;
import pro.oblivioncoding.yonggan.airsofttac.Services.GoogleLocationService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MapFragment.OnFragmentInteractionListener, PlayerFragment.OnFragmentInteractionListener, TeamFragment.OnFragmentInteractionListener, ChatFragment.OnFragmentInteractionListener {

    private Fragment currentFragment;
    private MapFragment mapFragment;
    private PlayerFragment playerFragment;
    private ChatFragment chatFragment;
    private static MainActivity instance;

    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private Criteria locationManagerCriteria;
    private TeamFragment teamFragment;

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

    private Menu menu;

    public static MainActivity getInstance() {
        return instance;
    }

    @NonNull
    private GoogleLocationService googleLocationService = new GoogleLocationService();

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
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_title)).setText(FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).getNickname());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_email)).setText(FirebaseAuthentication.getFirebaseUser().getEmail());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Create new Object of Fragment
        mapFragment = new MapFragment();
        playerFragment = new PlayerFragment();
        teamFragment = new TeamFragment();
        chatFragment = new ChatFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.content_main, mapFragment);
        fragmentTransaction.add(R.id.content_main, playerFragment);
        fragmentTransaction.add(R.id.content_main, teamFragment);
        fragmentTransaction.add(R.id.content_main, chatFragment);

        fragmentTransaction.detach(playerFragment);
        fragmentTransaction.detach(teamFragment);
        fragmentTransaction.detach(chatFragment);
        fragmentTransaction.commit();

        currentFragment = mapFragment;

        addSnapshotListener();
        if (FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isOrga()) {
            navigationView.getMenu().getItem(1).setVisible(true);
        }
        requestLocationPermissions();

        locationManager = (LocationManager) this.

                getSystemService(Context.LOCATION_SERVICE);

        locationManagerCriteria = new Criteria();
        locationManagerCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationManager.getBestProvider(locationManagerCriteria, true);
        locationListener = googleLocationService;

        startService(new Intent(getApplicationContext(), googleLocationService.getClass()));


    }

    public void queryUpdateData() {
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
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Couldn´t query Database!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addSnapshotListener() {
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
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Couldn´t query Database!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateData(DocumentSnapshot documentSnapshot) {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            default:
                return super.onOptionsItemSelected(item);
            case R.id.settings_show_all_player:
                item.setChecked(true);
                mapFragment.showSettings = MapFragment.ShowSettings.AllPlayer;
                break;
            case R.id.settings_show_own_team_only:
                item.setChecked(true);
                mapFragment.showSettings = MapFragment.ShowSettings.ShowTeamOnly;
                break;
            case R.id.settings_show_not_assigned:
                item.setChecked(true);
                mapFragment.showSettings = MapFragment.ShowSettings.ShowOnlyNotAssigned;
                break;
            case R.id.settings_show_all_player_team:
                item.setChecked(true);
                teamFragment.showSettings = TeamFragment.ShowSettings.AllPlayer;
                break;
            case R.id.settings_show_own_team_only_team:
                item.setChecked(true);
                teamFragment.showSettings = TeamFragment.ShowSettings.ShowTeamOnly;
                break;
            case R.id.settings_show_not_assigned_team:
                item.setChecked(true);
                teamFragment.showSettings = TeamFragment.ShowSettings.ShowOnlyNotAssigned;
                break;
        }
        mapFragment.setMarker();
        if (teamFragment.getRecyclerView() != null && FirebaseDB.getGameData().getTeams() != null)
            teamFragment.setRecyclerView(FirebaseDB.getGameData().getTeams(), teamFragment.getRecyclerView());
        return true;

        //noinspection SimplifiableIfStatement

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
        if (playerFragment == null) playerFragment = new PlayerFragment();
        menu.clear();
        if (id == R.id.nav_map) {
            if (menu != null)
                getMenuInflater().inflate(R.menu.map, menu);
            fragmentTransaction.attach(mapFragment);
            currentFragment = mapFragment;
        } else if (id == R.id.nav_player) {
            if (menu != null)
                getMenuInflater().inflate(R.menu.main, menu);
            fragmentTransaction.attach(playerFragment);
            currentFragment = playerFragment;
        } else if (id == R.id.nav_team) {
            if (menu != null)
                getMenuInflater().inflate(R.menu.team, menu);
            fragmentTransaction.attach(teamFragment);
            currentFragment = teamFragment;
        } else if (id == R.id.nav_chat) {
            if (menu != null)
                getMenuInflater().inflate(R.menu.team, menu);
            fragmentTransaction.attach(chatFragment);
            currentFragment = chatFragment;
        }

        fragmentTransaction.commit();
        queryUpdateData();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
