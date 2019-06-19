package pro.oblivioncoding.yonggan.airsofttac.InfoWindowAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import pro.oblivioncoding.yonggan.airsofttac.R;


public class CustomMarkerTeamInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View mWindow;
    private Context mContext;

    private double latitude, longitude;

    private String title, description, teamName;

    public CustomMarkerTeamInfoWindowAdapter(final Context mContext, final double latitude, final double longitude, final String title, final String description, final String teamName) {
        this.mContext = mContext;
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.fragment_custom_marker_team_info_window_adapter, null);
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
        this.teamName = teamName;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        return mWindow;
    }

    private void renderWindowText(final Marker marker, final View view) {
        ((TextView) view.findViewById(R.id.title)).setText(title);
        ((TextView) view.findViewById(R.id.latitude)).setText(String.valueOf(latitude));
        ((TextView) view.findViewById(R.id.longitude)).setText(String.valueOf(longitude));
        ((TextView) view.findViewById(R.id.description)).setText(description);
        ((TextView) view.findViewById(R.id.teamName)).setText(teamName);
    }
}
