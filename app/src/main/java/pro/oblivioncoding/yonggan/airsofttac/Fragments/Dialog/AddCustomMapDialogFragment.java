package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.MapStyleCollection.MapStyleData;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class AddCustomMapDialogFragment extends DialogFragment {

    private View rootView;

    public AddCustomMapDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AddCustomMapDialogFragment newInstance(final String title) {
        return new AddCustomMapDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.add_custom_map_dialog, container, false);
        rootView.findViewById(R.id.addCustomMapButton).setOnClickListener(v -> {
            FirebaseDB.getMapStyles().add(new MapStyleData(
                    ((EditText) rootView.findViewById(R.id.customMapTitletext)).getText().toString(),
                    ((EditText) rootView.findViewById(R.id.customMap)).getText().toString()
            ));
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
