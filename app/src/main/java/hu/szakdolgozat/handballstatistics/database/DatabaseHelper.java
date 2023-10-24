package hu.szakdolgozat.handballstatistics.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

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
    private static final String EVENTS_TABLE_NAME = "events";
    private static final String EVENTS_COLUMN_ID = "eventId";
    private static final String EVENTS_COLUMN_MATCH_ID = "matchId";
    private static final String EVENTS_COLUMN_TIME = "time";
    private static final String EVENTS_COLUMN_TYPE = "type";
    private static final String EVENTS_COLUMN_RESULT = "result";
    private static final String MATCHES_TABLE_NAME = "matches";
    private static final String MATCHES_COLUMN_ID = "matchId";
    private static final String createEventsTable = "CREATE TABLE " + EVENTS_TABLE_NAME + " (" +
            EVENTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            EVENTS_COLUMN_MATCH_ID + " INTEGER," +
            EVENTS_COLUMN_TIME + " TEXT, " +
            EVENTS_COLUMN_TYPE + " TEXT, " +
            EVENTS_COLUMN_RESULT + " INTEGER, " +
            "FOREIGN KEY (" + EVENTS_COLUMN_MATCH_ID + ") REFERENCES " + MATCHES_TABLE_NAME + "(" + MATCHES_COLUMN_ID + "))";
    private static final String MATCHES_COLUMN_PLAYERID = "playerId";
    private static final String MATCHES_COLUMN_OPPONENT = "opponent";
    private static final String MATCHES_COLUMN_DATE = "date";
    private static final String createMatchesTable = "CREATE TABLE " + MATCHES_TABLE_NAME + " (" +
            MATCHES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MATCHES_COLUMN_PLAYERID + " INTEGER, " +
            MATCHES_COLUMN_DATE + " TEXT," +
            MATCHES_COLUMN_OPPONENT + " INTEGER, " +
            "FOREIGN KEY (" + MATCHES_COLUMN_PLAYERID + ") REFERENCES " + PLAYERS_TABLE_NAME + "(" + PLAYERS_COLUMN_ID + "));";
    private final Context context;


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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

    public void addPlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PLAYERS_COLUMN_ID, player.getId());
        cv.put(PLAYERS_COLUMN_NAME, player.getName());
        cv.put(PLAYERS_COLUMN_TEAM, player.getTeam());
        long result = db.insert(PLAYERS_TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Hiba történt!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Játékos létrehozva!", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<Player> selectAllPlayer() {
        ArrayList<Player> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + PLAYERS_TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(new Player(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return returnList;
    }

    public void deletePlayer(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(PLAYERS_TABLE_NAME, "playerId=?", new String[]{String.valueOf(id)});
        if (result == -1) {
            Toast.makeText(context, "Hiba a törlés során!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Törölve!", Toast.LENGTH_SHORT).show();
        }
    }

    public Player selectPlayerById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + PLAYERS_TABLE_NAME + " WHERE " + PLAYERS_COLUMN_ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        Player returnPlayer = null;
        if (cursor.moveToFirst()) {
            returnPlayer = new Player(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
        }
        cursor.close();
        return returnPlayer;
    }
}
