package pro.oblivioncoding.yonggan.airsofttac.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import pro.oblivioncoding.yonggan.airsofttac.Adapter.RecyclerViewTeamListAdapter;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Teams.TeamData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.CreateTeamDialogFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeamFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeamFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View rootView;

    public TeamFragment.ShowSettings showSettings = TeamFragment.ShowSettings.AllPlayer;
    private RecyclerView recyclerView;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_team, container, false);
        recyclerView = rootView.findViewById(R.id.teamList);
        final TextView searchTeamList = rootView.findViewById(R.id.teamListSearch);
        final FloatingActionButton teamListAddFB = rootView.findViewById(R.id.teamListAddTeamFB);
        teamListAddFB.setOnClickListener(v -> {
            CreateTeamDialogFragment createTeamDialogFragment = CreateTeamDialogFragment.newInstance("New Team", this, recyclerView);
            createTeamDialogFragment.show(getFragmentManager(), "create_team_dialog");
            setRecyclerView(FirebaseDB.getGameData().getTeams(), recyclerView);
            setAdapter(FirebaseDB.getGameData().getTeams());
        });
        final UserData ownUserData = FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail());
        if (ownUserData.getTeam() != null && !ownUserData.isOrga())
            teamListAddFB.hide();
        searchTeamList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<TeamData> teamDataArrayList = FirebaseDB.getGameData().getTeams();
                if (!s.toString().isEmpty()) {
                    teamDataArrayList = new ArrayList<>();
                    for (TeamData teamData : FirebaseDB.getGameData().getTeams()) {
                        if (teamData.getTeamName().toLowerCase().contains(s.toString().toLowerCase())
                        || String.valueOf(teamData.getMinorRadioChannel()).toLowerCase().contains(s.toString().toLowerCase())
                        || String.valueOf(teamData.getMajorRadioChannel()).toLowerCase().contains(s.toString().toLowerCase())) {
                            teamDataArrayList.add(teamData);
                        }
                    }
                }
                setRecyclerView(teamDataArrayList, recyclerView);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        setRecyclerView(FirebaseDB.getGameData().getTeams(), recyclerView);
        return rootView;
    }

    public TeamFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeamFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TeamFragment newInstance(String param1, String param2) {
        TeamFragment fragment = new TeamFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void setRecyclerView(ArrayList<TeamData> teamData, RecyclerView recyclerView) {
        UserData ownUserData = FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail());
        ArrayList<TeamData> teamDataBuffer = new ArrayList<>();
        if (showSettings.equals(ShowSettings.ShowTeamOnly)) {
            for (TeamData team : teamData) {
                if (!ownUserData.getTeam().isEmpty()) {
                    if (ownUserData.getTeam().equals(team.getTeamName())) {
                        teamDataBuffer.add(team);
                    }
                }
            }
        } else if (showSettings.equals(ShowSettings.ShowOnlyNotAssigned)) {
            for (TeamData team : teamData) {
                if (team.getFlagMarkerData() == null && team.getHqMarkerData() == null
                        && team.getMissionMarkerData() == null && team.getRespawnMarkerData() == null
                        && team.getTacticalMarkerData() == null) {
                    teamDataBuffer.add(team);
                }
            }
        } else if (showSettings.equals(ShowSettings.AllPlayer)) {
            teamDataBuffer = teamData;
        }
        setAdapter(teamData);
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

    public enum ShowSettings {
        AllPlayer, ShowTeamOnly, ShowOnlyNotAssigned
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

    private void setAdapter(ArrayList<TeamData> teamData) {
        RecyclerViewTeamListAdapter recyclerViewTeamListAdapter = new RecyclerViewTeamListAdapter(getFragmentManager(), teamData, rootView.getContext(), this);
        recyclerView.setAdapter(recyclerViewTeamListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
    }
}
