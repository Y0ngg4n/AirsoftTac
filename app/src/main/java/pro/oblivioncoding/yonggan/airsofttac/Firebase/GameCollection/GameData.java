package pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection;

import android.support.annotation.Nullable;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.FlagMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.HQMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.MissionMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.RespawnMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.TacticalMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Teams.TeamData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;

public class GameData {

    private String GameID, creatorEmail;

    private Timestamp startTime;

    private Timestamp endTime;

    private ArrayList<UserData> users;
    private ArrayList<TeamData> teams;

    private ArrayList<FlagMarkerData> flagMarkerData;
    private ArrayList<HQMarkerData> hqMarkerData;
    private ArrayList<MissionMarkerData> missionMarkerData;
    private ArrayList<RespawnMarkerData> respawnMarkerData;
    private ArrayList<TacticalMarkerData> tacticalMarkerData;

    @Nullable
    public UserData getOwnUserData(String email) {
        if (users != null) {
            for (UserData user : users) {
                if (user.getEmail().equals(email)) return user;
            }
        }
        return null;
    }

    public GameData() {
    }


    public GameData(String gameID, String creatorEmail, Timestamp startTime, Timestamp endTime, ArrayList<UserData> users) {
        GameID = gameID;
        this.creatorEmail = creatorEmail;
        this.startTime = startTime;
        this.endTime = endTime;
        this.users = users;
    }

    public GameData(String gameID, String creatorEmail, Timestamp startTime, Timestamp endTime,
                    ArrayList<UserData> users, ArrayList<FlagMarkerData> flagMarkerData,
                    ArrayList<HQMarkerData> hqMarkerData, ArrayList<MissionMarkerData> missionMarkerData,
                    ArrayList<RespawnMarkerData> respawnMarkerData, ArrayList<TacticalMarkerData> tacticalMarkerData) {
        GameID = gameID;
        this.creatorEmail = creatorEmail;
        this.startTime = startTime;
        this.endTime = endTime;
        this.users = users;
        this.flagMarkerData = flagMarkerData;
        this.hqMarkerData = hqMarkerData;
        this.missionMarkerData = missionMarkerData;
        this.respawnMarkerData = respawnMarkerData;
        this.tacticalMarkerData = tacticalMarkerData;
    }


    public String getGameID() {
        return GameID;
    }

    public void setGameID(String gameID) {
        GameID = gameID;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public ArrayList<UserData> getUsers() {
        if (users == null) users = new ArrayList<>();
        return users;
    }

    public void setUsers(ArrayList<UserData> users) {
        this.users = users;
    }

    public ArrayList<FlagMarkerData> getFlagMarkerData() {
        if (flagMarkerData == null) flagMarkerData = new ArrayList<>();
        return flagMarkerData;
    }

    public void setFlagMarkerData(ArrayList<FlagMarkerData> flagMarkerData) {
        this.flagMarkerData = flagMarkerData;
    }

    public ArrayList<HQMarkerData> getHqMarkerData() {
        if (hqMarkerData == null) hqMarkerData = new ArrayList<>();
        return hqMarkerData;
    }

    public void setHqMarkerData(ArrayList<HQMarkerData> hqMarkerData) {
        this.hqMarkerData = hqMarkerData;
    }

    public ArrayList<MissionMarkerData> getMissionMarkerData() {
        if (missionMarkerData == null) missionMarkerData = new ArrayList<>();
        return missionMarkerData;
    }

    public void setMissionMarkerData(ArrayList<MissionMarkerData> missionMarkerData) {
        this.missionMarkerData = missionMarkerData;
    }

    public ArrayList<RespawnMarkerData> getRespawnMarkerData() {
        if (respawnMarkerData == null) respawnMarkerData = new ArrayList<>();
        return respawnMarkerData;
    }

    public void setRespawnMarkerData(ArrayList<RespawnMarkerData> respawnMarkerData) {
        this.respawnMarkerData = respawnMarkerData;
    }

    public ArrayList<TacticalMarkerData> getTacticalMarkerData() {
        if (tacticalMarkerData == null) tacticalMarkerData = new ArrayList<>();
        return tacticalMarkerData;
    }

    public ArrayList<TeamData> getTeams() {
        if (teams == null) teams = new ArrayList<>();
        return teams;
    }

    public void setTeams(ArrayList<TeamData> teams) {
        this.teams = teams;
    }

    public void setTacticalMarkerData(ArrayList<TacticalMarkerData> tacticalMarkerData) {
        this.tacticalMarkerData = tacticalMarkerData;
    }
}


