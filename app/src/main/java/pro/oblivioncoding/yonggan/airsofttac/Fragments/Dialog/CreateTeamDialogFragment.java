package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Teams.TeamData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class CreateTeamDialogFragment extends DialogFragment {

    public CreateTeamDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CreateTeamDialogFragment newInstance(String title) {
        return new CreateTeamDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_team_dialog, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Current Lcoation Button

        FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        view.findViewById(R.id.createTeamCreateButton).setOnClickListener(v -> {
                            TextView teamname = view.findViewById(R.id.searchMarkerName);
                            if (teamname.getText().toString().isEmpty())
                                return;
                            boolean teamExists = false;
                            for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                                if (teamname.getText().toString().equals(teamData.getTeamName()))
                                    teamExists = true;
                            }
                            if (teamExists) {
                                Toast.makeText(getContext(), "Team allready exists!", Toast.LENGTH_LONG).show();
                                return;
                            }

                            final TeamData teamData = new TeamData(teamname.getText().toString());
                            final UserData ownUserData = FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail());
                            teamData.getUsers().add(ownUserData.getEmail());
                            FirebaseDB.getGameData().getTeams().add(teamData);
                            ownUserData.setTeam(teamData.getTeamName());
                            FirebaseDB.updateObject(documentSnapshot, "teams",
                                    FirebaseDB.getGameData().getTeams());
                            FirebaseDB.updateObject(documentSnapshot, "users",
                                    FirebaseDB.getGameData().getUsers());
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
