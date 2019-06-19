package pro.oblivioncoding.yonggan.airsofttac.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Teams.TeamData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.AssignMarkerTeamDialogFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.AssignRadioChannelDialogFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.TeamFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class RecyclerViewTeamListAdapter extends RecyclerView.Adapter<RecyclerViewTeamListAdapter.ViewHolder> {
    private ArrayList<TeamData> teamDataArrayList;
    private FragmentManager fragmentManager;
    private TeamFragment teamFragment;
    private Context context;

    public RecyclerViewTeamListAdapter(FragmentManager fragmentManager, ArrayList<TeamData> teamDataArrayList, Context context, TeamFragment teamFragment) {
        this.fragmentManager = fragmentManager;
        this.teamDataArrayList = teamDataArrayList;
        this.context = context;
        this.teamFragment = teamFragment;
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
        viewHolder.minorRadioChannel.setText(String.valueOf(teamData.getMinorRadioChannel()));
        viewHolder.majorRadioChannel.setText(String.valueOf(teamData.getMajorRadioChannel()));
    }

    @Override
    public int getItemCount() {
        return teamDataArrayList.size();
    }

    private void setButtonJointLeave(Button button, UserData ownUserdata, String teamName) {
        if (ownUserdata.getTeam() == null || !teamName.equals(teamName)) {
            button.setText("Join");
        } else {
            button.setText("Leave");
        }
    }

    private void toggleRadioAssignButton(Button assignRadioChannel, TextView teamName) {
        {
            TeamData teamData = null;
            for (TeamData team : FirebaseDB.getGameData().getTeams()) {
                if (team.getTeamName().equals(teamName.getText().toString())) {
                    teamData = team;
                }
            }
            if (teamData != null && (teamData.getUsers().contains(FirebaseAuthentication.getFirebaseUser().getEmail())
                    || FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isOrga())) {
                assignRadioChannel.setVisibility(View.INVISIBLE);
                showAfterTime(assignRadioChannel, 10000L);
            } else {
                assignRadioChannel.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void toggleRadioAssignButton(Button assignRadioChannel, TextView teamName, TeamData teamData) {
        {
            if (teamData != null && (teamData.getUsers().contains(FirebaseAuthentication.getFirebaseUser().getEmail())
                    || FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()).isOrga())) {
                assignRadioChannel.setVisibility(View.INVISIBLE);
                showAfterTime(assignRadioChannel, 10000L);
            } else {

                assignRadioChannel.setVisibility(View.INVISIBLE);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView teamName, minorRadioChannel, majorRadioChannel;
        Button joinLeaveTeam, assignMarker, assignRadioChannel;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.markerTitle);
            joinLeaveTeam = itemView.findViewById(R.id.teamMarkerAssign);
            assignMarker = itemView.findViewById(R.id.teamListAssign);
            assignRadioChannel = itemView.findViewById(R.id.radioChannel);
            constraintLayout = itemView.findViewById(R.id.playerListLayout);
            minorRadioChannel = itemView.findViewById(R.id.minorRadioChannelText);
            majorRadioChannel = itemView.findViewById(R.id.majorRadioChannelText);
            FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        {
                            TeamData teamData = null;
                            for (TeamData team : FirebaseDB.getGameData().getTeams()) {
                                if (team.getTeamName().equals(teamName.getText().toString())) {
                                    teamData = team;
                                }
                            }
                            final UserData ownUserData = FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication
                                    .getFirebaseUser().getEmail());
                            setButtonJointLeave(joinLeaveTeam, ownUserData, teamData.getTeamName());
                        }
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
                                    teamData.getUsers().add(ownUserData.getEmail());
                                    ownUserData.setTeam(teamData.getTeamName());
                                    Log.i("JoinLeave", FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication
                                            .getFirebaseUser().getEmail()).getTeam());
                                } else {
                                    if (ownUserData.getTeam().equals(teamData.getTeamName())) {
                                        teamData.getUsers().remove(ownUserData.getEmail());
                                        ownUserData.setTeam(null);
                                    } else {
                                        Toast.makeText(context, "Please leave your current Team First!", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                }
                                setButtonJointLeave(joinLeaveTeam, ownUserData, teamData.getTeamName());
                                toggleRadioAssignButton(assignRadioChannel, teamName, teamData);
                                FirebaseDB.updateObject(documentSnapshot.getReference(), "users", FirebaseDB.getGameData().getUsers());
                                FirebaseDB.updateObject(documentSnapshot.getReference(), "teams", FirebaseDB.getGameData().getTeams());
                                joinLeaveTeam.setVisibility(View.INVISIBLE);
                                showAfterTime(joinLeaveTeam, 10000L);
                            }
                        });

                        toggleRadioAssignButton(assignRadioChannel, teamName);
                        assignRadioChannel.setOnClickListener(v -> {
                            TeamData teamData = null;
                            for (TeamData team : FirebaseDB.getGameData().getTeams()) {
                                if (team.getTeamName().equals(teamName.getText().toString())) {
                                    teamData = team;
                                }
                            }
                            AssignRadioChannelDialogFragment assignRadioChannelDialogFragment = AssignRadioChannelDialogFragment.newInstance("Assign Radio Channel", teamData, teamFragment);
                            assignRadioChannelDialogFragment.show(fragmentManager, "assign_team_marker");

                        });


                        assignMarker.setOnClickListener(v -> {
                            TeamData teamData = null;
                            for (TeamData team : FirebaseDB.getGameData().getTeams()) {
                                if (team.getTeamName().equals(teamName.getText().toString())) {
                                    teamData = team;
                                }
                            }
                            AssignMarkerTeamDialogFragment assignMarkerTeamDialogFragment = AssignMarkerTeamDialogFragment.newInstance("Assign Team", teamData);
                            assignMarkerTeamDialogFragment.show(fragmentManager, "assign_team_marker");
                            assignMarker.setVisibility(View.INVISIBLE);
                            showAfterTime(assignMarker, 10000L);
                        });
                    }
                }
            });
        }
    }

    private void showAfterTime(Button button, long delay){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                teamFragment.getActivity().runOnUiThread(() -> {
                    button.setVisibility(View.VISIBLE);
                });
            }
        }, delay);
    }
}
