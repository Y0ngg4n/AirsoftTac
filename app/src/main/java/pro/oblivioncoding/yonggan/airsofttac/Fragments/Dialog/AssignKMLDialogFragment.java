package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import pro.oblivioncoding.yonggan.airsofttac.Activitys.CreateGameActivity;
import pro.oblivioncoding.yonggan.airsofttac.Adapter.RecyclerViewKML;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class AssignKMLDialogFragment extends DialogFragment {

    private static View rootView;
    private static CreateGameActivity createGameActivity;
    private RecyclerView customMapRecyclerView;

    public AssignKMLDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AssignKMLDialogFragment newInstance(final String title, final CreateGameActivity pCreateGameActivity) {
        createGameActivity = pCreateGameActivity;
        return new AssignKMLDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.assign_kml_dialog_fragment, container, false);
        customMapRecyclerView = rootView.findViewById(R.id.custommaplist);
        FirebaseDB.getKml().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    final List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    setKMLAdapter(documents, createGameActivity);
                } else {
                    Toast.makeText(getContext(), "Couldn´t find Document with this Title!",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Couldn´t query Database!",
                        Toast.LENGTH_LONG).show();
            }
        });

        ((EditText) rootView.findViewById(R.id.searchCustomMap)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void onTextChanged(@NonNull final CharSequence s, final int start, final int before, final int count) {
                if (!s.toString().isEmpty()) {
                    FirebaseDB.getKml().whereEqualTo("title", s.toString().toLowerCase()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                final List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                setKMLAdapter(documents, createGameActivity);
                            } else {
                                Toast.makeText(getContext(), "Couldn´t find Document this Title!",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Couldn´t query Database!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setKMLAdapter(@NonNull final List<DocumentSnapshot> customMaps, final CreateGameActivity createGameActivity) {
        Log.i("KML", "CustomMap");
        final RecyclerViewKML recyclerViewKML = new RecyclerViewKML(new ArrayList<DocumentSnapshot>(customMaps), createGameActivity);
        customMapRecyclerView.setAdapter(recyclerViewKML);
        customMapRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
    }
}
