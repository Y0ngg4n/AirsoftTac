package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerType;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class EditMarkerDialogFragment extends DialogFragment {

    private View rootView;

    public EditMarkerDialogFragment() {
    }

    public static EditMarkerDialogFragment newInstance(final String title, MarkerType markerType) {
        return new EditMarkerDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.edit_marker_dialog_fragment, container);
        rootView.findViewById(R.id.applyEdit).setOnClickListener(v -> {
            ((EditText) rootView.findViewById(R.id.editDescription)).getText().toString();
        });
        return rootView;
    }
}
