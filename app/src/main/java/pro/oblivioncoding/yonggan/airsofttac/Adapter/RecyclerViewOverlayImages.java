package pro.oblivioncoding.yonggan.airsofttac.Adapter;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

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
            byte[] decode = Base64.decode(overlayImage.getImage(), Base64.DEFAULT);
            GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory.decodeByteArray(decode, 0, decode.length)))
                    .anchor(0, 1)
                    .position(new LatLng(overlayImage.getLatitude(), overlayImage.getLongitude()),
                            overlayImage.getWidth());
            MapFragment.getGoogleMap().addGroundOverlay(groundOverlayOptions);
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
