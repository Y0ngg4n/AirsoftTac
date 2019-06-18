package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Teams.TeamData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.TeamFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class AssignRadioChannelDialogFragment extends DialogFragment {

    private static TeamData teamData;
    private static RecyclerView recyclerView;
    private static TeamFragment teamFragment;
    public AssignRadioChannelDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AssignRadioChannelDialogFragment newInstance(String title, TeamData pTeamData, TeamFragment pTeamFragment) {
        teamData = pTeamData;
        teamFragment = pTeamFragment;
        return new AssignRadioChannelDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.radio_channel_select_dialog, container);
        final NumberPicker minorRadioChannelPicker = rootView.findViewById(R.id.minorRadioChannel);
        minorRadioChannelPicker.setMinValue(1);
        minorRadioChannelPicker.setMaxValue(121);
        final NumberPicker majorRadioChannelPicker = rootView.findViewById(R.id.majorRadioChannel);
        majorRadioChannelPicker.setMinValue(1);
        majorRadioChannelPicker.setMaxValue(8);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        view.findViewById(R.id.assignRadioChannelButton).setOnClickListener(v -> {
                           teamData.setMinorRadioChannel(((NumberPicker) view.findViewById(R.id.minorRadioChannel)).getValue());
                           teamData.setMajorRadioChannel(((NumberPicker) view.findViewById(R.id.majorRadioChannel)).getValue());
                            FirebaseDB.updateObject(documentSnapshot, "teams",
                                    FirebaseDB.getGameData().getTeams());
                            teamFragment.setRecyclerView(FirebaseDB.getGameData().getTeams(), recyclerView);
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
