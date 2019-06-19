package pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User;


public class UserData {

    private String email, nickname;

    private boolean orga, alive = true, support, mission, underfire;

    private double positionLat, positionLong;

    private String team;

    public UserData() {
    }

    public UserData(final String email, final boolean orga) {
        this.email = email;
        this.orga = orga;
    }

    public UserData(final String email, final boolean orga, final String nickname) {
        this.email = email;
        this.orga = orga;
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public boolean isOrga() {
        return orga;
    }

    public void setOrga(final boolean orga) {
        this.orga = orga;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(final boolean alive) {
        this.alive = alive;
    }

    public boolean isSupport() {
        return support;
    }

    public void setSupport(final boolean support) {
        this.support = support;
    }

    public boolean isMission() {
        return mission;
    }

    public void setMission(final boolean mission) {
        this.mission = mission;
    }

    public boolean isUnderfire() {
        return underfire;
    }

    public void setUnderfire(final boolean underfire) {
        this.underfire = underfire;
    }

    public double getPositionLat() {
        return positionLat;
    }

    public void setPositionLat(final double positionLat) {
        this.positionLat = positionLat;
    }

    public double getPositionLong() {
        return positionLong;
    }

    public void setPositionLong(final double positionLong) {
        this.positionLong = positionLong;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(final String team) {
        this.team = team;
    }
}
