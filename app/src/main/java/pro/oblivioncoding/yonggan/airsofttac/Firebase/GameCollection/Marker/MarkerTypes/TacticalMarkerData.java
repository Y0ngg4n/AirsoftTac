package pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes;

public class TacticalMarkerData {

    private double latitude, longitude;

    private String title, description, team;

    public TacticalMarkerData(){}

    public TacticalMarkerData(double latitude, double longitude, String title, String description, String team) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
        this.team = team;
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

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
