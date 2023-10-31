package hu.szakdolgozat.handballstatistics.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import hu.szakdolgozat.handballstatistics.models.Event;
import hu.szakdolgozat.handballstatistics.models.EventType;
import hu.szakdolgozat.handballstatistics.models.Match;
import hu.szakdolgozat.handballstatistics.models.Player;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "HandballStatistics.db";
    private static final int DATABASE_VERSION = 1;
    private static final String PLAYERS_TABLE_NAME = "players";
    private static final String PLAYERS_COLUMN_ID = "playerId";
    private static final String PLAYERS_COLUMN_NAME = "name";
    private static final String PLAYERS_COLUMN_TEAM = "team";
    private static final String createPlayerTable = "CREATE TABLE IF NOT EXISTS " + PLAYERS_TABLE_NAME + " (" +
            PLAYERS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
            PLAYERS_COLUMN_NAME + " TEXT, " +
            PLAYERS_COLUMN_TEAM + " TEXT);";

    private static final String MATCHES_TABLE_NAME = "matches";
    private static final String MATCHES_COLUMN_ID = "matchId";
    private static final String MATCHES_COLUMN_PLAYERID = "playerId";
    private static final String MATCHES_COLUMN_OPPONENT = "opponent";
    private static final String MATCHES_COLUMN_DATE = "date";
    private static final String createMatchesTable = "CREATE TABLE IF NOT EXISTS " + MATCHES_TABLE_NAME + " (" +
            MATCHES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MATCHES_COLUMN_PLAYERID + " INTEGER, " +
            MATCHES_COLUMN_DATE + " TEXT," +
            MATCHES_COLUMN_OPPONENT + " INTEGER, " +
            "FOREIGN KEY (" + MATCHES_COLUMN_PLAYERID + ") REFERENCES " + PLAYERS_TABLE_NAME + "(" + PLAYERS_COLUMN_ID + "));";


    private static final String EVENTS_TABLE_NAME = "events";
    private static final String EVENTS_COLUMN_ID = "eventId";
    private static final String EVENTS_COLUMN_MATCH_ID = "matchId";
    private static final String EVENTS_COLUMN_TIME = "time";
    private static final String EVENTS_COLUMN_TYPE = "type";
    private static final String EVENTS_COLUMN_RESULT = "result";
    private static final String createEventsTable = "CREATE TABLE IF NOT EXISTS " + EVENTS_TABLE_NAME + " (" +
            EVENTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            EVENTS_COLUMN_MATCH_ID + " INTEGER," +
            EVENTS_COLUMN_TIME + " TEXT, " +
            EVENTS_COLUMN_TYPE + " TEXT, " +
            EVENTS_COLUMN_RESULT + " INTEGER, " +
            "FOREIGN KEY (" + EVENTS_COLUMN_MATCH_ID + ") REFERENCES " + MATCHES_TABLE_NAME + "(" + MATCHES_COLUMN_ID + "))";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createPlayerTable);
        db.execSQL(createMatchesTable);
        db.execSQL(createEventsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + PLAYERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MATCHES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE_NAME);
        onCreate(db);
    }

    public long addPlayer(long id, String name, String team) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PLAYERS_COLUMN_ID, id);
        cv.put(PLAYERS_COLUMN_NAME, name);
        cv.put(PLAYERS_COLUMN_TEAM, team);
        return db.insert(PLAYERS_TABLE_NAME, null, cv);
    }

    public int deletePlayer(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        findAllMatchByPlayerId(id).forEach(this::deleteMatch);
        return db.delete(PLAYERS_TABLE_NAME, "playerId=?", new String[]{String.valueOf(id)});
    }

    public Player findPlayerById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + PLAYERS_TABLE_NAME + " WHERE " + PLAYERS_COLUMN_ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        Player returnPlayer = null;
        if (cursor.moveToFirst()) {
            returnPlayer = new Player(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
        }
        cursor.close();
        return returnPlayer;
    }

    public ArrayList<Player> findAllPlayer() {
        ArrayList<Player> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + PLAYERS_TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(new Player(cursor.getLong(0), cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return returnList;
    }

    public long addEvent(long matchId, String time, EventType type, int result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(EVENTS_COLUMN_MATCH_ID, matchId);
        cv.put(EVENTS_COLUMN_TIME, time);
        cv.put(EVENTS_COLUMN_TYPE, String.valueOf(type));
        cv.put(EVENTS_COLUMN_RESULT, result);
        return db.insert(EVENTS_TABLE_NAME, null, cv);
    }

    public int deleteEvent(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(EVENTS_TABLE_NAME, "eventId=?", new String[]{String.valueOf(id)});
    }

    public Event findEventById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + EVENTS_TABLE_NAME + " WHERE " + EVENTS_COLUMN_ID + " = " + id + ";";
        Cursor cursor = db.rawQuery(query, null);
        Event event = null;
        if (cursor.moveToFirst()) {
            event = new Event(cursor.getLong(0), cursor.getLong(1), cursor.getString(2),
                    typeResolve(cursor.getString(3)), cursor.getInt(4));
        }
        cursor.close();
        return event;
    }

    public ArrayList<Event> findAllEventByMatchId(long matchId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Event> returnList = new ArrayList<>();
        String query = "SELECT * FROM " + EVENTS_TABLE_NAME + " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + matchId + ";";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(new Event(cursor.getLong(0), cursor.getLong(1), cursor.getString(2),
                        typeResolve(cursor.getString(3)), cursor.getInt(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return returnList;
    }

    public long addMatch(long playerId, String date, String opponenet) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MATCHES_COLUMN_PLAYERID, playerId);
        cv.put(MATCHES_COLUMN_DATE, date);
        cv.put(MATCHES_COLUMN_OPPONENT, opponenet);
        return db.insert(MATCHES_TABLE_NAME, null, cv);
    }


    public int deleteMatch(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(EVENTS_TABLE_NAME, "matchId=?", new String[]{String.valueOf(id)});
        return db.delete(MATCHES_TABLE_NAME, "matchId=?", new String[]{String.valueOf(id)});
    }

    public Match findMatchById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + MATCHES_TABLE_NAME + " WHERE " + MATCHES_COLUMN_ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        Match match = null;
        if (cursor.moveToFirst()) {
            match = new Match(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3));
        }
        cursor.close();
        return match;
    }

    public ArrayList<Match> findAllMatch(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Match> returnList = new ArrayList<>();
        String query = "SELECT * FROM " + MATCHES_TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()){
            do{
                returnList.add(new Match(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return returnList;
    }

    public ArrayList<Long> findAllMatchByPlayerId(long playerId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT matchId FROM " + MATCHES_TABLE_NAME + " WHERE " + MATCHES_COLUMN_PLAYERID + " = " + playerId;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Long> returnList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            returnList.add(cursor.getLong(0));
        }
        cursor.close();
        return returnList;
    }

    private EventType typeResolve(String type) {
        switch (type) {
            case "LEFTBACK":
                return EventType.LEFTBACK;
            case "CENTERBACK":
                return EventType.CENTERBACK;
            case "RIGHTBACK":
                return EventType.RIGHTBACK;
            case "RIGHTWING":
                return EventType.RIGHTWING;
            case "BREAKIN":
                return EventType.BREAKIN;
            case "PIVOT":
                return EventType.PIVOT;
            case "SEVENMETERS":
                return EventType.SEVENMETERS;
            case "FASTBREAK":
                return EventType.FASTBREAK;
            case "TWOMINUTES":
                return EventType.TWOMINUTES;
            case "YELLOWCARD":
                return EventType.YELLOWCARD;
            case "REDCARD":
                return EventType.REDCARD;
            case "BLUECARD":
                return EventType.BLUECARD;
            default:
                return EventType.LEFTWING;
        }
    }

}
