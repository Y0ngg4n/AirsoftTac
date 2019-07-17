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

import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerType;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.FlagMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.HQMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.MissionMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.RespawnMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.TacticalMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.GotoSearchMarkerSelection;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.MapFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class RecyclerViewGotoMarkerListAdapter extends RecyclerView.Adapter<RecyclerViewGotoMarkerListAdapter.ViewHolder> {
    private ArrayList<MarkerType> markerTypeArrayList;
    private Context context;
    private FragmentManager fragmentManager;
    private GotoSearchMarkerSelection gotoSearchMarkerSelection;

    public RecyclerViewGotoMarkerListAdapter(final ArrayList<MarkerType> markerTypeArrayList, final FragmentManager fragmentManager, final Context context, GotoSearchMarkerSelection gotoSearchMarkerSelection) {
        this.markerTypeArrayList = markerTypeArrayList;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.gotoSearchMarkerSelection = gotoSearchMarkerSelection;
    }

    @NonNull
    @Override
    public RecyclerViewGotoMarkerListAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_goto_marker_list_item, viewGroup, false);
        final RecyclerViewGotoMarkerListAdapter.ViewHolder holder = new RecyclerViewGotoMarkerListAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewGotoMarkerListAdapter.ViewHolder viewHolder, final int i) {
        final MarkerType gotoMarkerData = markerTypeArrayList.get(i);

        if (gotoMarkerData instanceof FlagMarkerData) {
            viewHolder.markerTitle.setText(gotoMarkerData.getTitle());
            viewHolder.markerDescription.setText(gotoMarkerData.getDescription());
            viewHolder.markerType.setText(R.string.marker_markerType_flag);
        } else if (gotoMarkerData instanceof HQMarkerData) {
            viewHolder.markerTitle.setText(gotoMarkerData.getTitle());
            viewHolder.markerDescription.setText(gotoMarkerData.getDescription());
            viewHolder.markerType.setText(R.string.marker_markerType_hq);
        } else if (gotoMarkerData instanceof MissionMarkerData) {
            viewHolder.markerTitle.setText(gotoMarkerData.getTitle());
            viewHolder.markerDescription.setText(gotoMarkerData.getDescription());
            viewHolder.markerType.setText(R.string.marker_markerType_mission);
        } else if (gotoMarkerData instanceof RespawnMarkerData) {
            viewHolder.markerTitle.setText(gotoMarkerData.getTitle());
            viewHolder.markerDescription.setText(gotoMarkerData.getDescription());
            viewHolder.markerType.setText(R.string.marker_markerType_respawn);
        } else if (gotoMarkerData instanceof TacticalMarkerData) {
            viewHolder.markerTitle.setText(gotoMarkerData.getTitle());
            viewHolder.markerDescription.setText(gotoMarkerData.getDescription());
            viewHolder.markerType.setText(R.string.marker_markerType_tactical);
        }

        viewHolder.gotoMarker.setOnClickListener(v -> {
            GoogleMap googleMap = MapFragment.getGoogleMap();
            if (googleMap != null) {
                if (gotoMarkerData instanceof FlagMarkerData) {
                    FlagMarkerData flagMarkerData = (FlagMarkerData) gotoMarkerData;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                            flagMarkerData.getLatitude(),
                            flagMarkerData.getLongitude())));
                } else if (gotoMarkerData instanceof HQMarkerData) {
                    HQMarkerData hqMarkerData = (HQMarkerData) gotoMarkerData;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                            hqMarkerData.getLatitude(),
                            hqMarkerData.getLongitude())));
                } else if (gotoMarkerData instanceof MissionMarkerData) {
                    MissionMarkerData missionMarkerData = (MissionMarkerData) gotoMarkerData;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                            missionMarkerData.getLatitude(),
                            missionMarkerData.getLongitude())));
                } else if (gotoMarkerData instanceof RespawnMarkerData) {
                    RespawnMarkerData respawnMarkerData = (RespawnMarkerData) gotoMarkerData;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                            respawnMarkerData.getLatitude(),
                            respawnMarkerData.getLongitude())));
                } else if (gotoMarkerData instanceof TacticalMarkerData) {
                    TacticalMarkerData tacticalMarkerData = (TacticalMarkerData) gotoMarkerData;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                            tacticalMarkerData.getLatitude(),
                            tacticalMarkerData.getLongitude())));
                }
            }
            fragmentManager.beginTransaction().remove(gotoSearchMarkerSelection).commit();
        });
    }

    @Override
    public int getItemCount() {
        return markerTypeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView markerTitle, markerDescription, markerType;
        Button gotoMarker;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            markerTitle = itemView.findViewById(R.id.markerTitle);
            markerDescription = itemView.findViewById(R.id.markerDescription);
            markerType = itemView.findViewById(R.id.markerType);
            gotoMarker = itemView.findViewById(R.id.gotoMarker);
        }
    }
}
