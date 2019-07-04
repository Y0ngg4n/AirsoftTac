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

import pro.oblivioncoding.yonggan.airsofttac.Adapter.RecyclerViewGotoMarkerListAdapter;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerType;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.FlagMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.HQMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.MissionMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.RespawnMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.TacticalMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class GotoSearchMarkerSelection extends DialogFragment {

    private View rootView;

    public GotoSearchMarkerSelection() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static GotoSearchMarkerSelection newInstance(final String title) {
        return new GotoSearchMarkerSelection();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.goto_marker_selection_dialog, container);
        final RecyclerView recyclerView = rootView.findViewById(R.id.markerList);
        final TextView searchMarkerList = rootView.findViewById(R.id.markerListSearch);

        searchMarkerList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<MarkerType> markerTypeArrayList = getAllMarker();
                if (!s.toString().isEmpty()) {
                    markerTypeArrayList = new ArrayList<>();
                    for (MarkerType markerType : getAllMarker()) {
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
                            } else if (title.isEmpty()) {
                                markerTypeArrayList.addAll(getAllMarker());
                            }
                        }
                    }
                }
                setRecyclerView(markerTypeArrayList, recyclerView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setRecyclerView(getAllMarker(), recyclerView);

        return rootView;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setRecyclerView(final ArrayList<MarkerType> markerTypes, final RecyclerView recyclerView) {
        final RecyclerViewGotoMarkerListAdapter recyclerViewGotoMarkerListAdapter = new RecyclerViewGotoMarkerListAdapter(markerTypes, getFragmentManager(), rootView.getContext(), this);
        recyclerView.setAdapter(recyclerViewGotoMarkerListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
    }
}
