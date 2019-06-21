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

import pro.oblivioncoding.yonggan.airsofttac.Activitys.CreateGameActivity;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.KMLCollection.KMLData;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class RecyclerViewKML extends RecyclerView.Adapter<RecyclerViewKML.ViewHolder> {


    private ArrayList<DocumentSnapshot> customMap;
    private CreateGameActivity createGameActivity;

    public RecyclerViewKML(final ArrayList<DocumentSnapshot> customMap, CreateGameActivity createGameActivity) {
        this.customMap = customMap;
        this.createGameActivity = createGameActivity;
    }

    @NonNull
    @Override
    public RecyclerViewKML.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_custom_map, viewGroup, false);
        final RecyclerViewKML.ViewHolder holder = new RecyclerViewKML.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewKML.ViewHolder viewHolder, final int i) {
        KMLData kmlData = this.customMap.get(i).toObject(KMLData.class);
        viewHolder.title.setText(kmlData.getTitle());
        viewHolder.applyButton.setOnClickListener(v -> {
            ((TextView) createGameActivity.findViewById(R.id.kmlTitleLabel)).setText(kmlData.getTitle());
        });
    }

    @Override
    public int getItemCount() {
        return customMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        Button applyButton;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.custommaptitle);
            applyButton = itemView.findViewById(R.id.custommapapply);
        }
    }
}
