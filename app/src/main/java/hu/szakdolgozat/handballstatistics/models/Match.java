package hu.szakdolgozat.handballstatistics.models;

import java.util.ArrayList;

public class Match {
    private int matchId;
    private int playerId;
    private String opponent;
    private String date;
    private ArrayList<Event> eventList;

    public Match(int matchId, int playerId, String opponent, String date) {
        this.matchId = matchId;
        this.playerId = playerId;
        this.opponent = opponent;
        this.date = date;
        this.eventList = new ArrayList<>();
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

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }
}
