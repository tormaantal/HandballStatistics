package hu.szakdolgozat.handballstatistics.services;

import android.content.Context;

import java.util.ArrayList;

import hu.szakdolgozat.handballstatistics.database.DatabaseHelper;
import hu.szakdolgozat.handballstatistics.models.EventType;
import hu.szakdolgozat.handballstatistics.models.Match;

public class MatchServices {
    private final DatabaseHelper db;

    public MatchServices(Context context) {
        this.db = new DatabaseHelper(context);
    }

    public long addMatch(long playerId, String date, String opponent) {
        return db.addMatch(playerId, date, opponent);
    }

    public int deleteMatch(long id) {
        return db.deleteMatch(id);
    }

    public ArrayList<Match> findAllMatch() {
        return db.findAllMatch();
    }

    public ArrayList<Match> findAllMatchByPlayerId(long playerId){
        return db.findAllMatchByPlayerId(playerId);
    }

    public Match findMatchById(long id) {
        return db.findMatchById(id);
    }

    public long findAllSaveByMatch(long matchId) {
        return db.findAllSaveByMatch(matchId);
    }

    public long findAllGoalByMatch(long matchId) {
        return db.findAllGoalByMatch(matchId);
    }

    public long findAllSaveByMatchByType(long matchId, EventType type) {
        return db.findAllSaveByMatchByType(matchId, type);
    }

    public long findAllGoalByMatchByType(long matchId, EventType type) {
        return db.findAllGoalByMatchByType(matchId, type);
    }

    public long findAllYellowCardByMatch(long matchId) {
        return db.findAllYellowCardByMatch(matchId);
    }

    public long findAllTwoMinutesByMatch(long matchId) {
        return db.findAllTwoMinutesByMatch(matchId);
    }

    public long findAllRedCardByMatch(long matchId) {
        return db.findAllRedCardByMatch(matchId);
    }

    public long findAllBlueCardByMatch(long matchId) {
        return db.findAllBlueCardByMatch(matchId);
    }
}
