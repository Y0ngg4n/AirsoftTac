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

    public TeamData() {
    }

    public TeamData(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public ArrayList<String> getUsers() {
        if (users == null) users = new ArrayList<>();
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public FlagMarkerData getFlagMarkerData() {
        return flagMarkerData;
    }

    public void setFlagMarkerData(FlagMarkerData flagMarkerData) {
        this.flagMarkerData = flagMarkerData;
    }

    public HQMarkerData getHqMarkerData() {
        return hqMarkerData;
    }

    public void setHqMarkerData(HQMarkerData hqMarkerData) {
        this.hqMarkerData = hqMarkerData;
    }

    public MissionMarkerData getMissionMarkerData() {
        return missionMarkerData;
    }

    public void setMissionMarkerData(MissionMarkerData missionMarkerData) {
        this.missionMarkerData = missionMarkerData;
    }

    public RespawnMarkerData getRespawnMarkerData() {
        return respawnMarkerData;
    }

    public void setRespawnMarkerData(RespawnMarkerData respawnMarkerData) {
        this.respawnMarkerData = respawnMarkerData;
    }

    public TacticalMarkerData getTacticalMarkerData() {
        return tacticalMarkerData;
    }

    public void setTacticalMarkerData(TacticalMarkerData tacticalMarkerData) {
        this.tacticalMarkerData = tacticalMarkerData;
    }
}
