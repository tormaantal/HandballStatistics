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
    private static final String createPlayerTable = "CREATE TABLE " + PLAYERS_TABLE_NAME + " (" +
            PLAYERS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
            PLAYERS_COLUMN_NAME + " TEXT, " +
            PLAYERS_COLUMN_TEAM + " TEXT);";

    private static final String MATCHES_TABLE_NAME = "matches";
    private static final String MATCHES_COLUMN_ID = "matchId";
    private static final String MATCHES_COLUMN_PLAYER_ID = "playerId";
    private static final String MATCHES_COLUMN_OPPONENT = "opponent";
    private static final String MATCHES_COLUMN_DATE = "date";
    private static final String createMatchesTable = "CREATE TABLE " + MATCHES_TABLE_NAME + " (" +
            MATCHES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MATCHES_COLUMN_PLAYER_ID + " INTEGER, " +
            MATCHES_COLUMN_DATE + " TEXT," +
            MATCHES_COLUMN_OPPONENT + " TEXT, " +
            "FOREIGN KEY (" + MATCHES_COLUMN_PLAYER_ID + ") REFERENCES " + PLAYERS_TABLE_NAME + "(" + PLAYERS_COLUMN_ID + "));";

    private static final String EVENTS_TABLE_NAME = "events";
    private static final String EVENTS_COLUMN_ID = "eventId";
    private static final String EVENTS_COLUMN_MATCH_ID = "matchId";
    private static final String EVENTS_COLUMN_TIME = "time";
    private static final String EVENTS_COLUMN_TYPE = "type";
    private static final String EVENTS_COLUMN_RESULT = "result";
    private static final String createEventsTable = "CREATE TABLE " + EVENTS_TABLE_NAME + " (" +
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

    public long addPlayer(long playerId, String name, String team) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PLAYERS_COLUMN_ID, playerId);
        cv.put(PLAYERS_COLUMN_NAME, name);
        cv.put(PLAYERS_COLUMN_TEAM, team);
        return db.insert(PLAYERS_TABLE_NAME, null, cv);
    }

    public int deletePlayer(long playerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        findAllMatchByPlayerId(playerId).forEach(match ->
                deleteMatch(match.getMatchId())
        );
        return db.delete(PLAYERS_TABLE_NAME, "playerId=?", new String[]{String.valueOf(playerId)});
    }

    public Player findPlayerById(long playerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + PLAYERS_TABLE_NAME + " WHERE " + PLAYERS_COLUMN_ID + " = " + playerId;
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

    public int deleteEvent(long eventId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(EVENTS_TABLE_NAME, "eventId=?", new String[]{String.valueOf(eventId)});
    }


    public long addMatch(long playerId, String date, String opponent) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MATCHES_COLUMN_PLAYER_ID, playerId);
        cv.put(MATCHES_COLUMN_DATE, date);
        cv.put(MATCHES_COLUMN_OPPONENT, opponent);
        return db.insert(MATCHES_TABLE_NAME, null, cv);
    }


    public int deleteMatch(long matchId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(EVENTS_TABLE_NAME, "matchId=?", new String[]{String.valueOf(matchId)});
        return db.delete(MATCHES_TABLE_NAME, "matchId=?", new String[]{String.valueOf(matchId)});
    }

    public Match findMatchById(long matchId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + MATCHES_TABLE_NAME + " WHERE " + MATCHES_COLUMN_ID + " = " + matchId;
        Cursor cursor = db.rawQuery(query, null);
        Match match = null;
        if (cursor.moveToFirst()) {
            match = new Match(cursor.getLong(0), cursor.getLong(1),
                    cursor.getString(2), cursor.getString(3));
        }
        cursor.close();
        return match;
    }

    public ArrayList<Match> findAllMatch() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Match> returnList = new ArrayList<>();
        String query = "SELECT * FROM " + MATCHES_TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(new Match(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return returnList;
    }

    public ArrayList<Match> findAllMatchByPlayerId(long playerId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + MATCHES_TABLE_NAME + " WHERE " + MATCHES_COLUMN_PLAYER_ID + " = " + playerId + ";";
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Match> returnList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                returnList.add(new Match(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return returnList;
    }

    public long findAllSaveByPlayer(long playerId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + MATCHES_COLUMN_ID + " FROM " + MATCHES_TABLE_NAME +
                " WHERE " + MATCHES_COLUMN_PLAYER_ID + " = " + playerId + ";";
        Cursor cursorMatchId = db.rawQuery(query, null);
        long rv = 0;
        if (cursorMatchId.moveToFirst()) {
            do {
                query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                        " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + cursorMatchId.getLong(0) +
                        " AND " + EVENTS_COLUMN_RESULT + " =  1;";
                Cursor cursorEvent = db.rawQuery(query, null);
                if (cursorEvent.moveToFirst()) rv += cursorEvent.getLong(0);
                cursorEvent.close();
            } while (cursorMatchId.moveToNext());
        }
        cursorMatchId.close();
        return rv;
    }

    public long findAllGoalByPlayer(long playerId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + MATCHES_COLUMN_ID + " FROM " + MATCHES_TABLE_NAME +
                " WHERE " + MATCHES_COLUMN_PLAYER_ID + " = " + playerId + ";";
        Cursor cursorMatchId = db.rawQuery(query, null);
        long rv = 0;
        if (cursorMatchId.moveToFirst()) {
            do {
                query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                        " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + cursorMatchId.getLong(0) +
                        " AND " + EVENTS_COLUMN_RESULT + " =  0;";
                Cursor cursorEvent = db.rawQuery(query, null);
                if (cursorEvent.moveToFirst()) rv += cursorEvent.getLong(0);
                cursorEvent.close();
            } while (cursorMatchId.moveToNext());
        }
        cursorMatchId.close();
        return rv;
    }

    public long findAllSaveByPlayerByType(long playerId, EventType type) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + MATCHES_COLUMN_ID + " FROM " + MATCHES_TABLE_NAME +
                " WHERE " + MATCHES_COLUMN_PLAYER_ID + " = " + playerId + ";";
        Cursor cursorMatchId = db.rawQuery(query, null);
        long rv = 0;
        if (cursorMatchId.moveToFirst()) {
            do {
                query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                        " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + cursorMatchId.getLong(0) +
                        " AND " + EVENTS_COLUMN_TYPE + " = '" + type +
                        "' AND " + EVENTS_COLUMN_RESULT + " =  1;";
                Cursor cursorEvent = db.rawQuery(query, null);
                if (cursorEvent.moveToFirst()) rv += cursorEvent.getLong(0);
                cursorEvent.close();
            } while (cursorMatchId.moveToNext());
        }
        cursorMatchId.close();
        return rv;
    }

    public long findAllGoalByPlayerByType(long playerId, EventType type) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + MATCHES_COLUMN_ID + " FROM " + MATCHES_TABLE_NAME +
                " WHERE " + MATCHES_COLUMN_PLAYER_ID + " = " + playerId + ";";
        Cursor cursorMatchId = db.rawQuery(query, null);
        long rv = 0;
        if (cursorMatchId.moveToFirst()) {
            do {
                query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                        " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + cursorMatchId.getLong(0) +
                        " AND " + EVENTS_COLUMN_TYPE + " = '" + type +
                        "' AND " + EVENTS_COLUMN_RESULT + " =  0;";
                Cursor cursorEvent = db.rawQuery(query, null);
                if (cursorEvent.moveToFirst()) rv += cursorEvent.getLong(0);
                cursorEvent.close();
            } while (cursorMatchId.moveToNext());
        }
        cursorMatchId.close();
        return rv;
    }

    public long findAllYellowCardByPlayer(long playerId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + MATCHES_COLUMN_ID + " FROM " + MATCHES_TABLE_NAME +
                " WHERE " + MATCHES_COLUMN_PLAYER_ID + " = " + playerId + ";";
        Cursor cursorMatchId = db.rawQuery(query, null);
        long rv = 0;
        if (cursorMatchId.moveToFirst()) {
            do {
                query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                        " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + cursorMatchId.getLong(0) +
                        " AND " + EVENTS_COLUMN_RESULT + " =  2;";
                Cursor cursorEvent = db.rawQuery(query, null);
                if (cursorEvent.moveToFirst()) rv += cursorEvent.getLong(0);
                cursorEvent.close();
            } while (cursorMatchId.moveToNext());
        }
        cursorMatchId.close();
        return rv;
    }

    public long findAllTwoMinutesByPlayer(long playerId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + MATCHES_COLUMN_ID + " FROM " + MATCHES_TABLE_NAME +
                " WHERE " + MATCHES_COLUMN_PLAYER_ID + " = " + playerId + ";";
        Cursor cursorMatchId = db.rawQuery(query, null);
        long rv = 0;
        if (cursorMatchId.moveToFirst()) {
            do {
                query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                        " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + cursorMatchId.getLong(0) +
                        " AND " + EVENTS_COLUMN_RESULT + " =  3;";
                Cursor cursorEvent = db.rawQuery(query, null);
                if (cursorEvent.moveToFirst()) rv += cursorEvent.getLong(0);
                cursorEvent.close();
            } while (cursorMatchId.moveToNext());
        }
        cursorMatchId.close();
        return rv;
    }

    public long findAllRedCardByPlayer(long playerId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + MATCHES_COLUMN_ID + " FROM " + MATCHES_TABLE_NAME +
                " WHERE " + MATCHES_COLUMN_PLAYER_ID + " = " + playerId + ";";
        Cursor cursorMatchId = db.rawQuery(query, null);
        long rv = 0;
        if (cursorMatchId.moveToFirst()) {
            do {
                query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                        " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + cursorMatchId.getLong(0) +
                        " AND " + EVENTS_COLUMN_RESULT + " =  4;";
                Cursor cursorEvent = db.rawQuery(query, null);
                if (cursorEvent.moveToFirst()) rv += cursorEvent.getLong(0);
                cursorEvent.close();
            } while (cursorMatchId.moveToNext());
        }
        cursorMatchId.close();
        return rv;
    }

    public long findAllBlueCardByPlayer(long playerId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + MATCHES_COLUMN_ID + " FROM " + MATCHES_TABLE_NAME +
                " WHERE " + MATCHES_COLUMN_PLAYER_ID + " = " + playerId + ";";
        Cursor cursorMatchId = db.rawQuery(query, null);
        long rv = 0;
        if (cursorMatchId.moveToFirst()) {
            do {
                query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                        " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + cursorMatchId.getLong(0) +
                        " AND " + EVENTS_COLUMN_RESULT + " =  5;";
                Cursor cursorEvent = db.rawQuery(query, null);
                if (cursorEvent.moveToFirst()) rv += cursorEvent.getLong(0);
                cursorEvent.close();
            } while (cursorMatchId.moveToNext());
        }
        cursorMatchId.close();
        return rv;
    }

    public long findAllSaveByMatch(long matchId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + matchId +
                " AND " + EVENTS_COLUMN_RESULT + " =  1;";
        Cursor cursor = db.rawQuery(query, null);
        long rv = 0;
        if (cursor.moveToFirst()) rv = cursor.getLong(0);
        cursor.close();
        return rv;
    }

    public long findAllGoalByMatch(long matchId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + matchId +
                " AND " + EVENTS_COLUMN_RESULT + " =  0;";
        Cursor cursor = db.rawQuery(query, null);
        long rv = 0;
        if (cursor.moveToFirst()) rv = cursor.getLong(0);
        cursor.close();
        return rv;
    }

    public long findAllSaveByMatchByType(long matchId, EventType type) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + matchId +
                " AND " + EVENTS_COLUMN_TYPE + " = '" + type +
                "' AND " + EVENTS_COLUMN_RESULT + " =  1;";
        Cursor cursor = db.rawQuery(query, null);
        long rv = 0;
        if (cursor.moveToFirst()) rv = cursor.getLong(0);
        cursor.close();
        return rv;
    }

    public long findAllGoalByMatchByType(long matchId, EventType type) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + matchId +
                " AND " + EVENTS_COLUMN_TYPE + " = '" + type +
                "' AND " + EVENTS_COLUMN_RESULT + " =  0;";
        Cursor cursor = db.rawQuery(query, null);
        long rv = 0;
        if (cursor.moveToFirst()) rv = cursor.getLong(0);
        cursor.close();
        return rv;
    }

    public long findAllYellowCardByMatch(long matchId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + matchId +
                " AND " + EVENTS_COLUMN_RESULT + " =  2;";
        Cursor cursor = db.rawQuery(query, null);
        long rv = 0;
        if (cursor.moveToFirst()) rv = cursor.getLong(0);
        cursor.close();
        return rv;
    }

    public long findAllTwoMinutesByMatch(long matchId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + matchId +
                " AND " + EVENTS_COLUMN_RESULT + " =  3;";
        Cursor cursor = db.rawQuery(query, null);
        long rv = 0;
        if (cursor.moveToFirst()) rv = cursor.getLong(0);
        cursor.close();
        return rv;
    }

    public long findAllRedCardByMatch(long matchId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + matchId +
                " AND " + EVENTS_COLUMN_RESULT + " = 4;";
        Cursor cursor = db.rawQuery(query, null);
        long rv = 0;
        if (cursor.moveToFirst()) rv = cursor.getLong(0);
        cursor.close();
        return rv;
    }

    public long findAllBlueCardByMatch(long matchId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + EVENTS_TABLE_NAME +
                " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + matchId +
                " AND " + EVENTS_COLUMN_RESULT + " =  5;";
        Cursor cursor = db.rawQuery(query, null);
        long rv = 0;
        if (cursor.moveToFirst()) rv = cursor.getLong(0);
        cursor.close();
        return rv;
    }

    public ArrayList<Event> findAllEventByMatchId(long matchId) {
        ArrayList<Event> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + EVENTS_TABLE_NAME +
                " WHERE " + EVENTS_COLUMN_MATCH_ID + " = " + matchId;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(new Event(cursor.getLong(0), cursor.getLong(1),
                        cursor.getString(2), stringToType(cursor.getString(3)), cursor.getInt(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return returnList;
    }

    private EventType stringToType(String type) {
        switch (type) {
            case "PIVOT":
                return EventType.PIVOT;
            case "BREAKIN":
                return EventType.BREAKIN;
            case "CENTERBACK":
                return EventType.CENTERBACK;
            case "FASTBREAK":
                return EventType.FASTBREAK;
            case "LEFTBACK":
                return EventType.LEFTBACK;
            case "LEFTWING":
                return EventType.LEFTWING;
            case "RIGHTBACK":
                return EventType.RIGHTBACK;
            case "RIGHTWING":
                return EventType.RIGHTWING;
            case "SEVENMETERS":
                return EventType.SEVENMETERS;
            case "YELLOWCARD":
                return EventType.YELLOWCARD;
            case "TWOMINUTES":
                return EventType.TWOMINUTES;
            case "REDCARD":
                return EventType.REDCARD;
            case "BLUECARD":
                return EventType.BLUECARD;
            default:
                return null;
        }
    }
}
