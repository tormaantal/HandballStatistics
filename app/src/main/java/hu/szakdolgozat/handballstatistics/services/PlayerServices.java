package hu.szakdolgozat.handballstatistics.services;

import android.content.Context;

import java.util.ArrayList;

import hu.szakdolgozat.handballstatistics.database.DatabaseHelper;
import hu.szakdolgozat.handballstatistics.models.EventType;
import hu.szakdolgozat.handballstatistics.models.Player;

public class PlayerServices {

    private final DatabaseHelper db;

    public PlayerServices(Context context) {
        this.db = new DatabaseHelper(context);
    }

    @Override
    protected void finalize() throws Throwable {
        db.close();
        super.finalize();
    }

    public long addPlayer(long id, String name, String team) {
        return db.addPlayer(id, name, team);
    }

    public ArrayList<Player> findAllPlayer() {
        return db.findAllPlayer();
    }

    public int deletePlayer(long id) {
        return db.deletePlayer(id);
    }

    public Player findPlayerById(long id) {
        return db.findPlayerById(id);
    }

    public long findAllSaveByPlayer(long playerId) {
        return db.findAllSaveByPlayer(playerId);
    }

    public long findAllGoalByPlayer(long playerId) {
        return db.findAllGoalByPlayer(playerId);
    }

    public long findAllSaveByPlayerByType(long playerId, EventType type) {
        return db.findAllSaveByPlayerByType(playerId, type);
    }

    public long findAllGoalByPlayerByType(long playerId, EventType type) {
        return db.findAllGoalByPlayerByType(playerId, type);
    }

    public long findAllYellowCardByPlayer(long playerId) {
        return db.findAllYellowCardByPlayer(playerId);
    }

    public long findAllTwoMinutesByPlayer(long playerId) {
        return db.findAllTwoMinutesByPlayer(playerId);
    }

    public long findAllRedCardByPlayer(long playerId) {
        return db.findAllRedCardByPlayer(playerId);
    }

    public long findAllBlueCardByPlayer(long playerId) {
        return db.findAllBlueCardByPlayer(playerId);
    }
}
