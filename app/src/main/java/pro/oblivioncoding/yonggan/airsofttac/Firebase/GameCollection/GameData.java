package pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection;

import android.support.annotation.Nullable;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Chat.ChatMessage;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.FlagMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.HQMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.MissionMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.RespawnMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Marker.MarkerTypes.TacticalMarkerData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Teams.TeamData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;

public class GameData {

    private String GameID, creatorEmail, password, kmlTitle;

    private Timestamp startTime;

    private Timestamp endTime;

    private ArrayList<UserData> users;
    private ArrayList<TeamData> teams;

    private ArrayList<FlagMarkerData> flagMarkerData;
    private ArrayList<HQMarkerData> hqMarkerData;
    private ArrayList<MissionMarkerData> missionMarkerData;
    private ArrayList<RespawnMarkerData> respawnMarkerData;
    private ArrayList<TacticalMarkerData> tacticalMarkerData;

    private ArrayList<ChatMessage> chatMessages;

    public GameData(final String gameID, final String creatorEmail, final Timestamp startTime, final Timestamp endTime, final ArrayList<UserData> users, String password, String kmlTitle) {
        GameID = gameID;
        this.creatorEmail = creatorEmail;
        this.startTime = startTime;
        this.endTime = endTime;
        this.users = users;
        this.password = password;
        this.kmlTitle = kmlTitle;
    }

    public GameData() {
    }


    public GameData(final String gameID, final String creatorEmail, final Timestamp startTime, final Timestamp endTime,
                    final String password, final String kmlTitle,
                    final ArrayList<UserData> users, final ArrayList<FlagMarkerData> flagMarkerData,
                    final ArrayList<HQMarkerData> hqMarkerData, final ArrayList<MissionMarkerData> missionMarkerData,
                    final ArrayList<RespawnMarkerData> respawnMarkerData, final ArrayList<TacticalMarkerData> tacticalMarkerData) {
        GameID = gameID;
        this.password = password;
        this.creatorEmail = creatorEmail;
        this.startTime = startTime;
        this.endTime = endTime;
        this.users = users;
        this.kmlTitle = kmlTitle;
        this.flagMarkerData = flagMarkerData;
        this.hqMarkerData = hqMarkerData;
        this.missionMarkerData = missionMarkerData;
        this.respawnMarkerData = respawnMarkerData;
        this.tacticalMarkerData = tacticalMarkerData;
    }

    @Nullable
    public UserData getOwnUserData(final String email) {
        if (users != null) {
            for (final UserData user : users) {
                if (user.getEmail().equals(email)) return user;
            }
        }
        return null;
    }


    public String getGameID() {
        return GameID;
    }

    public void setGameID(final String gameID) {
        GameID = gameID;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(final String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(final Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(final Timestamp endTime) {
        this.endTime = endTime;
    }

    public ArrayList<UserData> getUsers() {
        if (users == null) users = new ArrayList<>();
        return users;
    }

    public void setUsers(final ArrayList<UserData> users) {
        this.users = users;
    }

    public ArrayList<FlagMarkerData> getFlagMarkerData() {
        if (flagMarkerData == null) flagMarkerData = new ArrayList<>();
        return flagMarkerData;
    }

    public void setFlagMarkerData(final ArrayList<FlagMarkerData> flagMarkerData) {
        this.flagMarkerData = flagMarkerData;
    }

    public ArrayList<HQMarkerData> getHqMarkerData() {
        if (hqMarkerData == null) hqMarkerData = new ArrayList<>();
        return hqMarkerData;
    }

    public void setHqMarkerData(final ArrayList<HQMarkerData> hqMarkerData) {
        this.hqMarkerData = hqMarkerData;
    }

    public ArrayList<MissionMarkerData> getMissionMarkerData() {
        if (missionMarkerData == null) missionMarkerData = new ArrayList<>();
        return missionMarkerData;
    }

    public void setMissionMarkerData(final ArrayList<MissionMarkerData> missionMarkerData) {
        this.missionMarkerData = missionMarkerData;
    }

    public ArrayList<RespawnMarkerData> getRespawnMarkerData() {
        if (respawnMarkerData == null) respawnMarkerData = new ArrayList<>();
        return respawnMarkerData;
    }

    public void setRespawnMarkerData(final ArrayList<RespawnMarkerData> respawnMarkerData) {
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

    public void setTeams(final ArrayList<TeamData> teams) {
        this.teams = teams;
    }

    public void setTacticalMarkerData(final ArrayList<TacticalMarkerData> tacticalMarkerData) {
        this.tacticalMarkerData = tacticalMarkerData;
    }

    public ArrayList<ChatMessage> getChatMessages() {
        if (chatMessages == null) chatMessages = new ArrayList<>();
        return chatMessages;
    }

    public void setChatMessages(final ArrayList<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKmlTitle() {
        return kmlTitle;
    }

    public void setKmlTitle(String kmlTitle) {
        this.kmlTitle = kmlTitle;
    }
}


