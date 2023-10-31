package hu.szakdolgozat.handballstatistics.services;

import android.content.Context;

import java.util.ArrayList;

import hu.szakdolgozat.handballstatistics.database.DatabaseHelper;
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
}
