package pro.oblivioncoding.yonggan.airsofttac.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.TacticalMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;

    private GoogleMap googleMap;

    private FloatingActionButton currentlocationfb;

    private HashMap<Marker, TacticalMarkerData> tacticalMarkerDataHashMap = new HashMap<>();

    public MapFragment() {
        // Required empty public constructor
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public void onMapReady(GoogleMap googleMap) {
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

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if (tacticalMarkerDataHashMap.containsKey(marker)) {
                    final TacticalMarkerData tacticalMarkerData = tacticalMarkerDataHashMap.get(marker);
                    tacticalMarkerData.setLatitude(marker.getPosition().latitude);
                    tacticalMarkerData.setLongitude(marker.getPosition().longitude);
                }
            }
        });

        this.googleMap = googleMap;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setMarker() {
        if (googleMap != null) {
            googleMap.clear();
            setAllPositionMarker();
            setAlTacticalMarker();
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
            Log.i("Tactic", FirebaseDB.getGameData().getTacticalMarkerData().size() + "");
            for (TacticalMarkerData tacticalMarkerData : FirebaseDB.getGameData().getTacticalMarkerData()) {
                final MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(tacticalMarkerData.getLatitude(), tacticalMarkerData.getLongitude()));
                markerOptions.draggable(true);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                tacticalMarkerDataHashMap.put(googleMap.addMarker(markerOptions), tacticalMarkerData);
            }
        }
        else Log.i("Tactic", "Null");
    }

    private void setMarkerIcon(MarkerOptions markerOptions, UserData userData) {
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
