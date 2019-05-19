package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.GameData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.FlagMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.HQMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.MissionMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.RespawnMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.TacticalMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class OrgaAddMarkerDialogFragment extends DialogFragment {

    public OrgaAddMarkerDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static OrgaAddMarkerDialogFragment newInstance(String title) {
        return new OrgaAddMarkerDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.orga_add_marker_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Spinner spinner = (Spinner) getView().findViewById(R.id.orgaaddmarkerdialogtype);
        ArrayList<String> spinnerItems = new ArrayList<String>(Arrays.asList("Tactical Marker", "Mission Marker", "Respawn Marker", "HQ Marker", "Flag Marker"));
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerItems);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        getView().findViewById(R.id.orgaaddmarkerdialogown).setVisibility(View.INVISIBLE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Spinner", position + " selected");
                if (position == 0) {
                    getView().findViewById(R.id.orgaaddmarkerteamname).setVisibility(View.VISIBLE);
                } else if (position == 2 || position == 3 || position == 4) {
                    getView().findViewById(R.id.orgaaddmarkerteamname).setVisibility(View.INVISIBLE);
                    getView().findViewById(R.id.orgaaddmarkerdialogown).setVisibility(View.VISIBLE);
                } else {
                    getView().findViewById(R.id.orgaaddmarkerteamname).setVisibility(View.INVISIBLE);
                    getView().findViewById(R.id.orgaaddmarkerdialogown).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final TextView latitudeTextView = getView().findViewById(R.id.orgaaddmarkerdialoglatitude);
        final TextView longitudeTextView = getView().findViewById(R.id.orgaaddmarkerdialoglongitude);
        //Current Lcoation Button
        getView().findViewById(R.id.orgaaddmarkerdialogcurrentlocation).setOnClickListener(view1 ->
        {
            latitudeTextView.setText(String.valueOf(FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).getPositionLat()));
            longitudeTextView.setText(String.valueOf(FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).getPositionLong()));
        });

        (getView().findViewById(R.id.orgaaddmarkerdialogaddmarker)).setOnClickListener(view1 ->
        {
            switch (spinner.getSelectedItemPosition()) {
                case 0:
                    FirebaseDB.getGameData().getTacticalMarkerData().add(new TacticalMarkerData(
                            Double.valueOf(((EditText)getView().findViewById(R.id.orgaaddmarkerdialoglatitude)).getText().toString()),
                            Double.valueOf(((EditText)getView().findViewById(R.id.orgaaddmarkerdialoglongitude)).getText().toString()),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerdialogtitle)).getText().toString(),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerdialogdescription)).getText().toString(),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerteamname)).getText().toString()
                    ));
                    break;
                case 1:
                    FirebaseDB.getGameData().getMissionMarkerData().add(new MissionMarkerData(
                            Double.valueOf(((EditText)getView().findViewById(R.id.orgaaddmarkerdialoglatitude)).getText().toString()),
                            Double.valueOf(((EditText)getView().findViewById(R.id.orgaaddmarkerdialoglongitude)).getText().toString()),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerdialogtitle)).getText().toString(),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerdialogdescription)).getText().toString(),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerteamname)).getText().toString()
                    ));
                    break;
                case 2:
                    FirebaseDB.getGameData().getRespawnMarkerData().add(new RespawnMarkerData(
                            Double.valueOf(((EditText)getView().findViewById(R.id.orgaaddmarkerdialoglatitude)).getText().toString()),
                            Double.valueOf(((EditText)getView().findViewById(R.id.orgaaddmarkerdialoglongitude)).getText().toString()),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerdialogtitle)).getText().toString(),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerdialogdescription)).getText().toString(),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerteamname)).getText().toString()
                    ));
                    break;
                case 3:
                    FirebaseDB.getGameData().getHqMarkerData().add(new HQMarkerData(
                            Double.valueOf(((EditText)getView().findViewById(R.id.orgaaddmarkerdialoglatitude)).getText().toString()),
                            Double.valueOf(((EditText)getView().findViewById(R.id.orgaaddmarkerdialoglongitude)).getText().toString()),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerdialogtitle)).getText().toString(),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerdialogdescription)).getText().toString(),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerteamname)).getText().toString()
                    ));
                    break;
                case 4:
                    FirebaseDB.getGameData().getFlagMarkerData().add(new FlagMarkerData(
                            Double.valueOf(((EditText)getView().findViewById(R.id.orgaaddmarkerdialoglatitude)).getText().toString()),
                            Double.valueOf(((EditText)getView().findViewById(R.id.orgaaddmarkerdialoglongitude)).getText().toString()),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerdialogtitle)).getText().toString(),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerdialogdescription)).getText().toString(),
                            ((EditText)getView().findViewById(R.id.orgaaddmarkerteamname)).getText().toString()
                    ));
                    break;
            }
            FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            FirebaseDB.updateObject(documentSnapshot, "flagMarkerData", FirebaseDB.getGameData().getTacticalMarkerData());
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

            getFragmentManager().beginTransaction().remove(this).commit();
        });
    }
}
