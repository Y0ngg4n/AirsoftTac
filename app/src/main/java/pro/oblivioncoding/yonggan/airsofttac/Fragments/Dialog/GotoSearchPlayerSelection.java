package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pro.oblivioncoding.yonggan.airsofttac.Adapter.RecyclerViewGotoPlayerListAdapter;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class GotoSearchPlayerSelection extends DialogFragment {

    View rootView;

    public GotoSearchPlayerSelection() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static GotoSearchPlayerSelection newInstance(final String title) {
        return new GotoSearchPlayerSelection();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.goto_player_selection_dialog, container);
        final RecyclerView recyclerView = rootView.findViewById(R.id.playerList);
        final TextView searchMarkerList = rootView.findViewById(R.id.playerListSearch);

        searchMarkerList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<UserData> userDataArrayList = FirebaseDB.getGameData().getUsers();
                if (!s.toString().isEmpty()) {
                    userDataArrayList = new ArrayList<>();
                    for (UserData userData : FirebaseDB.getGameData().getUsers()) {
                        String email = userData.getEmail();
                        String nickname = userData.getNickname();

                        if (email != null && nickname != null) {
                            if (email.toLowerCase().contains(s.toString().toLowerCase())
                                    || nickname.toLowerCase().contains(s.toString().toLowerCase())) {
                                userDataArrayList.add(userData);
                            }
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setRecyclerView(final ArrayList<UserData> markerTypes, final RecyclerView recyclerView) {
        final RecyclerViewGotoPlayerListAdapter recyclerViewGotoPlayerListAdapter = new RecyclerViewGotoPlayerListAdapter(markerTypes, getFragmentManager(), rootView.getContext(), this);
        recyclerView.setAdapter(recyclerViewGotoPlayerListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
    }
}
