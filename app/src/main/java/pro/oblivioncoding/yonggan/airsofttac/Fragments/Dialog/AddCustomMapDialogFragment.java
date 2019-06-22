package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.KMLCollection.KMLData;
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
            final String title = ((EditText) rootView.findViewById(R.id.customMapTitletext)).getText().toString();
            FirebaseDB.getKml().whereEqualTo("title", title).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        Toast.makeText(getContext(), "Title already exists", Toast.LENGTH_LONG).show();
                    } else {
                        FirebaseDB.getKml().add(new KMLData(title,
                                ((EditText) rootView.findViewById(R.id.customMap)).getText().toString()));
                        getFragmentManager().beginTransaction().remove(this).commit();
                    }
                } else {
                    Toast.makeText(getContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG).show();
                }
            });

        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
