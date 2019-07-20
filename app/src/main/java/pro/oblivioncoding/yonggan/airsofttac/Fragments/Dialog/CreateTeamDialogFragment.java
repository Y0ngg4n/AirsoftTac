package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

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

        FirebaseDB.getGames().whereEqualTo(getContext().getResources().getString(R.string.firebase_firestore_variable_games_gameID), FirebaseDB.getGameData().getGameID())
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
                                Toast.makeText(getContext(), R.string.code_create_team_dialog_fragment_team_already_exists, Toast.LENGTH_LONG).show();
                                return;
                            }

                            final TeamData teamData = new TeamData(teamname.getText().toString());
                            FirebaseDB.getGameData().getTeams().add(teamData);
                            FirebaseDB.updateObject(documentSnapshot, getContext().getResources().getString(R.string.firebase_firestore_variable_games_teams),
                                    FirebaseDB.getGameData().getTeams());
                            teamFragment.setRecyclerView(FirebaseDB.getGameData().getTeams(), recyclerView);
                            getFragmentManager().beginTransaction().remove(this).commit();
                        });
                    } else {
                        Log.d(getContext().getResources().getString(R.string.firebase_firestore_update_db_tag), getContext().getResources().getString(R.string.firebase_firestore_current_data_null));
                    }
                } else {
                    Toast.makeText(getContext(), R.string.firebase_firestore_could_not_find_document_with_gameid,
                            Toast.LENGTH_LONG);
                }
            } else {
                Toast.makeText(getContext(), R.string.firebase_firestore_could_not_query_database,
                        Toast.LENGTH_LONG);
            }
        });
    }
}
