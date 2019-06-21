package pro.oblivioncoding.yonggan.airsofttac.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.MapStyleCollection.MapStyleData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.MapFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class RecyclerViewMapStyle extends RecyclerView.Adapter<RecyclerViewMapStyle.ViewHolder> {

    private ArrayList<DocumentSnapshot> mapStyleData;

    public RecyclerViewMapStyle(final ArrayList<DocumentSnapshot> mapStyleData) {
        this.mapStyleData = mapStyleData;
    }

    @NonNull
    @Override
    public RecyclerViewMapStyle.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_map_style, viewGroup, false);
        final RecyclerViewMapStyle.ViewHolder holder = new RecyclerViewMapStyle.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewMapStyle.ViewHolder viewHolder, final int i) {
        MapStyleData mapStyleData = this.mapStyleData.get(i).toObject(MapStyleData.class);
        viewHolder.title.setText(mapStyleData.getTitle());
        viewHolder.applyButton.setOnClickListener(v -> {
            MapFragment.setMapStyle(mapStyleData.getJson());
        });
    }

    @Override
    public int getItemCount() {
        return mapStyleData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        Button applyButton;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.mapstyletitle);
            applyButton = itemView.findViewById(R.id.mapstyleapply);
        }
    }
}
