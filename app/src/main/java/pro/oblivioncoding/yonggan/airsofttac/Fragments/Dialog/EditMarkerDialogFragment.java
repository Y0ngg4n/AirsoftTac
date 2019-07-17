package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerType;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.FlagMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.HQMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.MissionMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.RespawnMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.TacticalMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Teams.TeamData;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class EditMarkerDialogFragment extends DialogFragment {

    private View rootView;
    private static MarkerType markerType;
    private static DocumentReference documentReference;
    public EditMarkerDialogFragment() {
    }

    public static EditMarkerDialogFragment newInstance(final String title, MarkerType pMarkerType, DocumentReference pDocumentReference) {
        markerType = pMarkerType;
        documentReference = pDocumentReference;
        return new EditMarkerDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.edit_marker_dialog_fragment, container);
        rootView.findViewById(R.id.applyEdit).setOnClickListener(v -> {
            final String description = ((EditText) rootView.findViewById(R.id.editDescription)).getText().toString();
            if (markerType instanceof FlagMarkerData) {
                ((FlagMarkerData) markerType).setDescription(description);
                FirebaseDB.getGameData().getFlagMarkerData().set(FirebaseDB.getGameData()
                        .getFlagMarkerData().indexOf(markerType), (FlagMarkerData) markerType);
                for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                    if (teamData.getFlagMarkerData() != null && teamData.getFlagMarkerData().equals(markerType)) {
                        teamData.setFlagMarkerData((FlagMarkerData) markerType);
                    }
                }
            } else if (markerType instanceof HQMarkerData) {
                ((HQMarkerData) markerType).setDescription(description);
                FirebaseDB.getGameData().getHqMarkerData().set(FirebaseDB.getGameData()
                        .getHqMarkerData().indexOf(markerType), (HQMarkerData) markerType);
                for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                    if (teamData.getHqMarkerData() != null && teamData.getHqMarkerData().equals(markerType)) {
                        teamData.setHqMarkerData((HQMarkerData) markerType);
                    }
                }
            } else if (markerType instanceof MissionMarkerData) {
                ((MissionMarkerData) markerType).setDescription(description);
                FirebaseDB.getGameData().getMissionMarkerData().set(FirebaseDB.getGameData()
                        .getMissionMarkerData().indexOf(markerType), (MissionMarkerData) markerType);
                for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                    if (teamData.getMissionMarkerData() != null && teamData.getMissionMarkerData().equals(markerType)) {
                        teamData.setMissionMarkerData((MissionMarkerData) markerType);
                    }
                }
            } else if (markerType instanceof RespawnMarkerData) {
                ((RespawnMarkerData) markerType).setDescription(description);
                FirebaseDB.getGameData().getRespawnMarkerData().set(FirebaseDB.getGameData()
                        .getRespawnMarkerData().indexOf(markerType), (RespawnMarkerData) markerType);
                for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                    if (teamData.getRespawnMarkerData() != null && teamData.getRespawnMarkerData().equals(markerType)) {
                        teamData.setRespawnMarkerData((RespawnMarkerData) markerType);
                    }
                }
            } else if (markerType instanceof TacticalMarkerData) {
                ((TacticalMarkerData) markerType).setDescription(description);
                FirebaseDB.getGameData().getTacticalMarkerData().set(FirebaseDB.getGameData()
                        .getTacticalMarkerData().indexOf(markerType), (TacticalMarkerData) markerType);
                for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                    if (teamData.getTacticalMarkerData() != null && teamData.getTacticalMarkerData().equals(markerType)) {
                        teamData.setTacticalMarkerData((TacticalMarkerData) markerType);
                    }
                }
            }

            FirebaseDB.updateObject(documentReference, FirebaseDB.getGameData());
            getFragmentManager().beginTransaction().remove(this).commit();
        });
        return rootView;
    }
}
