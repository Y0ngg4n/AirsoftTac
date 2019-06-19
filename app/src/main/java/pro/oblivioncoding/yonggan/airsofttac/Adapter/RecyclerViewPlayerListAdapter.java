package pro.oblivioncoding.yonggan.airsofttac.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.PlayerFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class RecyclerViewPlayerListAdapter extends RecyclerView.Adapter<RecyclerViewPlayerListAdapter.ViewHolder> {

    private ArrayList<UserData> userDataArrayList;
    private Context context;
    private PlayerFragment playerFragment;
    public RecyclerViewPlayerListAdapter(ArrayList<UserData> userDataArrayList, Context context, PlayerFragment playerFragment) {
        this.userDataArrayList = userDataArrayList;
        this.context = context;
        this.playerFragment = playerFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_player_list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final UserData userData = userDataArrayList.get(i);
        viewHolder.isOrga.setChecked(userData.isOrga());
        viewHolder.email.setText(userData.getEmail());
        viewHolder.nickname.setText(userData.getNickname());
    }

    @Override
    public int getItemCount() {
        return userDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView email, nickname;
        Switch isOrga;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.playerListEmail);
            nickname = itemView.findViewById(R.id.playerListNickName);
            isOrga = itemView.findViewById(R.id.playerOrgaSwitch);
            constraintLayout = itemView.findViewById(R.id.playerListLayout);
            FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        isOrga.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            FirebaseDB.getGameData().getOwnUserData(email.getText().toString()).setOrga(isOrga.isChecked());
                            FirebaseDB.updateObject(documentSnapshot, "users",
                                    FirebaseDB.getGameData().getUsers());
                            isOrga.setVisibility(View.INVISIBLE);
                            showAfterTime(isOrga, 30000L);
                        });
                    }
                }
            });
        }
    }
    private void showAfterTime(Switch button, long delay){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                playerFragment.getActivity().runOnUiThread(() -> {
                    button.setVisibility(View.VISIBLE);
                });
            }
        }, delay);
    }
}
