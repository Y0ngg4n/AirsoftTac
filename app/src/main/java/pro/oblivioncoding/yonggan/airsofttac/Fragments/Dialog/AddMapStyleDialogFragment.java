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
import pro.oblivioncoding.yonggan.airsofttac.Firebase.MapStyleCollection.MapStyleData;
import pro.oblivioncoding.yonggan.airsofttac.R;

import static android.app.Activity.RESULT_OK;

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

        rootView.findViewById(R.id.selectMapStyle).setOnClickListener(v -> {
            startActivityForResult(new Intent(Intent.ACTION_PICK),
                    3);
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
            case 3:
                if (resultCode == RESULT_OK) {
                    final Uri uri = data.getData();
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 7);
                    } else {
                        try {
                            ((TextView) rootView.findViewById(R.id.jsonMapStyle)).setText(
                                    FileUtils.readFileToString(new File(uri.getPath()), "UTF-8"));
                            Log.i("MapStyle", "Loaded MapStyle");
                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Could not load MapStyle", Toast.LENGTH_LONG).show();
                            Log.i("MapStyle", e.getMessage());
                        }
                    }
                }
                break;
        }
    }
}
