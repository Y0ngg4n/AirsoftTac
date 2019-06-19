package pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Teams;

import java.util.ArrayList;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.FlagMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.HQMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.MissionMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.RespawnMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.TacticalMarkerData;

public class TeamData {

    private String teamName;

    private ArrayList<String> users;

    private FlagMarkerData flagMarkerData;
    private HQMarkerData hqMarkerData;
    private MissionMarkerData missionMarkerData;
    private RespawnMarkerData respawnMarkerData;
    private TacticalMarkerData tacticalMarkerData;

    private int minorRadioChannel, majorRadioChannel;

    public TeamData() {
    }

    public TeamData(final String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(final String teamName) {
        this.teamName = teamName;
    }

    public ArrayList<String> getUsers() {
        if (users == null) users = new ArrayList<>();
        return users;
    }

    public void setUsers(final ArrayList<String> users) {
        this.users = users;
    }

    public FlagMarkerData getFlagMarkerData() {
        return flagMarkerData;
    }

    public void setFlagMarkerData(final FlagMarkerData flagMarkerData) {
        this.flagMarkerData = flagMarkerData;
    }

    public HQMarkerData getHqMarkerData() {
        return hqMarkerData;
    }

    public void setHqMarkerData(final HQMarkerData hqMarkerData) {
        this.hqMarkerData = hqMarkerData;
    }

    public MissionMarkerData getMissionMarkerData() {
        return missionMarkerData;
    }

    public void setMissionMarkerData(final MissionMarkerData missionMarkerData) {
        this.missionMarkerData = missionMarkerData;
    }

    public RespawnMarkerData getRespawnMarkerData() {
        return respawnMarkerData;
    }

    public void setRespawnMarkerData(final RespawnMarkerData respawnMarkerData) {
        this.respawnMarkerData = respawnMarkerData;
    }

    public TacticalMarkerData getTacticalMarkerData() {
        return tacticalMarkerData;
    }

    public void setTacticalMarkerData(final TacticalMarkerData tacticalMarkerData) {
        this.tacticalMarkerData = tacticalMarkerData;
    }

    public int getMinorRadioChannel() {
        return minorRadioChannel;
    }

    public void setMinorRadioChannel(final int minorRadioChannel) {
        this.minorRadioChannel = minorRadioChannel;
    }

    public int getMajorRadioChannel() {
        return majorRadioChannel;
    }

    public void setMajorRadioChannel(final int majorRadioChannel) {
        this.majorRadioChannel = majorRadioChannel;
    }
}