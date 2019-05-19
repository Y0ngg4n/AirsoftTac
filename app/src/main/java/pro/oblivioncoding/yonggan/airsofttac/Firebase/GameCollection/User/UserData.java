package pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User;


import com.google.android.gms.maps.model.LatLng;

public class UserData {

    private String email;

    private boolean orga, alive = true, support, mission, underfire;

    private double positionLat, positionLong;

    public UserData() {
    }

    public UserData(String email, boolean orga) {
        this.email = email;
        this.orga = orga;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isOrga() {
        return orga;
    }

    public void setOrga(boolean orga) {
        this.orga = orga;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isSupport() {
        return support;
    }

    public void setSupport(boolean support) {
        this.support = support;
    }

    public boolean isMission() {
        return mission;
    }

    public void setMission(boolean mission) {
        this.mission = mission;
    }

    public boolean isUnderfire() {
        return underfire;
    }

    public void setUnderfire(boolean underfire) {
        this.underfire = underfire;
    }

    public double getPositionLat() {
        return positionLat;
    }

    public void setPositionLat(double positionLat) {
        this.positionLat = positionLat;
    }

    public double getPositionLong() {
        return positionLong;
    }

    public void setPositionLong(double positionLong) {
        this.positionLong = positionLong;
    }
}
