package pro.oblivioncoding.yonggan.airsofttac.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

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

    public RecyclerViewPlayerListAdapter(final ArrayList<UserData> userDataArrayList, final Context context, final PlayerFragment playerFragment) {
        this.userDataArrayList = userDataArrayList;
        this.context = context;
        this.playerFragment = playerFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_player_list_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final UserData userData = userDataArrayList.get(i);
        viewHolder.isOrga.setChecked(userData.isOrga());
        viewHolder.first.setChecked(userData.isFirst());
        viewHolder.second.setChecked(userData.isSecond());
        viewHolder.email.setText(userData.getEmail());
        viewHolder.nickname.setText(userData.getNickname());
    }

    @Override
    public int getItemCount() {
        return userDataArrayList.size();
    }

    private void showAfterTime(@NonNull final Switch button, final long delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                playerFragment.getActivity().runOnUiThread(() -> {
                    button.setVisibility(View.VISIBLE);
                });
            }
        }, delay);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView email, nickname;
        Switch isOrga, first, second;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.playerListEmail);
            nickname = itemView.findViewById(R.id.playerListNickName);
            isOrga = itemView.findViewById(R.id.playerOrgaSwitch);
            first = itemView.findViewById(R.id.playerFirstSwitch);
            second = itemView.findViewById(R.id.playerSecondSwitch);
            constraintLayout = itemView.findViewById(R.id.playerListLayout);
            FirebaseDB.getGames().whereEqualTo(context.getResources().getString(R.string.firebase_firestore_variable_games_gameID), FirebaseDB.getGameData().getGameID())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        isOrga.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            FirebaseDB.getGameData().getOwnUserData(email.getText().toString()).setOrga(isOrga.isChecked());
                            FirebaseDB.updateObject(documentSnapshot, context.getResources().getString(R.string.firebase_firestore_variable_games_users),
                                    FirebaseDB.getGameData().getUsers());
                            isOrga.setVisibility(View.INVISIBLE);
                            showAfterTime(isOrga, 30000L);
                        });

                        first.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            FirebaseDB.getGameData().getOwnUserData(email.getText().toString()).setFirst(first.isChecked());
                            FirebaseDB.updateObject(documentSnapshot, context.getResources().getString(R.string.firebase_firestore_variable_games_users),
                                    FirebaseDB.getGameData().getUsers());
                            first.setVisibility(View.INVISIBLE);
                            showAfterTime(first, 30000L);
                        });

                        second.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            FirebaseDB.getGameData().getOwnUserData(email.getText().toString()).setSecond(second.isChecked());
                            FirebaseDB.updateObject(documentSnapshot, context.getResources().getString(R.string.firebase_firestore_variable_games_users),
                                    FirebaseDB.getGameData().getUsers());
                            second.setVisibility(View.INVISIBLE);
                            showAfterTime(second, 30000L);
                        });
                    }
                }
            });
        }
    }
}
