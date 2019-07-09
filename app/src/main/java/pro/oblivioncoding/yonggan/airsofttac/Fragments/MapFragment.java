package pro.oblivioncoding.yonggan.airsofttac.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.pengrad.mapscaleview.MapScaleView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.data.kml.KmlGroundOverlay;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import pro.oblivioncoding.yonggan.airsofttac.Activitys.MainActivity;
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
import pro.oblivioncoding.yonggan.airsofttac.Firebase.KMLCollection.KMLData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.OverlayImageCollection.OverlayImage;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.GotoSearchMarkerSelection;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.GotoSearchPlayerSelection;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.OrgaAddMarkerDialogFragment;
import pro.oblivioncoding.yonggan.airsofttac.InfoWindowAdapter.CustomMarkerInfoWindowAdapter;
import pro.oblivioncoding.yonggan.airsofttac.InfoWindowAdapter.CustomMarkerOwnInfoWindowAdapter;
import pro.oblivioncoding.yonggan.airsofttac.InfoWindowAdapter.CustomMarkerTeamInfoWindowAdapter;
import pro.oblivioncoding.yonggan.airsofttac.MapUtils.ClusterMarkerItem;
import pro.oblivioncoding.yonggan.airsofttac.R;
import pro.oblivioncoding.yonggan.airsofttac.Services.GoogleLocationService;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static GoogleMap googleMap;
    private static MapScaleView scaleView;
    private static ImageButton hitfb, underfirefb, supportfb, missionfb;
    private static ImageButton setMarkerfb, removeMarkerfb, swapFlagfb;
    private static TextView rotationDegrees;
    private static ImageButton reloadfb, currentlocationfb, toggleMap, toggleMapRotation;
    private static ImageButton gotoFirstHQSelection, gotoPlayerSelection, gotoMarkerSelection;

    private static int MapType = GoogleMap.MAP_TYPE_HYBRID;
    private static MapStyleOptions mapStyleOptions;
    @NonNull
    public ShowSettings showSettings = ShowSettings.AllPlayer;
    public boolean showKmlLayer = true, showHeatMap = false;
    public boolean showFirstRadials = true, showSecondRadials = true;

    @Nullable
    private PlayerFragment.OnFragmentInteractionListener mListener;
    @Nullable
    private Marker currentMarker;
    private View rootView;

    public static TextView getRotationDegrees() {
        return rotationDegrees;
    }

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
    @NonNull
    private HashMap<Marker, UserData> userMarkerDataHashMap = new HashMap<>();
    @NonNull
    private HashMap<UserData, Polyline> userMarkerPolyline = new HashMap<>();

    private HashMap<UserData, Circle> userDataCircleFirstHashMap = new HashMap<>();

    private HashMap<UserData, Circle> userDataCircleSecondHashMap = new HashMap<>();

    private static OverlayImage overlayImage;
    private static String overlayImageTitle;

    public static void setOverlayImage(OverlayImage overlayImage) {
        MapFragment.overlayImage = overlayImage;
    }

    public static String getOverlayImageTitle() {
        return overlayImageTitle;
    }

    public static void setOverlayImageTitle(String overlayImageTitle) {
        MapFragment.overlayImageTitle = overlayImageTitle;
    }

    private KmlLayer kmlLayer;
    @NonNull
    private ArrayList<Marker> kmlMarker = new ArrayList<>();
    @NonNull
    private ArrayList<GroundOverlay> groundOverlays = new ArrayList<>();
    @Nullable
    private ClusterManager<ClusterMarkerItem> clusterManager;

    public static GoogleMap getGoogleMap() {
        return googleMap;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static MapFragment newInstance() {
        final MapFragment fragment = new MapFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private static float dpTopixel(final Context c, final float dp) {
        final float density = c.getResources().getDisplayMetrics().density;
        final float pixel = dp * density;
        return pixel;
    }

    public static void setMapStyle(final String json) {
        if (googleMap != null) {
            mapStyleOptions = new MapStyleOptions(json);
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        //This is for loading the Map
        final SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this::onMapReady);
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        currentlocationfb = rootView.findViewById(R.id.currentlocationfb);


        scaleView = rootView.findViewById(R.id.scaleView);
        toggleMap = rootView.findViewById(R.id.togglemapfb);
        toggleMapRotation = rootView.findViewById(R.id.togglemaprotationfb);
        rotationDegrees = rootView.findViewById(R.id.rotationDegrees);
        hitfb = rootView.findViewById(R.id.hitfb);
        underfirefb = rootView.findViewById(R.id.underfirefb);
        supportfb = rootView.findViewById(R.id.supportfb);
        missionfb = rootView.findViewById(R.id.missionfb);

        reloadfb = rootView.findViewById(R.id.reloadfb);
        setMarkerfb = rootView.findViewById(R.id.setMarker);
        removeMarkerfb = rootView.findViewById(R.id.removeMarker);
        swapFlagfb = rootView.findViewById(R.id.swapFlagMarker);

        gotoFirstHQSelection = rootView.findViewById(R.id.gotoFirstHQ);
        gotoMarkerSelection = rootView.findViewById(R.id.gotoMarkerSelection);
        gotoPlayerSelection = rootView.findViewById(R.id.gotoPlayerSelection);


        setMarkerfb.setVisibility(View.INVISIBLE);
        removeMarkerfb.setVisibility(View.INVISIBLE);
        swapFlagfb.setVisibility(View.INVISIBLE);

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
                            missionfb.setVisibility(View.VISIBLE);
                            supportfb.setVisibility(View.VISIBLE);
                            underfirefb.setVisibility(View.VISIBLE);
                            hitfb.setImageResource(R.drawable.ic_fb_hit);
                            hitfb.setVisibility(View.INVISIBLE);
                            showAfterTime(hitfb, 3000L);
                        } else {
                            missionfb.setVisibility(View.INVISIBLE);
                            supportfb.setVisibility(View.INVISIBLE);
                            underfirefb.setVisibility(View.INVISIBLE);
                            hitfb.setImageResource(R.drawable.ic_fb_healed);
                            hitfb.setVisibility(View.INVISIBLE);
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
                            missionfb.setVisibility(View.INVISIBLE);
                            underfirefb.setImageResource(R.drawable.ic_fb_not_underfire);
                            underfirefb.setVisibility(View.INVISIBLE);
                            showAfterTime(underfirefb, 3000L);
                        } else {
                            missionfb.setVisibility(View.VISIBLE);
                            underfirefb.setImageResource(R.drawable.ic_fb_under_fire);
                            underfirefb.setVisibility(View.INVISIBLE);
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
                            missionfb.setVisibility(View.INVISIBLE);
                            supportfb.setImageResource(R.drawable.ic_fb_no_support);
                            supportfb.setVisibility(View.INVISIBLE);
                            showAfterTime(supportfb, 3000L);
                        } else {
                            missionfb.setVisibility(View.VISIBLE);
                            supportfb.setImageResource(R.drawable.ic_fb_support);
                            supportfb.setVisibility(View.INVISIBLE);
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
                            underfirefb.setVisibility(View.INVISIBLE);
                            supportfb.setVisibility(View.INVISIBLE);
                            hitfb.setVisibility(View.INVISIBLE);
                            missionfb.setImageResource(R.drawable.ic_fb_mission_success);
                            missionfb.setVisibility(View.INVISIBLE);
                            showAfterTime(missionfb, 3000L);
                        } else {
                            underfirefb.setVisibility(View.VISIBLE);
                            supportfb.setVisibility(View.VISIBLE);
                            hitfb.setVisibility(View.VISIBLE);
                            missionfb.setImageResource(R.drawable.ic_fb_mission);
                            missionfb.setVisibility(View.INVISIBLE);
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
            MainActivity.getInstance().getGoogleLocationService().requestLocation();
            reloadfb.setVisibility(View.INVISIBLE);
            FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            MainActivity.getInstance().getGoogleLocationService().updateLastKnowLocation();
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(final Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof PlayerFragment.OnFragmentInteractionListener) {
            mListener = (PlayerFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void updateData(final DocumentSnapshot documentSnapshot) {
        Log.d("UpdateDB", "Current data: " + documentSnapshot.getData());
        FirebaseDB.setGameData(documentSnapshot.toObject(GameData.class));
        this.setMarker();
    }

    public void setAllPositionMarker() {
        userMarkerDataHashMap.clear();
        userDataCircleFirstHashMap.clear();
        userDataCircleSecondHashMap.clear();
        userMarkerPolyline.clear();
        for (final UserData userData : FirebaseDB.getGameData().getUsers()) {
            if (showSettings.equals(ShowSettings.ShowTeamOnly)) {
                if (userData.getTeam() != null && userData.getTeam().equals(FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).getTeam())) {
                    setPositionMarker(userData);
                    TeamData teamData = null;
                    for (final TeamData team : FirebaseDB.getGameData().getTeams()) {
                        if (team.getTeamName().equals(userData.getTeam())) {
                            teamData = team;
                        }
                    }

                    if (teamData != null) {
                        LatLng latLng = null;
                        if (teamData.getFlagMarkerData() != null) {
                            final FlagMarkerData flagMarkerData = teamData.getFlagMarkerData();
                            latLng = new LatLng(flagMarkerData.getLatitude(), flagMarkerData.getLongitude());
                        } else if (teamData.getHqMarkerData() != null) {
                            final HQMarkerData hqMarkerData = teamData.getHqMarkerData();
                            latLng = new LatLng(hqMarkerData.getLatitude(), hqMarkerData.getLongitude());
                        } else if (teamData.getMissionMarkerData() != null) {
                            final MissionMarkerData missionMarkerData = teamData.getMissionMarkerData();
                            latLng = new LatLng(missionMarkerData.getLatitude(), missionMarkerData.getLongitude());
                        } else if (teamData.getRespawnMarkerData() != null) {
                            final RespawnMarkerData respawnMarkerData = teamData.getRespawnMarkerData();
                            latLng = new LatLng(respawnMarkerData.getLatitude(), respawnMarkerData.getLongitude());
                        } else if (teamData.getTacticalMarkerData() != null) {
                            final TacticalMarkerData tacticalMarkerData = teamData.getTacticalMarkerData();
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
                for (final TeamData team : FirebaseDB.getGameData().getTeams()) {
                    if (team.getTeamName().equals(userData.getTeam())) {
                        teamData = team;
                    }
                }

                if (teamData != null) {
                    LatLng latLng = null;
                    if (teamData.getFlagMarkerData() != null) {
                        final FlagMarkerData flagMarkerData = teamData.getFlagMarkerData();
                        latLng = new LatLng(flagMarkerData.getLatitude(), flagMarkerData.getLongitude());
                    } else if (teamData.getHqMarkerData() != null) {
                        final HQMarkerData hqMarkerData = teamData.getHqMarkerData();
                        latLng = new LatLng(hqMarkerData.getLatitude(), hqMarkerData.getLongitude());
                    } else if (teamData.getMissionMarkerData() != null) {
                        final MissionMarkerData missionMarkerData = teamData.getMissionMarkerData();
                        latLng = new LatLng(missionMarkerData.getLatitude(), missionMarkerData.getLongitude());
                    } else if (teamData.getRespawnMarkerData() != null) {
                        final RespawnMarkerData respawnMarkerData = teamData.getRespawnMarkerData();
                        latLng = new LatLng(respawnMarkerData.getLatitude(), respawnMarkerData.getLongitude());
                    } else if (teamData.getTacticalMarkerData() != null) {
                        final TacticalMarkerData tacticalMarkerData = teamData.getTacticalMarkerData();
                        latLng = new LatLng(tacticalMarkerData.getLatitude(), tacticalMarkerData.getLongitude());
                    }

                    if (latLng != null)
                        userMarkerPolyline.put(userData, googleMap.addPolyline(
                                new PolylineOptions().add(new LatLng(userData.getPositionLat(),
                                        userData.getPositionLong())).add(latLng).color(Color.CYAN).geodesic(true)));
                }
            } else if (showSettings.equals(ShowSettings.ShowOnlyNotAssigned)) {
                TeamData teamData = null;
                for (final TeamData team : FirebaseDB.getGameData().getTeams()) {
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

            if (showFirstRadials && userData.isFirst()) {
                userDataCircleFirstHashMap.put(userData, googleMap.addCircle(new CircleOptions().center(new LatLng(userData.getPositionLat(),
                        userData.getPositionLong())).radius(100).fillColor(Color.argb(100, 50, 50, 255))
                        .strokeColor(Color.WHITE)));
            }

            if (showSecondRadials && userData.isSecond()) {
                userDataCircleSecondHashMap.put(userData, googleMap.addCircle(new CircleOptions().center(new LatLng(userData.getPositionLat(),
                        userData.getPositionLong())).radius(50).fillColor(Color.argb(100, 255, 150, 50))
                        .strokeColor(Color.WHITE)));
            }
        }

    }

    public void setMarker() {
        if (googleMap != null) {
            googleMap.clear();
            addKmlLayer();
            showHeatMap();
//            setClusterItems();
            showImageGroundOverlay();
            setAllPositionMarker();

            setAlTacticalMarker();
            setAllMissionMarker();
            setAllHQMarker();
            setAllRespawnMarker();
            setAllFlagMarker();
        }
    }

    private void setPositionMarker(final UserData userData) {
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(userData.getPositionLat(), userData.getPositionLong()));
        setMarkerIcon(markerOptions, userData);
        userMarkerDataHashMap.put(googleMap.addMarker(markerOptions), userData);
    }

    public void setAlTacticalMarker() {
        tacticalMarkerDataHashMap.clear();
        if (FirebaseDB.getGameData().getTacticalMarkerData() != null) {
            for (final TacticalMarkerData tacticalMarkerData : FirebaseDB.getGameData().getTacticalMarkerData()) {
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
            for (final MissionMarkerData missionMarkerData : FirebaseDB.getGameData().getMissionMarkerData()) {
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
            for (final RespawnMarkerData respawnMarkerData : FirebaseDB.getGameData().getRespawnMarkerData()) {
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
            for (final HQMarkerData hqMarkerData : FirebaseDB.getGameData().getHqMarkerData()) {
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
            for (final FlagMarkerData flagMarkerData : FirebaseDB.getGameData().getFlagMarkerData()) {
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

    private void setMarkerIcon(@NonNull final MarkerOptions markerOptions, final UserData userData) {
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

    private BitmapDescriptor getBitmapDescriptor(final int id) {
        final Drawable vectorDrawable = getResources().getDrawable(id);
        final int h = ((int) dpTopixel(getContext(), 50));
        final int w = ((int) dpTopixel(getContext(), 50));
        vectorDrawable.setBounds(0, 0, w, h);
        final Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    private void showAfterTime(@NonNull final ImageButton button, final long delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
                    button.setVisibility(View.VISIBLE);
                });
            }
        }, delay);
    }

    @Override
    public void onMapReady(@NonNull final GoogleMap googleMap) {
        MapFragment.googleMap = googleMap;

        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);


        final UserData ownUserData = FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication
                .getFirebaseUser().getEmail());

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                ownUserData.getPositionLat(),
                ownUserData.getPositionLong())));

        googleMap.setMapType(MapType);

        currentlocationfb.setOnClickListener(v -> {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                    ownUserData.getPositionLat(),
                    ownUserData.getPositionLong())));
        });


        toggleMap.setOnClickListener(v -> {
            switch (MapType) {
                case GoogleMap.MAP_TYPE_HYBRID:
                    googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    MapType = GoogleMap.MAP_TYPE_TERRAIN;
                    break;
                case GoogleMap.MAP_TYPE_TERRAIN:
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    MapType = GoogleMap.MAP_TYPE_SATELLITE;
                    break;
                case GoogleMap.MAP_TYPE_SATELLITE:
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    MapType = GoogleMap.MAP_TYPE_NORMAL;
                    break;
                case GoogleMap.MAP_TYPE_NORMAL:
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                    MapType = GoogleMap.MAP_TYPE_NONE;
                    break;
                case GoogleMap.MAP_TYPE_NONE:
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    MapType = GoogleMap.MAP_TYPE_HYBRID;
                    break;
            }
        });

        toggleMapRotation.setOnClickListener(v -> {
            GoogleLocationService.mapsRotate = !GoogleLocationService.mapsRotate;
            if (GoogleLocationService.mapsRotate) {
                rotationDegrees.setVisibility(View.VISIBLE);
            } else {
                rotationDegrees.setVisibility(View.INVISIBLE);
            }
        });

        gotoFirstHQSelection.setOnClickListener(v -> {
            if (FirebaseDB.getGameData().getHqMarkerData().size() > 0) {
                HQMarkerData hqMarkerData = FirebaseDB.getGameData().getHqMarkerData().get(0);
                if (hqMarkerData != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                            hqMarkerData.getLatitude(),
                            hqMarkerData.getLongitude())));
                }
            }
        });

        gotoPlayerSelection.setOnClickListener(v -> {
            final GotoSearchPlayerSelection gotoSearchPlayerSelection = GotoSearchPlayerSelection.newInstance("Search Goto Player");
            gotoSearchPlayerSelection.show(getFragmentManager(), "goto_search_player_selection");
        });

        gotoMarkerSelection.setOnClickListener(v -> {
            final GotoSearchMarkerSelection gotoSearchMarkerSelection = GotoSearchMarkerSelection.newInstance("Search Goto Marker");
            gotoSearchMarkerSelection.show(getFragmentManager(), "goto_search_marker_selection");
        });


        if (mapStyleOptions != null) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setMapStyle(mapStyleOptions);
        }

        addKmlLayer();
//        setClusterManager();

        googleMap.setOnMarkerClickListener(marker ->
        {
            Log.d("DDDD", "FFFFFF");
            final float[] distanceResults = new float[1];
            Location.distanceBetween(ownUserData.getPositionLat(), ownUserData.getPositionLong(),
                    marker.getPosition().latitude, marker.getPosition().longitude, distanceResults);
            final String distance = distanceResults[0] + "m";
            if (userMarkerDataHashMap.containsKey(marker)) {
                final UserData userData = userMarkerDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerTeamInfoWindowAdapter(getContext(),
                        userData.getPositionLat(), userData.getPositionLong(),
                        userData.getEmail(), userData.getNickname(), userData.getTeam(), distance));
            } else if (tacticalMarkerDataHashMap.containsKey(marker)) {
                final TacticalMarkerData tacticalMarkerData = tacticalMarkerDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerInfoWindowAdapter(getContext(),
                        tacticalMarkerData.getLatitude(), tacticalMarkerData.getLongitude(),
                        tacticalMarkerData.getTitle(), tacticalMarkerData.getDescription(), distance));
                currentMarker = marker;
            } else if (missionMarkerDataHashMap.containsKey(marker)) {
                final MissionMarkerData missionMarkerData = missionMarkerDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerInfoWindowAdapter(getContext(),
                        missionMarkerData.getLatitude(), missionMarkerData.getLongitude(),
                        missionMarkerData.getTitle(), missionMarkerData.getDescription(), distance));
                currentMarker = marker;
            } else if (respawnMarkerDataHashMap.containsKey(marker)) {
                final RespawnMarkerData respawnMarkerData = respawnMarkerDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerOwnInfoWindowAdapter(getContext(),
                        respawnMarkerData.getLatitude(), respawnMarkerData.getLongitude(),
                        respawnMarkerData.getTitle(), respawnMarkerData.getDescription(), respawnMarkerData.isOwn(), distance));
                currentMarker = marker;
                if (FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isOrga()) {
                    swapFlagfb.setVisibility(View.VISIBLE);
                }
            } else if (hqMarkerDataHashMap.containsKey(marker)) {
                final HQMarkerData hqMarkerData = hqMarkerDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerOwnInfoWindowAdapter(getContext(),
                        hqMarkerData.getLatitude(), hqMarkerData.getLongitude(),
                        hqMarkerData.getTitle(), hqMarkerData.getDescription(), hqMarkerData.isOwn(), distance));
                currentMarker = marker;
            } else if (flagDataHashMap.containsKey(marker)) {
                final FlagMarkerData flagMarkerData = flagDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerOwnInfoWindowAdapter(getContext(),
                        flagMarkerData.getLatitude(), flagMarkerData.getLongitude(),
                        flagMarkerData.getTitle(), flagMarkerData.getDescription(), flagMarkerData.isOwn(), distance));
                if (FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isOrga()) {
                    swapFlagfb.setVisibility(View.VISIBLE);
                }
                currentMarker = marker;
            }
            if (FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isOrga()) {
                Log.d("DDDD", "adas");
                if (currentMarker != null)
                    removeMarkerfb.setVisibility(View.VISIBLE);
                else removeMarkerfb.setVisibility(View.INVISIBLE);
            }
            marker.showInfoWindow();

            return true;
        });

        googleMap.setOnMapClickListener(e -> {
            swapFlagfb.setVisibility(View.INVISIBLE);
            removeMarkerfb.setVisibility(View.INVISIBLE);
        });

        googleMap.setOnCameraMoveListener(() -> {
            scaleView.update(googleMap.getCameraPosition().zoom, googleMap.getCameraPosition().target.latitude);
        });

        googleMap.setOnCameraIdleListener(() -> {
            scaleView.update(googleMap.getCameraPosition().zoom, googleMap.getCameraPosition().target.latitude);
            if (clusterManager != null)
                clusterManager.onCameraIdle();
        });

        googleMap.setOnCameraMoveStartedListener(i -> {
            scaleView.update(googleMap.getCameraPosition().zoom, googleMap.getCameraPosition().target.latitude);
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
            setMarkerfb.setVisibility(View.VISIBLE);
            setMarkerfb.setOnClickListener(v -> {
                final OrgaAddMarkerDialogFragment orgaAddMarkerDialogFragment = OrgaAddMarkerDialogFragment.newInstance("New Marker");
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
                                final TacticalMarkerData tacticalMarkerData = tacticalMarkerDataHashMap.get(currentMarker);
                                for (final TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                    if (teamData.getTacticalMarkerData() != null && teamData.getTacticalMarkerData().getTitle().equals(tacticalMarkerData.getTitle())) {
                                        teamData.setTacticalMarkerData(null);
                                    }
                                }
                                FirebaseDB.getGameData().getTacticalMarkerData().remove(tacticalMarkerData);
                                tacticalMarkerDataHashMap.remove(currentMarker);
                            } else if (respawnMarkerDataHashMap.containsKey(currentMarker)) {
                                final RespawnMarkerData respawnMarkerData = respawnMarkerDataHashMap.get(currentMarker);
                                for (final TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                    if (teamData.getRespawnMarkerData() != null && teamData.getRespawnMarkerData().getTitle().equals(respawnMarkerData.getTitle()))
                                        teamData.setRespawnMarkerData(null);
                                }
                                FirebaseDB.getGameData().getRespawnMarkerData().remove(respawnMarkerData);
                                respawnMarkerDataHashMap.remove(currentMarker);
                            } else if (missionMarkerDataHashMap.containsKey(currentMarker)) {
                                final MissionMarkerData missionMarkerData = missionMarkerDataHashMap.get(currentMarker);
                                for (final TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                    if (teamData.getMissionMarkerData() != null && teamData.getMissionMarkerData().getTitle().equals(missionMarkerData.getTitle()))
                                        teamData.setMissionMarkerData(null);
                                }
                                FirebaseDB.getGameData().getMissionMarkerData().remove(missionMarkerData);
                                missionMarkerDataHashMap.remove(currentMarker);
                            } else if (hqMarkerDataHashMap.containsKey(currentMarker)) {
                                final HQMarkerData hqMarkerData = hqMarkerDataHashMap.get(currentMarker);
                                for (final TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                    if (teamData.getHqMarkerData() != null && teamData.getHqMarkerData().getTitle().equals(hqMarkerData.getTitle()))
                                        teamData.setHqMarkerData(null);
                                }
                                FirebaseDB.getGameData().getHqMarkerData().remove(hqMarkerData);
                                hqMarkerDataHashMap.remove(currentMarker);
                            } else if (flagDataHashMap.containsKey(currentMarker)) {
                                final FlagMarkerData flagMarkerData = flagDataHashMap.get(currentMarker);
                                for (final TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                    if (teamData.getFlagMarkerData() != null && teamData.getFlagMarkerData().getTitle().equals(flagMarkerData.getTitle()))
                                        teamData.setFlagMarkerData(null);
                                }
                                FirebaseDB.getGameData().getFlagMarkerData().remove(flagMarkerData);
                                flagDataHashMap.remove(currentMarker);
                            }
                            FirebaseDB.updateObject(documentReference, FirebaseDB.getGameData());
                            currentMarker = null;
                            removeMarkerfb.setVisibility(View.INVISIBLE);
                        });

                        swapFlagfb.setOnClickListener(e -> {
                            if (currentMarker != null && flagDataHashMap.containsKey(currentMarker)) {
                                final FlagMarkerData flagMarkerData =
                                        FirebaseDB.getGameData().getFlagMarkerData()
                                                .get(FirebaseDB.getGameData().getFlagMarkerData()
                                                        .indexOf(flagDataHashMap.get(currentMarker)));
                                flagMarkerData.setOwn(!flagMarkerData.isOwn());
                                FirebaseDB.updateObject(documentReference, "flagMarkerData",
                                        FirebaseDB.getGameData().getFlagMarkerData());
                                currentMarker = null;
                                swapFlagfb.setVisibility(View.INVISIBLE);
                            } else if (currentMarker != null && respawnMarkerDataHashMap.containsKey(currentMarker)) {
                                final RespawnMarkerData respawnMarkerData = FirebaseDB.getGameData().getRespawnMarkerData()
                                        .get(FirebaseDB.getGameData().getRespawnMarkerData()
                                                .indexOf(respawnMarkerDataHashMap.get(currentMarker)));
                                respawnMarkerData.setOwn(!respawnMarkerData.isOwn());
                                FirebaseDB.updateObject(documentReference, "respawnMarkerData",
                                        FirebaseDB.getGameData().getRespawnMarkerData());
                                currentMarker = null;
                                swapFlagfb.setVisibility(View.INVISIBLE);
                            }
                        });

                        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                            @Override
                            public void onMarkerDragStart(@NonNull final Marker marker) {
                                marker.hideInfoWindow();
                            }

                            @Override
                            public void onMarkerDrag(@NonNull final Marker marker) {
                                marker.hideInfoWindow();
                            }

                            @Override
                            public void onMarkerDragEnd(@NonNull final Marker marker) {
                                if (tacticalMarkerDataHashMap.containsKey(marker)) {
                                    final TacticalMarkerData tacticalMarkerData = tacticalMarkerDataHashMap.get(marker);
                                    tacticalMarkerData.setLatitude(marker.getPosition().latitude);
                                    tacticalMarkerData.setLongitude(marker.getPosition().longitude);
                                    for (final TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                        if (teamData.getTacticalMarkerData() != null && teamData.getTacticalMarkerData().getTitle().equals(tacticalMarkerData.getTitle())) {
                                            teamData.getTacticalMarkerData().setLatitude(marker.getPosition().latitude);
                                            teamData.getTacticalMarkerData().setLongitude(marker.getPosition().longitude);
                                        }
                                    }
                                } else if (missionMarkerDataHashMap.containsKey(marker)) {
                                    final MissionMarkerData missionMarkerData = missionMarkerDataHashMap.get(marker);
                                    missionMarkerData.setLatitude(marker.getPosition().latitude);
                                    missionMarkerData.setLongitude(marker.getPosition().longitude);
                                    for (final TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                        if (teamData.getMissionMarkerData() != null && teamData.getMissionMarkerData().getTitle().equals(missionMarkerData.getTitle())) {
                                            teamData.getMissionMarkerData().setLatitude(marker.getPosition().latitude);
                                            teamData.getMissionMarkerData().setLongitude(marker.getPosition().longitude);
                                        }
                                    }
                                } else if (respawnMarkerDataHashMap.containsKey(marker)) {
                                    final RespawnMarkerData respawnMarkerData = respawnMarkerDataHashMap.get(marker);
                                    respawnMarkerData.setLatitude(marker.getPosition().latitude);
                                    respawnMarkerData.setLongitude(marker.getPosition().longitude);
                                    for (final TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                        if (teamData.getRespawnMarkerData() != null && teamData.getRespawnMarkerData().getTitle().equals(respawnMarkerData.getTitle())) {
                                            teamData.getRespawnMarkerData().setLatitude(marker.getPosition().latitude);
                                            teamData.getRespawnMarkerData().setLongitude(marker.getPosition().longitude);
                                        }
                                    }
                                } else if (hqMarkerDataHashMap.containsKey(marker)) {
                                    final HQMarkerData hqMarkerData = hqMarkerDataHashMap.get(marker);
                                    hqMarkerData.setLatitude(marker.getPosition().latitude);
                                    hqMarkerData.setLongitude(marker.getPosition().longitude);
                                    for (final TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                        if (teamData.getHqMarkerData() != null && teamData.getHqMarkerData().getTitle().equals(hqMarkerData.getTitle())) {
                                            teamData.getHqMarkerData().setLatitude(marker.getPosition().latitude);
                                            teamData.getHqMarkerData().setLongitude(marker.getPosition().longitude);
                                        }
                                    }
                                } else if (flagDataHashMap.containsKey(marker)) {
                                    final FlagMarkerData flagMarkerData = flagDataHashMap.get(marker);
                                    flagMarkerData.setLatitude(marker.getPosition().latitude);
                                    flagMarkerData.setLongitude(marker.getPosition().longitude);
                                    for (final TeamData teamData : FirebaseDB.getGameData().getTeams()) {
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
            setMarkerfb.setVisibility(View.INVISIBLE);
            removeMarkerfb.setVisibility(View.INVISIBLE);
            swapFlagfb.setVisibility(View.INVISIBLE);
        }
    }

    public void addKmlLayer() {
        //TODO: Fix removing of KML Layer after Seconds
        if (googleMap != null) {
            if (kmlLayer == null) {
                FirebaseDB.getKml().whereEqualTo("title", FirebaseDB.getGameData().getKmlTitle()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            try {
                                kmlLayer = new KmlLayer(googleMap, new ByteArrayInputStream(task.getResult().toObjects(KMLData.class).get(0).getKml().getBytes(StandardCharsets.UTF_8)),
                                        getActivity().getApplicationContext());
                                setKmlLayer();
                            } catch (final XmlPullParserException e) {
                                Toast.makeText(getContext(), "Couldn´t parse KML Data!",
                                        Toast.LENGTH_LONG).show();
                            } catch (final IOException e) {
                                Toast.makeText(getContext(), "Couldn´t get inputstream from KML Data!",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Couldn´t query Database!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            } else
                setKmlLayer();
        }
    }

    private void setKmlLayer() {
        if (showKmlLayer) {
            try {
                if (!kmlLayer.isLayerOnMap()) {
                    kmlLayer.addLayerToMap();
                }

                groundOverlays.clear();
                kmlMarker.clear();

                for (final KmlGroundOverlay kmlGroundOverlay : kmlLayer.getGroundOverlays()) {
                    groundOverlays.add(googleMap.addGroundOverlay(new GroundOverlayOptions()
                            .positionFromBounds(kmlGroundOverlay.getLatLngBox())));
                }

                for (final KmlPlacemark kmlPlacemark : kmlLayer.getPlacemarks()) {
                    kmlMarker.add(googleMap.addMarker(kmlPlacemark.getMarkerOptions()));
                }

//                Toast.makeText(getContext(), "Added KMLLayer", Toast.LENGTH_LONG).show();
            } catch (final IOException e) {
                Toast.makeText(getContext(), "Couldn´t parse KML Data!",
                        Toast.LENGTH_LONG).show();
            } catch (final XmlPullParserException e) {
                Toast.makeText(getContext(), "Couldn´t get inputstream from KML Data!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showHeatMap() {
        if (showHeatMap) {
            final ArrayList<LatLng> latLngArrayList = new ArrayList<>();
            for (final Marker marker : userMarkerDataHashMap.keySet()) {
                latLngArrayList.add(marker.getPosition());
            }
            googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(
                    new HeatmapTileProvider.Builder().data(latLngArrayList).build()));
        }
    }

    private void setClusterManager() {
        if (googleMap != null) {
            clusterManager = new ClusterManager<ClusterMarkerItem>(getContext(), googleMap);
            setClusterItems();
        }
    }

    private void setClusterItems() {
        if (clusterManager != null) {
            for (final Marker marker : userMarkerDataHashMap.keySet()) {
                final ClusterMarkerItem clusterMarkerItem = new ClusterMarkerItem(marker.getPosition().latitude,
                        marker.getPosition().longitude);
                clusterManager.addItem(clusterMarkerItem);
            }
        }
    }

    public void showImageGroundOverlay() {
        if (googleMap == null || overlayImage == null) return;
        byte[] decode = Base64.decode(overlayImage.getImage(), Base64.DEFAULT);
        GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeByteArray(decode, 0, decode.length)))
                .anchor(0, 1)
                .position(new LatLng(overlayImage.getLatitude(), overlayImage.getLongitude()),
                        overlayImage.getWidth());
        googleMap.addGroundOverlay(groundOverlayOptions);
    }

    public enum ShowSettings {
        AllPlayer, ShowTeamOnly, ShowOnlyNotAssigned
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
}
