package pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerType;

public class TacticalMarkerData extends MarkerType {

    public TacticalMarkerData(){}

    public TacticalMarkerData(final double latitude, final double longitude, final String title, final String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
