package pro.oblivioncoding.yonggan.airsofttac.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import pro.oblivioncoding.yonggan.airsofttac.Adapter.RecyclerViewKML;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        recyclerView = rootView.findViewById(R.id.mapstylelist);
        customMapRecyclerView = rootView.findViewById(R.id.custommaplist);
        FirebaseDB.getMapStyles().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    setAdapter(documents);
                } else {
                    Toast.makeText(getContext(), "Couldn´t find Document with this Title!",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Couldn´t query Database!",
                        Toast.LENGTH_LONG).show();
            }
        });
        FirebaseDB.getKml().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    setKMLAdapter(documents);
                } else {
                    Toast.makeText(getContext(), "Couldn´t find Document with this Title!",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Couldn´t query Database!",
                        Toast.LENGTH_LONG).show();
            }
        });
        ((EditText) rootView.findViewById(R.id.searchMapStyle)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    FirebaseDB.getMapStyles().whereEqualTo("title", s.toString().toLowerCase()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                setAdapter(documents);
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
            public void afterTextChanged(Editable s) {
            }
        });
        rootView.findViewById(R.id.addMapStyle).setOnClickListener(v -> {
            final AddMapStyleDialogFragment addMapStyleDialogFragment = AddMapStyleDialogFragment.newInstance("Add Map Style");
            addMapStyleDialogFragment.show(getFragmentManager(), "add_map_style");
        });
        ((EditText) rootView.findViewById(R.id.searchCustomMap)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    FirebaseDB.getKml().whereEqualTo("title", s.toString().toLowerCase()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                setKMLAdapter(documents);
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
            public void afterTextChanged(Editable s) {
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    private void setAdapter(final List<DocumentSnapshot> mapStyles) {
        final RecyclerViewMapStyle recyclerViewMapStyle = new RecyclerViewMapStyle(new ArrayList<DocumentSnapshot>(mapStyles));
        recyclerView.setAdapter(recyclerViewMapStyle);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
    }

    private void setKMLAdapter(final List<DocumentSnapshot> customMaps) {
        final RecyclerViewKML recyclerViewKML = new RecyclerViewKML(new ArrayList<DocumentSnapshot>(customMaps));
        recyclerView.setAdapter(recyclerViewKML);
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
