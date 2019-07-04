package pro.oblivioncoding.yonggan.airsofttac.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.GotoSearchPlayerSelection;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.MapFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class RecyclerViewGotoPlayerListAdapter extends RecyclerView.Adapter<RecyclerViewGotoPlayerListAdapter.ViewHolder> {
    private ArrayList<UserData> userDataArrayList;
    private Context context;
    private FragmentManager fragmentManager;
    private GotoSearchPlayerSelection gotoSearchPlayerSelection;

    public RecyclerViewGotoPlayerListAdapter(final ArrayList<UserData> userDataArrayList, final FragmentManager fragmentManager, final Context context, GotoSearchPlayerSelection gotoSearchPlayerSelection) {
        this.userDataArrayList = userDataArrayList;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.gotoSearchPlayerSelection = gotoSearchPlayerSelection;
    }

    @NonNull
    @Override
    public RecyclerViewGotoPlayerListAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_goto_player_list_item, viewGroup, false);
        final RecyclerViewGotoPlayerListAdapter.ViewHolder holder = new RecyclerViewGotoPlayerListAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewGotoPlayerListAdapter.ViewHolder viewHolder, final int i) {
        final UserData gotoUserData = userDataArrayList.get(i);

        viewHolder.userEmail.setText(gotoUserData.getEmail());
        viewHolder.userNickname.setText(gotoUserData.getNickname());
        if (gotoUserData.getTeam() != null)
            viewHolder.teamName.setText(gotoUserData.getTeam());

        viewHolder.gotoPlayer.setOnClickListener(v -> {
            GoogleMap googleMap = MapFragment.getGoogleMap();
            if (googleMap != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                        gotoUserData.getPositionLat(),
                        gotoUserData.getPositionLong())));
            }
            fragmentManager.beginTransaction().remove(gotoSearchPlayerSelection).commit();
        });
    }

    @Override
    public int getItemCount() {
        return userDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userEmail, userNickname, teamName;
        Button gotoPlayer;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.userEmail);
            userNickname = itemView.findViewById(R.id.nickName);
            teamName = itemView.findViewById(R.id.teamName);
            gotoPlayer = itemView.findViewById(R.id.gotoPlayer);
        }
    }
}
