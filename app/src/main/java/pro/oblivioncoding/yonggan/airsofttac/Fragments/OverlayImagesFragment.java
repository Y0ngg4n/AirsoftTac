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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import pro.oblivioncoding.yonggan.airsofttac.Adapter.RecyclerViewOverlayImages;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.AddOverlayImageDialogFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OverlayImagesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OverlayImagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverlayImagesFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;

    private OnFragmentInteractionListener mListener;

    public OverlayImagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OverlayImagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverlayImagesFragment newInstance() {
        OverlayImagesFragment fragment = new OverlayImagesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_overlay_images, container, false);
        recyclerView = rootView.findViewById(R.id.overlay_images_list);

        FirebaseDB.getOverlayImages().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    final List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    setAdapter(documents);
                } else {
                    Toast.makeText(getContext(), R.string.firebase_firestore_could_not_find_document_with_gameid,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), R.string.firebase_firestore_could_not_query_database,
                        Toast.LENGTH_LONG).show();
            }
        });

        rootView.findViewById(R.id.addOverlayImage).setOnClickListener(v -> {
            final AddOverlayImageDialogFragment addOverlayImageDialogFragment = AddOverlayImageDialogFragment.newInstance("Add Overlay Image");
            addOverlayImageDialogFragment.show(getFragmentManager(), "add_overlay_image");
        });

        ((EditText) rootView.findViewById(R.id.searchOverlayImage)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void onTextChanged(@NonNull final CharSequence s, final int start, final int before, final int count) {
                if (!s.toString().isEmpty()) {
                    FirebaseDB.getOverlayImages().whereEqualTo(getContext().getResources().getString(R.string.firebase_firestore_variable_overlay_images_name), s.toString().toLowerCase()).get().addOnCompleteListener(task -> {
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
                    FirebaseDB.getOverlayImages().get().addOnCompleteListener(task -> {
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

//        final InterstitialAd interstitialAd = new InterstitialAd(getContext());
//        interstitialAd.setAdUnitId(AdMobIds.InterstialAll);
//        interstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                interstitialAd.show();
//            }
//
//        });
//        interstitialAd.loadAd(new AdRequest.Builder().build());
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
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

    private void setAdapter(@NonNull final List<DocumentSnapshot> overlayImages) {
        final RecyclerViewOverlayImages recyclerViewOverlayImages = new RecyclerViewOverlayImages(new ArrayList<DocumentSnapshot>(overlayImages));
        recyclerView.setAdapter(recyclerViewOverlayImages);
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
