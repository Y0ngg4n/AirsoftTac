package pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerType;

public class TacticalMarkerData extends MarkerType {

    public TacticalMarkerData(){}

    public TacticalMarkerData(double latitude, double longitude, String title, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
