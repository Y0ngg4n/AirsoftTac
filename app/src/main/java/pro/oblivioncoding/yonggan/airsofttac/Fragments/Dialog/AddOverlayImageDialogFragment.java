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
import pro.oblivioncoding.yonggan.airsofttac.Firebase.OverlayImageCollection.OverlayImage;
import pro.oblivioncoding.yonggan.airsofttac.R;

import static android.app.Activity.RESULT_OK;

public class AddOverlayImageDialogFragment extends DialogFragment {

    private static String image = null;
    private View rootView;

    public AddOverlayImageDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AddOverlayImageDialogFragment newInstance(final String title) {
        return new AddOverlayImageDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.add_overlay_image_dialog, container, false);
        rootView.findViewById(R.id.addOverlayImage).setOnClickListener(v -> {
            final String name = ((EditText) rootView.findViewById(R.id.name)).getText().toString();
            FirebaseDB.getOverlayImages().whereEqualTo("name", name).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        Toast.makeText(getContext(), "Title already exists", Toast.LENGTH_LONG).show();
                    } else {
                        if (image != null && !name.isEmpty()) {
                            FirebaseDB.getOverlayImages().add(new OverlayImage(name, image,
                                    Double.valueOf(((EditText) rootView.findViewById(R.id.latitude)).getText().toString()),
                                    Double.valueOf(((EditText) rootView.findViewById(R.id.longitude)).getText().toString()),
                                    Float.valueOf(((EditText) rootView.findViewById(R.id.imageWidth)).getText().toString())));
                            getFragmentManager().beginTransaction().remove(this).commit();
                        } else {
                            Toast.makeText(getContext(), "Please select Image first!", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG).show();
                }
            });

        });

        rootView.findViewById(R.id.selectImage).setOnClickListener(v -> {
            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                    1);
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
            case 1:
                if (resultCode == RESULT_OK) {
                    final Uri uri = data.getData();
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
                    } else {
                        try {
                            image = android.util.Base64.encodeToString(FileUtils.readFileToByteArray(
                                    new File(pro.oblivioncoding.yonggan.airsofttac.Utils.FileUtils.getRealPathFromURI(getContext(), uri))),
                                    android.util.Base64.DEFAULT);
                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Could not load Image", Toast.LENGTH_LONG).show();
                            Log.i("ImageLoader", e.getMessage());
                        }
                    }
                }
                break;
        }
    }


}
