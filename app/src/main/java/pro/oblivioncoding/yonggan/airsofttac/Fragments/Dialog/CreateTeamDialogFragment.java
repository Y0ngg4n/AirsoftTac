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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Teams.TeamData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.TeamFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class CreateTeamDialogFragment extends DialogFragment {

    private static TeamFragment teamFragment;
    private static RecyclerView recyclerView;
    public CreateTeamDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CreateTeamDialogFragment newInstance(final String title, final TeamFragment pTeamFragment, final RecyclerView pRecyclerView) {
        teamFragment = pTeamFragment;
        recyclerView = pRecyclerView;
        return new CreateTeamDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_team_dialog, container);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Current Lcoation Button

        FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        view.findViewById(R.id.createTeamCreateButton).setOnClickListener(v -> {
                            final TextView teamname = view.findViewById(R.id.searchMarkerName);
                            if (teamname.getText().toString().isEmpty())
                                return;
                            boolean teamExists = false;
                            for (final TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                if (teamname.getText().toString().equals(teamData.getTeamName()))
                                    teamExists = true;
                            }
                            if (teamExists) {
                                Toast.makeText(getContext(), "Team allready exists!", Toast.LENGTH_LONG).show();
                                return;
                            }

                            final TeamData teamData = new TeamData(teamname.getText().toString());
                            FirebaseDB.getGameData().getTeams().add(teamData);
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
