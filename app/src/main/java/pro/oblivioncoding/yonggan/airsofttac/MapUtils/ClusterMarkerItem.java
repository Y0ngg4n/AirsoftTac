package pro.oblivioncoding.yonggan.airsofttac.MapUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarkerItem implements ClusterItem {

    @NonNull
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;

    public ClusterMarkerItem(final double lat, final double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public ClusterMarkerItem(final double lat, final double lng, final String title, final String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
