package pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.FlagMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.HQMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.MissionMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.RespawnMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.TacticalMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;

public class GameData {

    private String GameID, creatorEmail;

    private Timestamp startTime;

    private Timestamp endTime;

    private ArrayList<UserData> users;

    private ArrayList<FlagMarkerData> flagMarkerData;
    private ArrayList<HQMarkerData> hqMarkerData;
    private ArrayList<MissionMarkerData> missionMarkerData;
    private ArrayList<RespawnMarkerData> respawnMarkerData;
    private ArrayList<TacticalMarkerData> tacticalMarkerData;

    public UserData getOwnUserData(String email){
        for (UserData user : users) {
            if(user.getEmail().equals(email)) return user;
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
        return users;
    }

    public void setUsers(ArrayList<UserData> users) {
        this.users = users;
    }

    public ArrayList<FlagMarkerData> getFlagMarkerData() {
        return flagMarkerData;
    }

    public void setFlagMarkerData(ArrayList<FlagMarkerData> flagMarkerData) {
        this.flagMarkerData = flagMarkerData;
    }

    public ArrayList<HQMarkerData> getHqMarkerData() {
        return hqMarkerData;
    }

    public void setHqMarkerData(ArrayList<HQMarkerData> hqMarkerData) {
        this.hqMarkerData = hqMarkerData;
    }

    public ArrayList<MissionMarkerData> getMissionMarkerData() {
        return missionMarkerData;
    }

    public void setMissionMarkerData(ArrayList<MissionMarkerData> missionMarkerData) {
        this.missionMarkerData = missionMarkerData;
    }

    public ArrayList<RespawnMarkerData> getRespawnMarkerData() {
        return respawnMarkerData;
    }

    public void setRespawnMarkerData(ArrayList<RespawnMarkerData> respawnMarkerData) {
        this.respawnMarkerData = respawnMarkerData;
    }

    public ArrayList<TacticalMarkerData> getTacticalMarkerData() {
        return tacticalMarkerData;
    }

    public void setTacticalMarkerData(ArrayList<TacticalMarkerData> tacticalMarkerData) {
        this.tacticalMarkerData = tacticalMarkerData;
    }
}

