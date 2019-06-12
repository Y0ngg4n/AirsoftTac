package pro.oblivioncoding.yonggan.airsofttac.InfoWindowAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import pro.oblivioncoding.yonggan.airsofttac.R;


public class CustomMarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{

    private View mWindow;
    private Context mContext;

    private double latitude, longitude;

    private String title, description;

    public CustomMarkerInfoWindowAdapter(Context mContext, double latitude, double longitude, String title, String description) {
        this.mContext = mContext;
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.fragment_custom_marker_info_window_adapter, null);
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return mWindow;
    }

    private void renderWindowText(Marker marker, View view) {
        ((TextView) view.findViewById(R.id.title)).setText(title);
        ((TextView) view.findViewById(R.id.latitudeLabel)).setText(String.valueOf(latitude));
        ((TextView) view.findViewById(R.id.longitude)).setText(String.valueOf(longitude));
        ((TextView) view.findViewById(R.id.description)).setText(description);
    }
}
