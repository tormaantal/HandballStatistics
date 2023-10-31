package hu.szakdolgozat.handballstatistics.models;

import androidx.annotation.NonNull;

public class Match {
    private final long matchId;
    private final long playerId;
    private final String opponent;
    private final String date;

    public Match(long matchId, long playerId, String date, String opponent) {
        this.matchId = matchId;
        this.playerId = playerId;
        this.opponent = opponent;
        this.date = date;
    }

    public long getMatchId() {
        return matchId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public String getOpponent() {
        return opponent;
    }

    public String getDate() {
        return date;
    }

    @NonNull
    @Override
    public String toString() {
        return matchId +
                " = " + playerId +
                " = " + opponent +
                " = " + date;
    }
}
