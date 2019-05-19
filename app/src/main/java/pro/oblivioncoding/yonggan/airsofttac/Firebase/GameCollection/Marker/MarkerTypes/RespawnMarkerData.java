package pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes;

public class RespawnMarkerData {

    private double latitude, longitude;

    private String title, desription, team;

    public RespawnMarkerData(){}

    public RespawnMarkerData(double latitude, double longitude, String title, String desription, String team) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.desription = desription;
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

    public String getDesription() {
        return desription;
    }

    public void setDesription(String desription) {
        this.desription = desription;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
