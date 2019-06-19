package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
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

    public static OrgaAddMarkerDialogFragment newInstance(final String title) {
        return new OrgaAddMarkerDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.orga_add_marker_dialog, container);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Spinner spinner = getView().findViewById(R.id.orgaaddmarkerdialogtype);
        final ArrayList<String> spinnerItems = new ArrayList<String>(Arrays.asList("Tactical Marker", "Mission Marker", "Respawn Marker", "HQ Marker", "Flag Marker"));
        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerItems);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        getView().findViewById(R.id.orgaaddmarkerdialogown).setVisibility(View.INVISIBLE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                Log.i("Spinner", position + " selected");

                if (position == 2 || position == 3 || position == 4) {
                    getView().findViewById(R.id.orgaaddmarkerdialogown).setVisibility(View.VISIBLE);
                } else {
                    getView().findViewById(R.id.orgaaddmarkerdialogown).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

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

        FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        getView().findViewById(R.id.orgaaddmarkerdialogaddmarker).setOnClickListener(view1 ->
                        {
                            final Double latitude = Double.valueOf(((EditText) view.findViewById(R.id.orgaaddmarkerdialoglatitude)).getText().toString());
                            final Double longitude = Double.valueOf(((EditText) view.findViewById(R.id.orgaaddmarkerdialoglongitude)).getText().toString());
                            final String title = ((EditText) view.findViewById(R.id.orgaaddmarkerdialogtitle)).getText().toString();
                            final String description = ((EditText) view.findViewById(R.id.orgaaddmarkerdialogdescription)).getText().toString();
                            final boolean own = ((Switch) view.findViewById(R.id.orgaaddmarkerdialogown)).isChecked();
                            Log.i("Owned", String.valueOf(own));
                            switch (spinner.getSelectedItemPosition()) {
                                case 0:
                                    for (final TacticalMarkerData tacticalMarkerData : FirebaseDB.getGameData().getTacticalMarkerData()) {
                                        if(tacticalMarkerData.getTitle().equals(title)) {
                                            Toast.makeText(getContext(), "Title allready exists!", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                    FirebaseDB.getGameData().getTacticalMarkerData().add(new TacticalMarkerData(latitude, longitude, title, description));
                                    FirebaseDB.updateObject(documentSnapshot, "tacticalMarkerData", FirebaseDB.getGameData().getTacticalMarkerData());
                                    break;
                                case 1:
                                    for (final MissionMarkerData missionMarkerData : FirebaseDB.getGameData().getMissionMarkerData()) {
                                        if(missionMarkerData.getTitle().equals(title)) {
                                            Toast.makeText(getContext(), "Title allready exists!", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                    FirebaseDB.getGameData().getMissionMarkerData().add(new MissionMarkerData(
                                            latitude, longitude, title, description));
                                    FirebaseDB.updateObject(documentSnapshot, "missionMarkerData", FirebaseDB.getGameData().getMissionMarkerData());
                                    break;
                                case 2:
                                    for (final RespawnMarkerData respawnMarkerData : FirebaseDB.getGameData().getRespawnMarkerData()) {
                                        if(respawnMarkerData.getTitle().equals(title)) {
                                            Toast.makeText(getContext(), "Title allready exists!", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                    FirebaseDB.getGameData().getRespawnMarkerData().add(new RespawnMarkerData(
                                            latitude, longitude, title, description, own
                                    ));
                                    FirebaseDB.updateObject(documentSnapshot, "respawnMarkerData", FirebaseDB.getGameData().getRespawnMarkerData());
                                    break;
                                case 3:
                                    for (final HQMarkerData hqMarkerData : FirebaseDB.getGameData().getHqMarkerData()) {
                                        if(hqMarkerData.getTitle().equals(title)) {
                                            Toast.makeText(getContext(), "Title allready exists!", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                    FirebaseDB.getGameData().getHqMarkerData().add(new HQMarkerData(
                                            latitude, longitude, title, description, own
                                    ));
                                    FirebaseDB.updateObject(documentSnapshot, "hqMarkerData", FirebaseDB.getGameData().getHqMarkerData());
                                    break;
                                case 4:
                                    for (final FlagMarkerData flagMarkerData : FirebaseDB.getGameData().getFlagMarkerData()) {
                                        if(flagMarkerData.getTitle().equals(title)) {
                                            Toast.makeText(getContext(), "Title allready exists!", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                    FirebaseDB.getGameData().getFlagMarkerData().add(new FlagMarkerData(
                                            latitude, longitude, title, description, own
                                    ));
                                    FirebaseDB.updateObject(documentSnapshot, "flagMarkerData", FirebaseDB.getGameData().getFlagMarkerData());
                                    break;
                            }
                            getFragmentManager().beginTransaction().remove(this).commit();
                        });
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
    }
}
