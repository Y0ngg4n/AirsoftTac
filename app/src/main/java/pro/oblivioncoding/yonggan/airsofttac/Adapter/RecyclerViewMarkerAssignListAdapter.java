package pro.oblivioncoding.yonggan.airsofttac.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerType;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.FlagMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.HQMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.MissionMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.RespawnMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.TacticalMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Teams.TeamData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.AssignMarkerTeamDialogFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class RecyclerViewMarkerAssignListAdapter extends RecyclerView.Adapter<RecyclerViewMarkerAssignListAdapter.ViewHolder> {
    private ArrayList<MarkerType> markerTypeArrayList;
    private TeamData teamData;
    private Context context;
    private FragmentManager fragmentManager;
    private AssignMarkerTeamDialogFragment assignMarkerTeamDialogFragment;

    public RecyclerViewMarkerAssignListAdapter(final ArrayList<MarkerType> markerTypeArrayList, final TeamData teamData, final FragmentManager fragmentManager, final AssignMarkerTeamDialogFragment assignMarkerTeamDialogFragment, final Context context) {
        this.markerTypeArrayList = markerTypeArrayList;
        this.teamData = teamData;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.assignMarkerTeamDialogFragment = assignMarkerTeamDialogFragment;
    }

    @NonNull
    @Override
    public RecyclerViewMarkerAssignListAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_assign_marker_team_list_item, viewGroup, false);
        final RecyclerViewMarkerAssignListAdapter.ViewHolder holder = new RecyclerViewMarkerAssignListAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewMarkerAssignListAdapter.ViewHolder viewHolder, final int i) {
        final MarkerType teamDataMarker = markerTypeArrayList.get(i);

        if (teamDataMarker instanceof FlagMarkerData) {
            viewHolder.markerTitle.setText(teamDataMarker.getTitle());
            viewHolder.markerDescription.setText(teamDataMarker.getDescription());
            viewHolder.markerType.setText("FlagMarkerData");
        } else if (teamDataMarker instanceof HQMarkerData) {
            viewHolder.markerTitle.setText(teamDataMarker.getTitle());
            viewHolder.markerDescription.setText(teamDataMarker.getDescription());
            viewHolder.markerType.setText("HQMarkerData");
        } else if (teamDataMarker instanceof MissionMarkerData) {
            viewHolder.markerTitle.setText(teamDataMarker.getTitle());
            viewHolder.markerDescription.setText(teamDataMarker.getDescription());
            viewHolder.markerType.setText("MissionMarkerData");
        } else if (teamDataMarker instanceof RespawnMarkerData) {
            viewHolder.markerTitle.setText(teamDataMarker.getTitle());
            viewHolder.markerDescription.setText(teamDataMarker.getDescription());
            viewHolder.markerType.setText("RespawnMarkerData");
        } else if (teamDataMarker instanceof TacticalMarkerData) {
            viewHolder.markerTitle.setText(teamDataMarker.getTitle());
            viewHolder.markerDescription.setText(teamDataMarker.getDescription());
            viewHolder.markerType.setText("TacticalMarkerData");
        }
        FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    viewHolder.assignMarker.setOnClickListener(v -> {

                        teamData.setFlagMarkerData(null);
                        teamData.setHqMarkerData(null);
                        teamData.setMissionMarkerData(null);
                        teamData.setRespawnMarkerData(null);
                        teamData.setTacticalMarkerData(null);
                        if (teamDataMarker instanceof FlagMarkerData) {
                            teamData.setFlagMarkerData((FlagMarkerData) teamDataMarker);
                        } else if (teamDataMarker instanceof HQMarkerData) {
                            teamData.setHqMarkerData((HQMarkerData) teamDataMarker);
                        } else if (teamDataMarker instanceof MissionMarkerData) {
                            teamData.setMissionMarkerData((MissionMarkerData) teamDataMarker);
                        } else if (teamDataMarker instanceof RespawnMarkerData) {
                            teamData.setRespawnMarkerData((RespawnMarkerData) teamDataMarker);
                        } else if (teamDataMarker instanceof TacticalMarkerData) {
                            teamData.setTacticalMarkerData((TacticalMarkerData) teamDataMarker);
                        }
                        FirebaseDB.updateObject(documentSnapshot.getReference(), FirebaseDB.getGameData());
                        fragmentManager.beginTransaction().remove(assignMarkerTeamDialogFragment).commit();
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return markerTypeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView markerTitle, markerDescription, markerType;
        Button assignMarker;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            markerTitle = itemView.findViewById(R.id.markerTitle);
            markerDescription = itemView.findViewById(R.id.markerDescription);
            markerType = itemView.findViewById(R.id.markerType);
            assignMarker = itemView.findViewById(R.id.joinLeaveTeam);
        }
    }
}
