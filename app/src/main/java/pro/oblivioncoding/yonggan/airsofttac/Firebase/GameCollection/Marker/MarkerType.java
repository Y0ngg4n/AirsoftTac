package pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker;

public class MarkerType {

    protected double latitude, longitude;

    protected String title, description;

    public MarkerType() {
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
}
