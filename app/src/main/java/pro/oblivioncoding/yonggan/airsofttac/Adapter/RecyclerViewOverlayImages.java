package pro.oblivioncoding.yonggan.airsofttac.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import pro.oblivioncoding.yonggan.airsofttac.Activitys.MainActivity;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.OverlayImageCollection.OverlayImage;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.MapFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class RecyclerViewOverlayImages extends RecyclerView.Adapter<RecyclerViewOverlayImages.ViewHolder> {

    private ArrayList<DocumentSnapshot> overlayImageArrayList;

    public RecyclerViewOverlayImages(final ArrayList<DocumentSnapshot> chatData) {
        overlayImageArrayList = chatData;
    }

    @NonNull
    @Override
    public RecyclerViewOverlayImages.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_overlay_image, viewGroup, false);
        final RecyclerViewOverlayImages.ViewHolder holder = new RecyclerViewOverlayImages.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewOverlayImages.ViewHolder viewHolder, final int i) {
        final OverlayImage overlayImage = this.overlayImageArrayList.get(i).toObject(OverlayImage.class);
        viewHolder.name.setText(overlayImage.getName());
        viewHolder.loadButton.setOnClickListener(v -> {
            MapFragment.setOverlayImage(overlayImage);
            MapFragment.setOverlayImageTitle(overlayImage.getName());
            MainActivity.getInstance().getMapFragment().showImageGroundOverlay();
        });
    }

    @Override
    public int getItemCount() {
        return overlayImageArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        Button loadButton;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            loadButton = itemView.findViewById(R.id.loadOverlayImage);
        }
    }

}
