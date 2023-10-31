package hu.szakdolgozat.handballstatistics.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

import hu.szakdolgozat.handballstatistics.database.DatabaseHelper;
import hu.szakdolgozat.handballstatistics.models.EventType;
import hu.szakdolgozat.handballstatistics.models.Match;

public class MatchServices {
    private final DatabaseHelper db;

    public MatchServices(Context context) {
        this.db = new DatabaseHelper(context);
    }

    public long addMatch(long playerId, String date, String opponent){
        return db.addMatch(playerId, date, opponent);
    }


    public int deleteMatch(long id){
        return db.deleteMatch(id);
    }

    public ArrayList<Match> findAllMatch(){
        return db.findAllMatch();
    }
    public Match findMatchById(long id){
        return db.findMatchById(id);
    }

    public ArrayList<Long> findAllMatchByPlayerId(long playerId){
        return db.findAllMatchByPlayerId(playerId);
    }
}
