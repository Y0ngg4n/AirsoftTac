package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.MapStyleCollection.MapStyleData;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class AddMapStyleDialogFragment extends DialogFragment {

    private View rootView;

    public AddMapStyleDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AddMapStyleDialogFragment newInstance(final String title) {
        return new AddMapStyleDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.add_map_style_dialog, container, false);
        rootView.findViewById(R.id.addMapStyleButton).setOnClickListener(v -> {
            FirebaseDB.getMapStyles().add(new MapStyleData(
                    ((EditText) rootView.findViewById(R.id.mapstyletitletext)).getText().toString(),
                    ((EditText) rootView.findViewById(R.id.jsonMapStyle)).getText().toString()
            ));
            getFragmentManager().beginTransaction().remove(this).commit();
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
