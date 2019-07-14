package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.KMLCollection.KMLData;
import pro.oblivioncoding.yonggan.airsofttac.R;

import static android.app.Activity.RESULT_OK;

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

        rootView.findViewById(R.id.selectKMLFile).setOnClickListener(v -> {
            startActivityForResult(new Intent(Intent.ACTION_PICK),
                    2);
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    final Uri uri = data.getData();
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 6);
                    } else {
                        try {
                            ((TextView) rootView.findViewById(R.id.customMap)).setText(
                                    FileUtils.readFileToString(new File(uri.getPath()), "UTF-8"));
                            Log.i("KMLLoader", "Loaded KML");
                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Could not load KML", Toast.LENGTH_LONG).show();
                            Log.i("KMLLoader", e.getMessage());
                        }
                    }
                }
                break;
        }
    }
}
