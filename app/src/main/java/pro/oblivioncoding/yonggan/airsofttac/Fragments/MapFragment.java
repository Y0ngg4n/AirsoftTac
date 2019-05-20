package pro.oblivioncoding.yonggan.airsofttac.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.FlagMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.HQMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.MissionMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.RespawnMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.TacticalMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.InfoWindowAdapter.CustomMarkerInfoWindowAdapter;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    @Nullable
    private PlayerFragment.OnFragmentInteractionListener mListener;

    private GoogleMap googleMap;

    private FloatingActionButton currentlocationfb;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        //This is for loading the Map
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this::onMapReady);
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        currentlocationfb = getActivity().findViewById(R.id.currentlocationfb);
        final UserData ownUserData = FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication
                .getFirebaseUser().getEmail());
        currentlocationfb.setOnClickListener(v -> {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                    ownUserData.getPositionLat(),
                    ownUserData.getPositionLong())));
        });
        return rootView;
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
                FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                        .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                if (tacticalMarkerDataHashMap.containsKey(marker)) {
                                    final TacticalMarkerData tacticalMarkerData = tacticalMarkerDataHashMap.get(marker);
                                    tacticalMarkerData.setLatitude(marker.getPosition().latitude);
                                    tacticalMarkerData.setLongitude(marker.getPosition().longitude);
                                    FirebaseDB.updateObject(documentSnapshot, "tacticalMarkerData", FirebaseDB.getGameData().getTacticalMarkerData());
                                }
                                else if (missionMarkerDataHashMap.containsKey(marker)) {
                                    final MissionMarkerData missionMarkerData = missionMarkerDataHashMap.get(marker);
                                    missionMarkerData.setLatitude(marker.getPosition().latitude);
                                    missionMarkerData.setLongitude(marker.getPosition().longitude);
                                    FirebaseDB.updateObject(documentSnapshot, "missionMarkerData", FirebaseDB.getGameData().getMissionMarkerData());
                                }
                                else if (respawnMarkerDataHashMap.containsKey(marker)) {
                                    final RespawnMarkerData respawnMarkerData = respawnMarkerDataHashMap.get(marker);
                                    respawnMarkerData.setLatitude(marker.getPosition().latitude);
                                    respawnMarkerData.setLongitude(marker.getPosition().longitude);
                                    FirebaseDB.updateObject(documentSnapshot, "respawnMarkerData", FirebaseDB.getGameData().getRespawnMarkerData());
                                }
                                else if (hqMarkerDataHashMap.containsKey(marker)) {
                                    final HQMarkerData hqMarkerData = hqMarkerDataHashMap.get(marker);
                                    hqMarkerData.setLatitude(marker.getPosition().latitude);
                                    hqMarkerData.setLongitude(marker.getPosition().longitude);
                                    FirebaseDB.updateObject(documentSnapshot, "hqMarkerData", FirebaseDB.getGameData().getHqMarkerData());
                                }
                                else if (flagDataHashMap.containsKey(marker)) {
                                    final FlagMarkerData flagMarkerData = flagDataHashMap.get(marker);
                                    flagMarkerData.setLatitude(marker.getPosition().latitude);
                                    flagMarkerData.setLongitude(marker.getPosition().longitude);
                                    FirebaseDB.updateObject(documentSnapshot, "flagMarkerData", FirebaseDB.getGameData().getFlagMarkerData());
                                }
                            } else {
                                Log.d("UpdateDB", "Current data: null");
                            }
                        } else {
                            Toast.makeText(getContext(), "Couldn´t find Document with GameID!",
                                    Toast.LENGTH_LONG);
                        }
                    } else {
                        Toast.makeText(getContext(), "Couldn´t query Database!",
                                Toast.LENGTH_LONG);
                    }
                });
                marker.showInfoWindow();
            }
        });
        this.googleMap = googleMap;

        googleMap.setOnMarkerClickListener(marker ->
        {
            if(tacticalMarkerDataHashMap.containsKey(marker)){
                final TacticalMarkerData tacticalMarkerData = tacticalMarkerDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerInfoWindowAdapter(getContext(),
                        tacticalMarkerData.getLatitude(), tacticalMarkerData.getLongitude(),
                        tacticalMarkerData.getTitle(), tacticalMarkerData.getDescription()));
            }else if(missionMarkerDataHashMap.containsKey(marker)){
                final MissionMarkerData missionMarkerData = missionMarkerDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerInfoWindowAdapter(getContext(),
                        missionMarkerData.getLatitude(), missionMarkerData.getLongitude(),
                        missionMarkerData.getTitle(), missionMarkerData.getDescription()));
            }else if(respawnMarkerDataHashMap.containsKey(marker)){
                final RespawnMarkerData respawnMarkerData = respawnMarkerDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerInfoWindowAdapter(getContext(),
                        respawnMarkerData.getLatitude(), respawnMarkerData.getLongitude(),
                        respawnMarkerData.getTitle(), respawnMarkerData.getDescription()));
            }else if(hqMarkerDataHashMap.containsKey(marker)){
                final HQMarkerData hqMarkerData = hqMarkerDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerInfoWindowAdapter(getContext(),
                        hqMarkerData.getLatitude(), hqMarkerData.getLongitude(),
                        hqMarkerData.getTitle(), hqMarkerData.getDescription()));
            }else if(flagDataHashMap.containsKey(marker)){
                final FlagMarkerData flagMarkerData = flagDataHashMap.get(marker);
                googleMap.setInfoWindowAdapter(new CustomMarkerInfoWindowAdapter(getContext(),
                        flagMarkerData.getLatitude(), flagMarkerData.getLongitude(),
                        flagMarkerData.getTitle(), flagMarkerData.getDescription()));
            }
            marker.showInfoWindow();
            return true;
        });
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

    public void setAllPositionMarker() {
        for (UserData userData : FirebaseDB.getGameData().getUsers()) {
            final MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(userData.getPositionLat(), userData.getPositionLong()));
            setMarkerIcon(markerOptions, userData);
            googleMap.addMarker(markerOptions);
        }
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
                markerOptions.icon(getBitmapDescriptor(R.drawable.ic_respawnicon));
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
                markerOptions.icon(getBitmapDescriptor(R.drawable.ic_hqicon));
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
                markerOptions.icon(getBitmapDescriptor(R.drawable.ic_flagicon));
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
}

