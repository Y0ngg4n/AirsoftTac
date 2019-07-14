package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
            public void onTextChanged(@NonNull final CharSequence s, final int start, final int before, final int count) {
                ArrayList<MarkerType> markerTypeArrayList = getAllMarker();
                if (!s.toString().isEmpty()) {
                    markerTypeArrayList = new ArrayList<>();
                    for (final MarkerType markerType : getAllMarker()) {
                        String title = null;
                        String description = null;
                        if (markerType instanceof FlagMarkerData) {
                            final FlagMarkerData flagMarkerData = (FlagMarkerData) markerType;
                            title = flagMarkerData.getTitle();
                            description = flagMarkerData.getDescription();
                        } else if (markerType instanceof HQMarkerData) {
                            final HQMarkerData hqMarkerData = (HQMarkerData) markerType;
                            title = hqMarkerData.getTitle();
                            description = hqMarkerData.getDescription();
                        } else if (markerType instanceof MissionMarkerData) {
                            final MissionMarkerData missionMarkerData = (MissionMarkerData) markerType;
                            title = missionMarkerData.getTitle();
                            description = missionMarkerData.getDescription();
                        } else if (markerType instanceof RespawnMarkerData) {
                            final RespawnMarkerData respawnMarkerData = (RespawnMarkerData) markerType;
                            title = respawnMarkerData.getTitle();
                            description = respawnMarkerData.getDescription();
                        } else if (markerType instanceof TacticalMarkerData) {
                            final TacticalMarkerData tacticalMarkerData = (TacticalMarkerData) markerType;
                            title = tacticalMarkerData.getTitle();
                            description = tacticalMarkerData.getDescription();
                        }
                        if (title != null && description != null) {
                            if (title.toLowerCase().contains(s.toString().toLowerCase())
                                    || description.toLowerCase().contains(s.toString().toLowerCase())) {
                                markerTypeArrayList.add(markerType);
                            }
                        }
                    }
                }
                setRecyclerView(markerTypeArrayList, teamData, recyclerView);
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

    @NonNull
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
