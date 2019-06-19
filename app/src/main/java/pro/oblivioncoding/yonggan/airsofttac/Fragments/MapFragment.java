package pro.oblivioncoding.yonggan.airsofttac.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.GameData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.FlagMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.HQMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.MissionMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.RespawnMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.TacticalMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Teams.TeamData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.OrgaAddMarkerDialogFragment;
import pro.oblivioncoding.yonggan.airsofttac.InfoWindowAdapter.CustomMarkerInfoWindowAdapter;
import pro.oblivioncoding.yonggan.airsofttac.InfoWindowAdapter.CustomMarkerOwnInfoWindowAdapter;
import pro.oblivioncoding.yonggan.airsofttac.InfoWindowAdapter.CustomMarkerTeamInfoWindowAdapter;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    @Nullable
    private PlayerFragment.OnFragmentInteractionListener mListener;

    private GoogleMap googleMap;

    private FloatingActionButton currentlocationfb;

    private static FloatingActionButton hitfb, underfirefb, supportfb, missionfb;

    private static FloatingActionButton reloadfb, setMarkerfb, removeMarkerfb, swapFlagfb;
    public ShowSettings showSettings = ShowSettings.AllPlayer;
    private Marker currentMarker;
    private View rootView;
    @NonNull
    private HashMap<Marker, TacticalMarkerData> tacticalMarkerDataHashMap = new HashMap<>();
    @NonNull
    private HashMap<Marker, MissionMarkerData> missionMarkerDataHashMap = new HashMap<>();
    @NonNull
    private HashMap<Marker, RespawnMarkerData> respawnMarkerDataHashMap = new HashMap<>();
    @NonNull
    private HashMap<Marker, FlagMarkerData> flagDataHashMap = new HashMap<>();
    @NonNull
    private HashMap<Marker, HQMarkerData> hqMarkerDataHashMap = new HashMap<>();
    private HashMap<Marker, UserData> userMarkerDataHashMap = new HashMap<>();
    private HashMap<UserData, Polyline> userMarkerPolyline = new HashMap<>();

    public enum ShowSettings {
        AllPlayer, ShowTeamOnly, ShowOnlyNotAssigned
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        //This is for loading the Map
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this::onMapReady);
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        currentlocationfb = rootView.findViewById(R.id.currentlocationfb);
        final UserData ownUserData = FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication
                .getFirebaseUser().getEmail());
        currentlocationfb.setOnClickListener(v -> {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                    ownUserData.getPositionLat(),
                    ownUserData.getPositionLong())));
        });

        hitfb = rootView.findViewById(R.id.hitfb);
        underfirefb = rootView.findViewById(R.id.underfirefb);
        supportfb = rootView.findViewById(R.id.supportfb);
        missionfb = rootView.findViewById(R.id.missionfb);

        reloadfb = rootView.findViewById(R.id.reloadfb);
        setMarkerfb = rootView.findViewById(R.id.setMarker);
        removeMarkerfb = rootView.findViewById(R.id.removeMarker);
        swapFlagfb = rootView.findViewById(R.id.swapFlagMarker);
        setMarkerfb.hide();
        removeMarkerfb.hide();
        swapFlagfb.hide();
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
                            showAfterTime(hitfb, 3000L);
                        } else {
                            missionfb.hide();
                            supportfb.hide();
                            underfirefb.hide();
                            hitfb.setImageResource(R.drawable.ic_fb_healed);
                            hitfb.hide();
                            showAfterTime(hitfb, 3000L);
                        }
                    } else {
                        Toast.makeText(getContext(), "Couldn´t find Document with GameID!",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG).show();
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
                            showAfterTime(underfirefb, 3000L);
                        } else {
                            missionfb.show();
                            underfirefb.setImageResource(R.drawable.ic_fb_under_fire);
                            underfirefb.hide();
                            showAfterTime(underfirefb, 3000L);
                        }
                    } else {
                        Toast.makeText(getContext(), "Couldn´t find Document with GameID!",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG).show();
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
                            showAfterTime(supportfb, 3000L);
                        } else {
                            missionfb.show();
                            supportfb.setImageResource(R.drawable.ic_fb_support);
                            supportfb.hide();
                            showAfterTime(supportfb, 3000L);
                        }
                    } else {
                        Toast.makeText(getContext(), "Couldn´t find Document with GameID!",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG).show();
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
                            showAfterTime(missionfb, 3000L);
                        } else {
                            underfirefb.show();
                            supportfb.show();
                            hitfb.show();
                            missionfb.setImageResource(R.drawable.ic_fb_mission);
                            missionfb.hide();
                            showAfterTime(missionfb, 3000L);
                        }
                    } else {
                        Toast.makeText(getContext(), "Couldn´t find Document with GameID!",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG).show();
                }
            });
        });

        reloadfb.setOnClickListener(v -> {
            reloadfb.hide();
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
                        Toast.makeText(getContext(), "Couldn´t find Document with GameID!",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG).show();
                }
            });
            showAfterTime(reloadfb, 30000L);
        });
        return rootView;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setCompassEnabled(true);

        final UserData ownUserData = FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication
                .getFirebaseUser().getEmail());

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                ownUserData.getPositionLat(),
                ownUserData.getPositionLong())));

        this.googleMap = googleMap;

        googleMap.setOnMarkerClickListener(marker ->
        {
            if (userMarkerDataHashMap.containsKey(marker)) {
                final UserData userData = userMarkerDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerTeamInfoWindowAdapter(getContext(),
                        userData.getPositionLat(), userData.getPositionLong(),
                        userData.getEmail(), userData.getEmail(), userData.getTeam()));
            } else if (tacticalMarkerDataHashMap.containsKey(marker)) {
                final TacticalMarkerData tacticalMarkerData = tacticalMarkerDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerInfoWindowAdapter(getContext(),
                        tacticalMarkerData.getLatitude(), tacticalMarkerData.getLongitude(),
                        tacticalMarkerData.getTitle(), tacticalMarkerData.getDescription()));
                currentMarker = marker;
            } else if (missionMarkerDataHashMap.containsKey(marker)) {
                final MissionMarkerData missionMarkerData = missionMarkerDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerInfoWindowAdapter(getContext(),
                        missionMarkerData.getLatitude(), missionMarkerData.getLongitude(),
                        missionMarkerData.getTitle(), missionMarkerData.getDescription()));
                currentMarker = marker;
            } else if (respawnMarkerDataHashMap.containsKey(marker)) {
                final RespawnMarkerData respawnMarkerData = respawnMarkerDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerOwnInfoWindowAdapter(getContext(),
                        respawnMarkerData.getLatitude(), respawnMarkerData.getLongitude(),
                        respawnMarkerData.getTitle(), respawnMarkerData.getDescription(), respawnMarkerData.isOwn()));
                currentMarker = marker;
            } else if (hqMarkerDataHashMap.containsKey(marker)) {
                final HQMarkerData hqMarkerData = hqMarkerDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerOwnInfoWindowAdapter(getContext(),
                        hqMarkerData.getLatitude(), hqMarkerData.getLongitude(),
                        hqMarkerData.getTitle(), hqMarkerData.getDescription(), hqMarkerData.isOwn()));
                currentMarker = marker;
            } else if (flagDataHashMap.containsKey(marker)) {
                final FlagMarkerData flagMarkerData = flagDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerOwnInfoWindowAdapter(getContext(),
                        flagMarkerData.getLatitude(), flagMarkerData.getLongitude(),
                        flagMarkerData.getTitle(), flagMarkerData.getDescription(), flagMarkerData.isOwn()));
                swapFlagfb.show();
                currentMarker = marker;
            }
            if (currentMarker != null)
                removeMarkerfb.show();
            else removeMarkerfb.hide();
            marker.showInfoWindow();

            googleMap.setOnMapClickListener(e -> {
                swapFlagfb.hide();
                removeMarkerfb.show();
            });

            return true;
        });

        FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    final DocumentReference documentReference = FirebaseDB.getGames()
                            .document(task.getResult().getDocuments().get(0).getId());

                } else {
                    Toast.makeText(getContext(), "Couldn´t find Document with GameID!",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Couldn´t query Database!",
                        Toast.LENGTH_LONG).show();
            }
        });

        if (FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isOrga()) {
            setMarkerfb.show();
            setMarkerfb.setOnClickListener(v -> {
                OrgaAddMarkerDialogFragment orgaAddMarkerDialogFragment = OrgaAddMarkerDialogFragment.newInstance("New Marker");
                orgaAddMarkerDialogFragment.show(getFragmentManager(), "orga_add_marker_dialog");
            });

            FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        final DocumentReference documentReference = FirebaseDB.getGames()
                                .document(task.getResult().getDocuments().get(0).getId());
                        removeMarkerfb.setOnClickListener(e -> {
                            if (tacticalMarkerDataHashMap.containsKey(currentMarker)) {
                                TacticalMarkerData tacticalMarkerData = tacticalMarkerDataHashMap.get(currentMarker);
                                for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                    if (teamData.getTacticalMarkerData() != null && teamData.getTacticalMarkerData().getTitle().equals(tacticalMarkerData.getTitle())) {
                                        teamData.setTacticalMarkerData(null);
                                    }
                                }
                                FirebaseDB.getGameData().getTacticalMarkerData().remove(tacticalMarkerData);
                                tacticalMarkerDataHashMap.remove(currentMarker);
                            } else if (respawnMarkerDataHashMap.containsKey(currentMarker)) {
                                RespawnMarkerData respawnMarkerData = respawnMarkerDataHashMap.get(currentMarker);
                                for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                    if (teamData.getRespawnMarkerData() != null && teamData.getRespawnMarkerData().getTitle().equals(respawnMarkerData.getTitle()))
                                        teamData.setRespawnMarkerData(null);
                                }
                                FirebaseDB.getGameData().getRespawnMarkerData().remove(respawnMarkerData);
                                respawnMarkerDataHashMap.remove(currentMarker);
                            } else if (missionMarkerDataHashMap.containsKey(currentMarker)) {
                                MissionMarkerData missionMarkerData = missionMarkerDataHashMap.get(currentMarker);
                                for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                    if (teamData.getMissionMarkerData() != null && teamData.getMissionMarkerData().getTitle().equals(missionMarkerData.getTitle()))
                                        teamData.setMissionMarkerData(null);
                                }
                                FirebaseDB.getGameData().getMissionMarkerData().remove(missionMarkerData);
                                missionMarkerDataHashMap.remove(currentMarker);
                            } else if (hqMarkerDataHashMap.containsKey(currentMarker)) {
                                final HQMarkerData hqMarkerData = hqMarkerDataHashMap.get(currentMarker);
                                for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                    if (teamData.getHqMarkerData() != null && teamData.getHqMarkerData().getTitle().equals(hqMarkerData.getTitle()))
                                        teamData.setHqMarkerData(null);
                                }
                                FirebaseDB.getGameData().getHqMarkerData().remove(hqMarkerData);
                                hqMarkerDataHashMap.remove(currentMarker);
                            } else if (flagDataHashMap.containsKey(currentMarker)) {
                                FlagMarkerData flagMarkerData = flagDataHashMap.get(currentMarker);
                                for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                    if (teamData.getFlagMarkerData() != null && teamData.getFlagMarkerData().getTitle().equals(flagMarkerData.getTitle()))
                                        teamData.setFlagMarkerData(null);
                                }
                                FirebaseDB.getGameData().getFlagMarkerData().remove(flagMarkerData);
                                flagDataHashMap.remove(currentMarker);
                            }
                            FirebaseDB.updateObject(documentReference, FirebaseDB.getGameData());
                            currentMarker = null;
                            removeMarkerfb.hide();
                        });

                        swapFlagfb.setOnClickListener(e -> {
                            if (flagDataHashMap.containsKey(currentMarker) && currentMarker != null) {
                                final FlagMarkerData flagMarkerData =
                                        FirebaseDB.getGameData().getFlagMarkerData()
                                                .get(FirebaseDB.getGameData().getFlagMarkerData()
                                                        .indexOf(flagDataHashMap.get(currentMarker)));
                                flagMarkerData.setOwn(!flagMarkerData.isOwn());
                                FirebaseDB.updateObject(documentReference, "flagMarkerData",
                                        FirebaseDB.getGameData().getFlagMarkerData());
                                currentMarker = null;
                                swapFlagfb.hide();
                            }
                        });

                        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                            @Override
                            public void onMarkerDragStart(Marker marker) {
                                marker.hideInfoWindow();
                            }

                            @Override
                            public void onMarkerDrag(Marker marker) {
                                marker.hideInfoWindow();
                            }

                            @Override
                            public void onMarkerDragEnd(@NonNull Marker marker) {
                                if (tacticalMarkerDataHashMap.containsKey(marker)) {
                                    final TacticalMarkerData tacticalMarkerData = tacticalMarkerDataHashMap.get(marker);
                                    tacticalMarkerData.setLatitude(marker.getPosition().latitude);
                                    tacticalMarkerData.setLongitude(marker.getPosition().longitude);
                                    for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                        if (teamData.getTacticalMarkerData() != null && teamData.getTacticalMarkerData().getTitle().equals(tacticalMarkerData.getTitle())) {
                                            teamData.getTacticalMarkerData().setLatitude(marker.getPosition().latitude);
                                            teamData.getTacticalMarkerData().setLongitude(marker.getPosition().longitude);
                                        }
                                    }
                                } else if (missionMarkerDataHashMap.containsKey(marker)) {
                                    final MissionMarkerData missionMarkerData = missionMarkerDataHashMap.get(marker);
                                    missionMarkerData.setLatitude(marker.getPosition().latitude);
                                    missionMarkerData.setLongitude(marker.getPosition().longitude);
                                    for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                        if (teamData.getMissionMarkerData() != null && teamData.getMissionMarkerData().getTitle().equals(missionMarkerData.getTitle())) {
                                            teamData.getMissionMarkerData().setLatitude(marker.getPosition().latitude);
                                            teamData.getMissionMarkerData().setLongitude(marker.getPosition().longitude);
                                        }
                                    }
                                } else if (respawnMarkerDataHashMap.containsKey(marker)) {
                                    final RespawnMarkerData respawnMarkerData = respawnMarkerDataHashMap.get(marker);
                                    respawnMarkerData.setLatitude(marker.getPosition().latitude);
                                    respawnMarkerData.setLongitude(marker.getPosition().longitude);
                                    for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                        if (teamData.getRespawnMarkerData() != null && teamData.getRespawnMarkerData().getTitle().equals(respawnMarkerData.getTitle())) {
                                            teamData.getRespawnMarkerData().setLatitude(marker.getPosition().latitude);
                                            teamData.getRespawnMarkerData().setLongitude(marker.getPosition().longitude);
                                        }
                                    }
                                } else if (hqMarkerDataHashMap.containsKey(marker)) {
                                    final HQMarkerData hqMarkerData = hqMarkerDataHashMap.get(marker);
                                    hqMarkerData.setLatitude(marker.getPosition().latitude);
                                    hqMarkerData.setLongitude(marker.getPosition().longitude);
                                    for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                        if (teamData.getHqMarkerData() != null && teamData.getHqMarkerData().getTitle().equals(hqMarkerData.getTitle())) {
                                            teamData.getHqMarkerData().setLatitude(marker.getPosition().latitude);
                                            teamData.getHqMarkerData().setLongitude(marker.getPosition().longitude);
                                        }
                                    }
                                } else if (flagDataHashMap.containsKey(marker)) {
                                    final FlagMarkerData flagMarkerData = flagDataHashMap.get(marker);
                                    flagMarkerData.setLatitude(marker.getPosition().latitude);
                                    flagMarkerData.setLongitude(marker.getPosition().longitude);
                                    for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                        if (teamData.getFlagMarkerData() != null && teamData.getFlagMarkerData().getTitle().equals(flagMarkerData.getTitle())) {
                                            teamData.getFlagMarkerData().setLatitude(marker.getPosition().latitude);
                                            teamData.getFlagMarkerData().setLongitude(marker.getPosition().longitude);
                                        }
                                    }
                                }
                                FirebaseDB.updateObject(documentReference, FirebaseDB.getGameData());
                                setMarker();
                                marker.showInfoWindow();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Couldn´t find Document with GameID!",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG).show();
                }
            });
        } else {
            setMarkerfb.hide();
            removeMarkerfb.hide();
            swapFlagfb.hide();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlayerFragment.OnFragmentInteractionListener) {
            mListener = (PlayerFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void updateData(DocumentSnapshot documentSnapshot) {
        Log.d("UpdateDB", "Current data: " + documentSnapshot.getData());
        FirebaseDB.setGameData(documentSnapshot.toObject(GameData.class));
        this.setMarker();
    }

    public void setAllPositionMarker() {
        userMarkerDataHashMap.clear();
        userMarkerPolyline.clear();
        for (UserData userData : FirebaseDB.getGameData().getUsers()) {
            if (showSettings.equals(ShowSettings.ShowTeamOnly)) {
                if (userData.getTeam() != null && userData.getTeam().equals(FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).getTeam())) {
                    setPositionMarker(userData);
                    TeamData teamData = null;
                    for (TeamData team : FirebaseDB.getGameData().getTeams()) {
                        if (team.getTeamName().equals(userData.getTeam())) {
                            teamData = team;
                        }
                    }

                    if (teamData != null) {
                        LatLng latLng = null;
                        if (teamData.getFlagMarkerData() != null) {
                            FlagMarkerData flagMarkerData = teamData.getFlagMarkerData();
                            latLng = new LatLng(flagMarkerData.getLatitude(), flagMarkerData.getLongitude());
                        } else if (teamData.getHqMarkerData() != null) {
                            HQMarkerData hqMarkerData = teamData.getHqMarkerData();
                            latLng = new LatLng(hqMarkerData.getLatitude(), hqMarkerData.getLongitude());
                        } else if (teamData.getMissionMarkerData() != null) {
                            MissionMarkerData missionMarkerData = teamData.getMissionMarkerData();
                            latLng = new LatLng(missionMarkerData.getLatitude(), missionMarkerData.getLongitude());
                        } else if (teamData.getRespawnMarkerData() != null) {
                            RespawnMarkerData respawnMarkerData = teamData.getRespawnMarkerData();
                            latLng = new LatLng(respawnMarkerData.getLatitude(), respawnMarkerData.getLongitude());
                        } else if (teamData.getTacticalMarkerData() != null) {
                            TacticalMarkerData tacticalMarkerData = teamData.getTacticalMarkerData();
                            latLng = new LatLng(tacticalMarkerData.getLatitude(), tacticalMarkerData.getLongitude());
                        }

                        if (latLng != null)
                            userMarkerPolyline.put(userData, googleMap.addPolyline(
                                    new PolylineOptions().add(new LatLng(userData.getPositionLat(),
                                            userData.getPositionLong())).add(latLng).color(Color.CYAN).geodesic(true)));
                    }
                }
            } else if (showSettings.equals(ShowSettings.AllPlayer)) {
                setPositionMarker(userData);
                TeamData teamData = null;
                for (TeamData team : FirebaseDB.getGameData().getTeams()) {
                    if (team.getTeamName().equals(userData.getTeam())) {
                        teamData = team;
                    }
                }

                if (teamData != null) {
//                        Log.i("Poly", "!showTeamAssignOnly");
                    LatLng latLng = null;
                    if (teamData.getFlagMarkerData() != null) {
                        FlagMarkerData flagMarkerData = teamData.getFlagMarkerData();
                        latLng = new LatLng(flagMarkerData.getLatitude(), flagMarkerData.getLongitude());
                    } else if (teamData.getHqMarkerData() != null) {
                        HQMarkerData hqMarkerData = teamData.getHqMarkerData();
                        latLng = new LatLng(hqMarkerData.getLatitude(), hqMarkerData.getLongitude());
                    } else if (teamData.getMissionMarkerData() != null) {
                        MissionMarkerData missionMarkerData = teamData.getMissionMarkerData();
                        latLng = new LatLng(missionMarkerData.getLatitude(), missionMarkerData.getLongitude());
                    } else if (teamData.getRespawnMarkerData() != null) {
                        RespawnMarkerData respawnMarkerData = teamData.getRespawnMarkerData();
                        latLng = new LatLng(respawnMarkerData.getLatitude(), respawnMarkerData.getLongitude());
                    } else if (teamData.getTacticalMarkerData() != null) {
                        TacticalMarkerData tacticalMarkerData = teamData.getTacticalMarkerData();
                        latLng = new LatLng(tacticalMarkerData.getLatitude(), tacticalMarkerData.getLongitude());
                    }

                    if (latLng != null)
                        userMarkerPolyline.put(userData, googleMap.addPolyline(
                                new PolylineOptions().add(new LatLng(userData.getPositionLat(),
                                        userData.getPositionLong())).add(latLng).color(Color.CYAN).geodesic(true)));
                }
            } else if (showSettings.equals(ShowSettings.ShowOnlyNotAssigned)) {
                TeamData teamData = null;
                for (TeamData team : FirebaseDB.getGameData().getTeams()) {
                    if (team.getTeamName().equals(userData.getTeam())) {
                        teamData = team;
                    }
                }
                if (teamData != null) {
                    if (teamData.getTacticalMarkerData() == null
                            && teamData.getRespawnMarkerData() == null
                            && teamData.getMissionMarkerData() == null
                            && teamData.getHqMarkerData() == null
                            && teamData.getFlagMarkerData() == null)
                        setPositionMarker(userData);
                }
            }
        }

    }

    private void setPositionMarker(UserData userData) {
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(userData.getPositionLat(), userData.getPositionLong()));
        setMarkerIcon(markerOptions, userData);
        userMarkerDataHashMap.put(googleMap.addMarker(markerOptions), userData);
    }

    public void setMarker() {
        if (googleMap != null) {
            googleMap.clear();
            setAllPositionMarker();

            setAlTacticalMarker();
            setAllMissionMarker();
            setAllHQMarker();
            setAllRespawnMarker();
            setAllFlagMarker();
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setAlTacticalMarker() {
        tacticalMarkerDataHashMap.clear();
        if (FirebaseDB.getGameData().getTacticalMarkerData() != null) {
            for (TacticalMarkerData tacticalMarkerData : FirebaseDB.getGameData().getTacticalMarkerData()) {
                final MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(tacticalMarkerData.getLatitude(), tacticalMarkerData.getLongitude()));
                markerOptions.draggable(true);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                tacticalMarkerDataHashMap.put(googleMap.addMarker(markerOptions), tacticalMarkerData);
            }
        }
    }

    public void setAllMissionMarker() {
        missionMarkerDataHashMap.clear();
        if (FirebaseDB.getGameData().getMissionMarkerData() != null) {
            for (MissionMarkerData missionMarkerData : FirebaseDB.getGameData().getMissionMarkerData()) {
                final MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(missionMarkerData.getLatitude(), missionMarkerData.getLongitude()));
                markerOptions.draggable(true);
                markerOptions.icon(getBitmapDescriptor(R.drawable.ic_missionicon));
                missionMarkerDataHashMap.put(googleMap.addMarker(markerOptions), missionMarkerData);
            }
        }
    }

    public void setAllRespawnMarker() {
        respawnMarkerDataHashMap.clear();
        if (FirebaseDB.getGameData().getRespawnMarkerData() != null) {
            for (RespawnMarkerData respawnMarkerData : FirebaseDB.getGameData().getRespawnMarkerData()) {
                final MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(respawnMarkerData.getLatitude(), respawnMarkerData.getLongitude()));
                markerOptions.draggable(true);
                if (respawnMarkerData.isOwn())
                    markerOptions.icon(getBitmapDescriptor(R.drawable.ic_respawnicon));
                else markerOptions.icon(getBitmapDescriptor(R.drawable.ic_respawn_red));
                respawnMarkerDataHashMap.put(googleMap.addMarker(markerOptions), respawnMarkerData);
            }
        }
    }

    public void setAllHQMarker() {
        hqMarkerDataHashMap.clear();
        if (FirebaseDB.getGameData().getHqMarkerData() != null) {
            for (HQMarkerData hqMarkerData : FirebaseDB.getGameData().getHqMarkerData()) {
                final MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(hqMarkerData.getLatitude(), hqMarkerData.getLongitude()));
                markerOptions.draggable(true);
                if (hqMarkerData.isOwn())
                    markerOptions.icon(getBitmapDescriptor(R.drawable.ic_hqicon));
                else markerOptions.icon(getBitmapDescriptor(R.drawable.ic_hqicon_red));
                hqMarkerDataHashMap.put(googleMap.addMarker(markerOptions), hqMarkerData);
            }
        }
    }

    public void setAllFlagMarker() {
        flagDataHashMap.clear();
        if (FirebaseDB.getGameData().getFlagMarkerData() != null) {
            for (FlagMarkerData flagMarkerData : FirebaseDB.getGameData().getFlagMarkerData()) {
                final MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(flagMarkerData.getLatitude(), flagMarkerData.getLongitude()));
                markerOptions.draggable(true);
                if (flagMarkerData.isOwn())
                    markerOptions.icon(getBitmapDescriptor(R.drawable.ic_flagicon));
                else markerOptions.icon(getBitmapDescriptor(R.drawable.ic_flagicon_red));
                flagDataHashMap.put(googleMap.addMarker(markerOptions), flagMarkerData);
            }
        }
    }


    private void setMarkerIcon(@NonNull MarkerOptions markerOptions, UserData userData) {
        Log.i("Marker", "Setting Icon of Marker");
        if (userData.isMission()) {
            markerOptions.icon(getBitmapDescriptor(R.drawable.ic_marker_alivemission));
        } else if (userData.isAlive() && userData.isUnderfire() && userData.isSupport()) {
            markerOptions.icon(getBitmapDescriptor(R.drawable.ic_marker_aliveunderfiresupport));
        } else if (userData.isAlive() && userData.isUnderfire() && !userData.isSupport()) {
            markerOptions.icon(getBitmapDescriptor(R.drawable.ic_marker_aliveunderfire));
        } else if (userData.isAlive() && !userData.isUnderfire() && userData.isSupport()) {
            markerOptions.icon(getBitmapDescriptor(R.drawable.ic_marker_alivenotunderfiresupport));
        } else if (userData.isAlive() && !userData.isUnderfire() && !userData.isSupport()) {
            markerOptions.icon(getBitmapDescriptor(R.drawable.ic_marker_alivenotunderfire));
        } else if (!userData.isAlive()) {
            markerOptions.icon(getBitmapDescriptor(R.drawable.ic_marker_notalivenotunderfire));
        }
    }

    private BitmapDescriptor getBitmapDescriptor(int id) {
        Drawable vectorDrawable = getResources().getDrawable(id);
        int h = ((int) dpTopixel(getContext(), 50));
        int w = ((int) dpTopixel(getContext(), 50));
        vectorDrawable.setBounds(0, 0, w, h);
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    private static float dpTopixel(Context c, float dp) {
        float density = c.getResources().getDisplayMetrics().density;
        float pixel = dp * density;
        return pixel;
    }

    private void showAfterTime(FloatingActionButton button, long delay){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
                    button.show();
                });
            }
        }, delay);
    }
}

