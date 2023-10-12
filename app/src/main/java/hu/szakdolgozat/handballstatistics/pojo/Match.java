package hu.szakdolgozat.handballstatistics.pojo;

import java.util.ArrayList;
import java.util.Date;

public class Match {
    private int matchId;
    private int playerId;
    private String opponent;
    private String date;
    private ArrayList<Event> eventsList;

    public Match(int matchId, int playerId, String opponent, String date) {
        this.matchId = matchId;
        this.playerId = playerId;
        this.opponent = opponent;
        this.date = date;
        this.eventsList = new ArrayList<>();
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Event> getEventsList() {
        return eventsList;
    }

    public void setEventsList(ArrayList<Event> eventsList) {
        this.eventsList = eventsList;
    }
}
