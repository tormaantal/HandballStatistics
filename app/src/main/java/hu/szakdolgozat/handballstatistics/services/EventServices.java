package hu.szakdolgozat.handballstatistics.services;

import android.content.Context;

import hu.szakdolgozat.handballstatistics.database.DatabaseHelper;
import hu.szakdolgozat.handballstatistics.models.EventType;

public class EventServices {
    private final DatabaseHelper db;

    public EventServices(Context context) {
        this.db = new DatabaseHelper(context);
    }

    public long addEvent(long matchId, String time, EventType type, int result) {
        return db.addEvent(matchId, time, type, result);
    }

    public int deleteEvent(long id) {
        return db.deleteEvent(id);
    }

}
