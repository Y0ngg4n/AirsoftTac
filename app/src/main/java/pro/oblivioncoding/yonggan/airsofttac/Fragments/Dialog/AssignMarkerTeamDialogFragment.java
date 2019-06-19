package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pro.oblivioncoding.yonggan.airsofttac.Adapter.RecyclerViewMarkerAssignListAdapter;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerType;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.FlagMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.HQMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.MissionMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.RespawnMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.TacticalMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Teams.TeamData;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class AssignMarkerTeamDialogFragment extends DialogFragment {

    private static TeamData teamData;
    private View rootView;

    public AssignMarkerTeamDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AssignMarkerTeamDialogFragment newInstance(final String title, final TeamData pteamData) {
        teamData = pteamData;
        return new AssignMarkerTeamDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.assign_marker_team_dialog, container, false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.markerassignlist);
        final TextView searchTeamList = rootView.findViewById(R.id.searchMarkerName);

        searchTeamList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                if (!s.toString().isEmpty()) {
                    final ArrayList<MarkerType> markerTypeArrayList = new ArrayList<>();
                    for (final MarkerType markerType : getAllMarker()) {
                        String title = null;
                        String description = null;
                        if (teamData.getFlagMarkerData() != null) {
                            final FlagMarkerData flagMarkerData = teamData.getFlagMarkerData();
                            title = flagMarkerData.getTitle();
                            description = flagMarkerData.getDescription();
                        } else if (teamData.getHqMarkerData() != null) {
                            final HQMarkerData hqMarkerData = teamData.getHqMarkerData();
                            title = hqMarkerData.getTitle();
                            description = hqMarkerData.getDescription();
                        } else if (teamData.getMissionMarkerData() != null) {
                            final MissionMarkerData missionMarkerData = teamData.getMissionMarkerData();
                            title = missionMarkerData.getTitle();
                            description = missionMarkerData.getDescription();
                        } else if (teamData.getRespawnMarkerData() != null) {
                            final RespawnMarkerData respawnMarkerData = teamData.getRespawnMarkerData();
                            title = respawnMarkerData.getTitle();
                            description = respawnMarkerData.getDescription();
                        } else if (teamData.getTacticalMarkerData() != null) {
                            final TacticalMarkerData tacticalMarkerData = teamData.getTacticalMarkerData();
                            title = tacticalMarkerData.getTitle();
                            description = tacticalMarkerData.getDescription();
                        }
                        if (title != null && description != null)
                            if (title.toLowerCase().contains(s.toString().toLowerCase())
                                    || description.toLowerCase().contains(s.toString().toLowerCase())) {
                                markerTypeArrayList.add(markerType);
                            }
                    }
                    setRecyclerView(markerTypeArrayList, teamData, recyclerView);
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {
            }
        });
        setRecyclerView(getAllMarker(), teamData, recyclerView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private ArrayList<MarkerType> getAllMarker() {
        final ArrayList<MarkerType> markerTypeArrayList = new ArrayList<>();
        for (final HQMarkerData hqMarkerData : FirebaseDB.getGameData().getHqMarkerData()) {
            markerTypeArrayList.add(hqMarkerData);
        }
        for (final FlagMarkerData flagMarkerData : FirebaseDB.getGameData().getFlagMarkerData()) {
            markerTypeArrayList.add(flagMarkerData);
        }
        for (final MissionMarkerData missionMarkerData : FirebaseDB.getGameData().getMissionMarkerData()) {
            markerTypeArrayList.add(missionMarkerData);
        }
        for (final RespawnMarkerData respawnMarkerData : FirebaseDB.getGameData().getRespawnMarkerData()) {
            markerTypeArrayList.add(respawnMarkerData);
        }
        for (final TacticalMarkerData tacticalMarkerData : FirebaseDB.getGameData().getTacticalMarkerData()) {
            markerTypeArrayList.add(tacticalMarkerData);
        }
        return markerTypeArrayList;
    }

    private void setRecyclerView(final ArrayList<MarkerType> markerTypes, final TeamData teamData, final RecyclerView recyclerView) {
        final RecyclerViewMarkerAssignListAdapter recyclerViewMarkerAssignListAdapter = new RecyclerViewMarkerAssignListAdapter(markerTypes, teamData, getFragmentManager(), this, rootView.getContext());
        recyclerView.setAdapter(recyclerViewMarkerAssignListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
    }
}
