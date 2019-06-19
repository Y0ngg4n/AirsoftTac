package pro.oblivioncoding.yonggan.airsofttac.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pro.oblivioncoding.yonggan.airsofttac.Adapter.RecyclerViewPlayerListAdapter;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {

    @Nullable
    private OnFragmentInteractionListener mListener;
    private View rootView;

    public PlayerFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static PlayerFragment newInstance() {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_player, container, false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.playerList);
        final TextView searchPlayerList = rootView.findViewById(R.id.searchPlayerList);
        searchPlayerList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<UserData> userDataArrayList = FirebaseDB.getGameData().getUsers();
                if (!s.toString().isEmpty()) {
                    userDataArrayList = new ArrayList<>();
                    for (UserData userData : FirebaseDB.getGameData().getUsers()) {
                        if (userData.getEmail().toLowerCase().contains(s.toString().toLowerCase())
                                || userData.getNickname().toLowerCase().contains(s.toString().toLowerCase())) {
                            userDataArrayList.add(userData);
                        }
                    }
                }
                setRecyclerView(userDataArrayList, recyclerView);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        setRecyclerView(FirebaseDB.getGameData().getUsers(), recyclerView);
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
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    private void setRecyclerView(ArrayList<UserData> userData, RecyclerView recyclerView) {
        RecyclerViewPlayerListAdapter recyclerViewPlayerListAdapter = new RecyclerViewPlayerListAdapter(userData, rootView.getContext(), this);
        recyclerView.setAdapter(recyclerViewPlayerListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
    }
}
