package pro.oblivioncoding.yonggan.airsofttac.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import pro.oblivioncoding.yonggan.airsofttac.AdMob.AdMobIds;
import pro.oblivioncoding.yonggan.airsofttac.Adapter.RecyclerViewMapStyle;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.AddMapStyleDialogFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    @Nullable
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private RecyclerView recyclerView;
    private RecyclerView customMapRecyclerView;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    @NonNull
    public static SettingsFragment newInstance() {
        final SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        recyclerView = rootView.findViewById(R.id.mapstylelist);
        FirebaseDB.getMapStyles().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    final List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    setAdapter(documents);
                } else {
                    Toast.makeText(getContext(), R.string.code_settings_fragment_could_not_load_map_style_with_this_title,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), R.string.firebase_firestore_could_not_query_database,
                        Toast.LENGTH_LONG).show();
            }
        });

        ((EditText) rootView.findViewById(R.id.searchMapStyle)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void onTextChanged(@NonNull final CharSequence s, final int start, final int before, final int count) {
                if (!s.toString().isEmpty()) {
                    FirebaseDB.getMapStyles().whereEqualTo(getContext().getResources().getString(R.string.firebase_firestore_variable_map_styles_title), s.toString().toLowerCase()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                final List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                setAdapter(documents);
                            } else {
                                setAdapter(new ArrayList<>());
                            }
                        } else {
                            Toast.makeText(getContext(), R.string.firebase_firestore_could_not_query_database,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    FirebaseDB.getMapStyles().get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                final List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                setAdapter(documents);
                            }
                        } else {
                            Toast.makeText(getContext(), R.string.firebase_firestore_could_not_query_database,
                                    Toast.LENGTH_LONG).show();
                        }

                    });
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {
            }
        });


        rootView.findViewById(R.id.addMapStyle).setOnClickListener(v -> {
            final AddMapStyleDialogFragment addMapStyleDialogFragment = AddMapStyleDialogFragment.newInstance("Add Map Style");
            addMapStyleDialogFragment.show(getFragmentManager(), "add_map_style");
        });


        final InterstitialAd interstitialAd = new InterstitialAd(getContext());
        interstitialAd.setAdUnitId(AdMobIds.InterstialAll);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                interstitialAd.show();
            }

        });
        interstitialAd.loadAd(new AdRequest.Builder().build());


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(final Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setAdapter(@NonNull final List<DocumentSnapshot> mapStyles) {
        final RecyclerViewMapStyle recyclerViewMapStyle = new RecyclerViewMapStyle(new ArrayList<DocumentSnapshot>(mapStyles));
        recyclerView.setAdapter(recyclerViewMapStyle);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
