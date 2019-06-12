package pro.oblivioncoding.yonggan.airsofttac.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Teams.TeamData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.AssignMarkerTeamDialogFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class RecyclerViewTeamListAdapter extends RecyclerView.Adapter<RecyclerViewTeamListAdapter.ViewHolder> {
    private ArrayList<TeamData> teamDataArrayList;
    private FragmentManager fragmentManager;
    private Context context;

    public RecyclerViewTeamListAdapter(FragmentManager fragmentManager, ArrayList<TeamData> teamDataArrayList, Context context) {
        this.fragmentManager = fragmentManager;
        this.teamDataArrayList = teamDataArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewTeamListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_team_list_item, viewGroup, false);
        RecyclerViewTeamListAdapter.ViewHolder holder = new RecyclerViewTeamListAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewTeamListAdapter.ViewHolder viewHolder, int i) {
        final TeamData teamData = teamDataArrayList.get(i);
        viewHolder.teamName.setText(teamData.getTeamName());
    }

    @Override
    public int getItemCount() {
        return teamDataArrayList.size();
    }

    private void setButtonJointLeave(Button button) {
        if (FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).getTeam() == null) {
            button.setText("Join");
        } else {
            button.setText("Leave");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView teamName;
        Button joinLeaveTeam, assignMarker;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.markerTitle);
            joinLeaveTeam = itemView.findViewById(R.id.teamMarkerAssign);
            assignMarker = itemView.findViewById(R.id.teamListAssign);
            constraintLayout = itemView.findViewById(R.id.playerListLayout);
            FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        setButtonJointLeave(joinLeaveTeam);
                        joinLeaveTeam.setOnClickListener(v -> {
                                    TeamData teamData = null;
                                    for (TeamData team : FirebaseDB.getGameData().getTeams()) {
                                        if (team.getTeamName().equals(teamName.getText().toString())) {
                                            teamData = team;
                                        }
                                    }
                                    if (teamData != null) {
                                        final UserData ownUserData = FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication
                                                .getFirebaseUser().getEmail());
                                        if (ownUserData.getTeam() == null) {
                                            if (!teamData.getUsers().contains(ownUserData.getEmail()))
                                                teamData.getUsers().add(ownUserData.getEmail());
                                            ownUserData.setTeam(teamData.getTeamName());
                                        } else {
                                            teamData.getUsers().remove(ownUserData.getEmail());
                                            ownUserData.setTeam(null);
                                        }
                                        FirebaseDB.updateObject(documentSnapshot, "teams",
                                                FirebaseDB.getGameData().getTeams());
                                        FirebaseDB.updateObject(documentSnapshot, "users",
                                                FirebaseDB.getGameData().getUsers());
                                        setButtonJointLeave(joinLeaveTeam);
                                    }
                                }
                        );

                        assignMarker.setOnClickListener(v -> {
                            TeamData teamData = null;
                            for (TeamData team : FirebaseDB.getGameData().getTeams()) {
                                if (team.getTeamName().equals(teamName.getText().toString())) {
                                    teamData = team;
                                }
                            }
                            AssignMarkerTeamDialogFragment assignMarkerTeamDialogFragment = AssignMarkerTeamDialogFragment.newInstance("Assign Team", teamData);
                            assignMarkerTeamDialogFragment.show(fragmentManager, "assign_team_marker");
                        });
                    }
                }
            });
        }
    }
}
