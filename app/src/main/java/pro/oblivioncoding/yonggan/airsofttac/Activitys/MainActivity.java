package pro.oblivioncoding.yonggan.airsofttac.Activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Chat.ChatMessage;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.GameData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.OverlayImageCollection.OverlayImage;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.BeerFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.ChatFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.GameIDFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.MapFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.OverlayImagesFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.PlayerFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.SettingsFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.TeamFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;
import pro.oblivioncoding.yonggan.airsofttac.Services.GoogleLocationService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MapFragment.OnFragmentInteractionListener, PlayerFragment.OnFragmentInteractionListener, TeamFragment.OnFragmentInteractionListener, ChatFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener, BeerFragment.OnFragmentInteractionListener, GameIDFragment.OnFragmentInteractionListener, OverlayImagesFragment.OnFragmentInteractionListener {

    private static MainActivity instance;


    private Fragment currentFragment;
    private MapFragment mapFragment;
    private PlayerFragment playerFragment;
    private ChatFragment chatFragment;
    private SettingsFragment settingsFragment;
    private BeerFragment beerFragment;
    private OverlayImagesFragment overlayImagesFragment;
    private GameIDFragment gameIDFragment;

    public MapFragment getMapFragment() {
        return mapFragment;
    }

    private boolean alreadyRequestingLocationPermissions = false;

    private TeamFragment teamFragment;
    private Menu menu;

    @NonNull
    private GoogleLocationService googleLocationService = new GoogleLocationService(this);

    @NonNull
    public GoogleLocationService getGoogleLocationService() {
        return googleLocationService;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        alreadyRequestingLocationPermissions = false;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            requestLocationPermissions();
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_title)).setText(FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).getNickname());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_email)).setText(FirebaseAuthentication.getFirebaseUser().getEmail());
        final TextView timeView = navigationView.getHeaderView(0).findViewById(R.id.nav_header_timeView);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final Date currentTime = Timestamp.now().toDate();
                final Date startDate = FirebaseDB.getGameData().getStartTime().toDate();
                final Date endDate = FirebaseDB.getGameData().getEndTime().toDate();
                final Calendar calendar = Calendar.getInstance();
                if (currentTime.before(startDate)) {
                    calendar.setTime(startDate);
                    runOnUiThread(() -> {
                        timeView.setText(R.string.code_main_activity_start_of_game + calendar.get(Calendar.DATE) + "." + (calendar.get(Calendar.MONTH) + 1) + R.string.code_main_activity_at +
                                calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                    });
                } else {
                    calendar.setTime(endDate);
                    runOnUiThread(() -> {
                        timeView.setText(R.string.code_main_activity_end_of_game + calendar.get(Calendar.DATE) + "." + (calendar.get(Calendar.MONTH) + 1) + R.string.code_main_activity_at +
                                calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                    });
                }
            }
        }, 0L, 60000L);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Create new Object of Fragment
        mapFragment = new MapFragment();
        playerFragment = new PlayerFragment();
        teamFragment = new TeamFragment();
        chatFragment = new ChatFragment();
        settingsFragment = new SettingsFragment();
//        beerFragment = new BeerFragment();
        gameIDFragment = new GameIDFragment();
        overlayImagesFragment = new OverlayImagesFragment();

        final FragmentManager fragmentManager = getSupportFragmentManager();

        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.content_main, mapFragment);
        fragmentTransaction.add(R.id.content_main, playerFragment);
        fragmentTransaction.add(R.id.content_main, teamFragment);
        fragmentTransaction.add(R.id.content_main, chatFragment);
        fragmentTransaction.add(R.id.content_main, settingsFragment);
//        fragmentTransaction.add(R.id.content_main, beerFragment);
        fragmentTransaction.add(R.id.content_main, gameIDFragment);
        fragmentTransaction.add(R.id.content_main, overlayImagesFragment);


        fragmentTransaction.detach(playerFragment);
        fragmentTransaction.detach(teamFragment);
        fragmentTransaction.detach(chatFragment);
        fragmentTransaction.detach(settingsFragment);
//        fragmentTransaction.detach(beerFragment);
        fragmentTransaction.detach(gameIDFragment);
        fragmentTransaction.detach(overlayImagesFragment);

        fragmentTransaction.commit();

        currentFragment = mapFragment;

        addSnapshotListener();
        if (FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isOrga()) {
            navigationView.getMenu().getItem(1).setVisible(true);
        }
        requestLocationPermissions();

        startService(new Intent(getApplicationContext(), googleLocationService.getClass()));

//        final InterstitialAd interstitialAd = new InterstitialAd(this);
//        interstitialAd.setAdUnitId(AdMobIds.InterstialAll15Min);
//        interstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                interstitialAd.show();
//            }
//
//        });
//        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onUserInteraction() {
//        if (mapFragment != null) mapFragment.addKmlLayer();
    }

    public void queryUpdateData() {
        FirebaseDB.getGames().whereEqualTo(this.getResources().getString(R.string.firebase_firestore_variable_games_gameID), FirebaseDB.getGameData().getGameID())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        updateData(documentSnapshot);
                    } else {
                        Log.d(this.getResources().getString(R.string.firebase_firestore_update_db_tag),
                                this.getResources().getString(R.string.firebase_firestore_current_data_null));
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.firebase_firestore_could_not_find_document_with_gameid,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.firebase_firestore_could_not_query_database,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addSnapshotListener() {
        FirebaseDB.getGames().whereEqualTo(this.getResources().getString(R.string.firebase_firestore_variable_games_gameID), FirebaseDB.getGameData().getGameID())
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
                        Log.d(this.getResources().getString(R.string.firebase_firestore_update_db_tag),
                                this.getResources().getString(R.string.firebase_firestore_current_data_null));
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.firebase_firestore_could_not_find_document_with_gameid,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.firebase_firestore_could_not_query_database,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateData(@NonNull final DocumentSnapshot documentSnapshot) {
        FirebaseDB.setGameData(documentSnapshot.toObject(GameData.class));
        if (mapFragment != null)
            mapFragment.setMarker();
        ArrayList<ChatMessage> chatMessages = FirebaseDB.getGameData().getChatMessages();
        if (chatFragment != null && FirebaseDB.getGameData() != null && chatMessages != null) {
            chatFragment.setRecyclerView(chatMessages);
            if (chatMessages.size() > 0) {
                ChatMessage chatMessage = chatMessages.get(chatMessages.size() - 1);
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle(chatMessage.getNickName() + ": " + chatMessage.getText());
                toolbar.setOnClickListener(v -> {
                    if (menu != null)
                        getMenuInflater().inflate(R.menu.main, menu);
                    final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    if (currentFragment != null) fragmentTransaction.detach(currentFragment);
                    fragmentTransaction.attach(chatFragment);
                    currentFragment = chatFragment;
                    fragmentTransaction.commit();
                });
            }
        }
        if (MapFragment.getOverlayImageTitle() != null)
            FirebaseDB.getOverlayImages().whereEqualTo(this.getResources().getString(R.string.firebase_firestore_variable_overlay_images_name), MapFragment.getOverlayImageTitle()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        final DocumentSnapshot documentSnapshot1 = task.getResult().getDocuments().get(0);
                        MapFragment.setOverlayImage(documentSnapshot1.toObject(OverlayImage.class));
                    }
                } else {
                    Toast.makeText(this, R.string.code_main_activity_please_select_image_first, Toast.LENGTH_LONG).show();
                }
            });

    }

    public void requestLocationPermissions() {
        if (alreadyRequestingLocationPermissions) return;
        alreadyRequestingLocationPermissions = true;
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

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
            case R.id.showKmlLayer:
                if (!mapFragment.showKmlLayer) {
                    item.setChecked(true);
                    mapFragment.showKmlLayer = true;
                } else {
                    item.setChecked(false);
                    mapFragment.showKmlLayer = false;
                }
                break;
            case R.id.showHeatMap:
                if (!mapFragment.showHeatMap) {
                    item.setChecked(true);
                    mapFragment.showHeatMap = true;
                } else {
                    item.setChecked(false);
                    mapFragment.showHeatMap = false;
                }
                break;
            case R.id.showFirstRadials:
                if (!mapFragment.showFirstRadials) {
                    item.setChecked(true);
                    mapFragment.showFirstRadials = true;
                } else {
                    item.setChecked(false);
                    mapFragment.showFirstRadials = false;
                }
                break;
            case R.id.showSecondRadials:
                if (!mapFragment.showSecondRadials) {
                    item.setChecked(true);
                    mapFragment.showSecondRadials = true;
                } else {
                    item.setChecked(false);
                    mapFragment.showSecondRadials = false;
                }
        }

        mapFragment.setMarker();
        if (teamFragment.getRecyclerView() != null && FirebaseDB.getGameData().getTeams() != null)
            teamFragment.setRecyclerView(FirebaseDB.getGameData().getTeams(), teamFragment.getRecyclerView());
        return true;

        //noinspection SimplifiableIfStatement

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (currentFragment != null) fragmentTransaction.detach(currentFragment);

        if (mapFragment == null) mapFragment = new MapFragment();
        if (playerFragment == null) playerFragment = new PlayerFragment();
        if (chatFragment == null) chatFragment = new ChatFragment();
        if (settingsFragment == null) settingsFragment = new SettingsFragment();
        if (teamFragment == null) teamFragment = new TeamFragment();
        if (gameIDFragment == null) gameIDFragment = new GameIDFragment();

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
                getMenuInflater().inflate(R.menu.main, menu);
            fragmentTransaction.attach(chatFragment);
            currentFragment = chatFragment;
        } else if (id == R.id.nav_settings) {
            if (menu != null)
                getMenuInflater().inflate(R.menu.main, menu);
            fragmentTransaction.attach(settingsFragment);
            currentFragment = settingsFragment;
        } else if (id == R.id.nav_overlay_images) {
            if (menu != null)
                getMenuInflater().inflate(R.menu.main, menu);
            fragmentTransaction.attach(overlayImagesFragment);
            currentFragment = overlayImagesFragment;
        }
//        else if(id == R.id.nav_beer){
//            if(menu != null)
//                getMenuInflater().inflate(R.menu.main, menu);
//            fragmentTransaction.attach(beerFragment);
//            currentFragment = beerFragment;
//        }
        else if (id == R.id.nav_gameID) {
            if (menu != null)
                getMenuInflater().inflate(R.menu.main, menu);
            fragmentTransaction.attach(gameIDFragment);
            currentFragment = gameIDFragment;
        }

        fragmentTransaction.commit();
        queryUpdateData();
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(final Uri uri) {
    }
}
