package com.aathis.timestable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SivarajKumar on 01/03/2018.
 */

public class DBHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "AathisTT.db";
    private static final String TABLE_NAME = "players";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DATE_PLAYED = "date_played";
    private static final String COLUMN_TABLENO = "table_no";
    private static final String COLUMN_TIME_TAKEN = "time_taken";
    private static final String COLUMN_COMPLETION_SCALE = "completion_scale";

    private HashMap hp;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table "+TABLE_NAME+
                        "("+COLUMN_ID+" integer primary key autoincrement, " +
                        COLUMN_NAME+" text," +
                        COLUMN_DATE_PLAYED+" NUMERIC," +
                        COLUMN_TABLENO+" NUMERIC, " +
                        COLUMN_TIME_TAKEN+" NUMERIC," +
                        COLUMN_COMPLETION_SCALE+" NUMERIC)"
        );
        Log.d("DB CREATE:", "Created or opened the player table.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("TIMESTABLE", "Upgrading Database from " + oldVersion + " to " + newVersion + " which will destroy all Old Data");
        db.execSQL("DROP TABLE IF EXISTS players");
        onCreate(db);
    }

    public void addPlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, player.getName()); // Contact Name
        values.put(COLUMN_DATE_PLAYED, player.getDatesPlayed().getTime()); // Contact Phone Number
        values.put(COLUMN_TABLENO, player.getTable());
        values.put(COLUMN_TIME_TAKEN, player.getTimeTakenInMillis());
        values.put(COLUMN_COMPLETION_SCALE, player.getCompletionScale());
        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
        Log.d("DB INSERT:", "Added an entry to Player"+player.getName());
    }


    public ArrayList<Player> getPlayerByName(String name) {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Player> playerList = new ArrayList<Player>();

        Cursor cursor = db.query(TABLE_NAME, new String[] {COLUMN_ID, COLUMN_DATE_PLAYED,
                        COLUMN_TABLENO, COLUMN_TIME_TAKEN, COLUMN_COMPLETION_SCALE }, COLUMN_NAME + "=?",
                new String[] { String.valueOf(name) }, null, null, COLUMN_DATE_PLAYED +" DESC" +","+COLUMN_TABLENO +" DESC", null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Player player = new Player();
                player.setId(Integer.parseInt(cursor.getString(0)));
                player.setDatesPlayed(new Date(cursor.getLong(1)));
                player.setTable(cursor.getInt(2));
                player.setTimeTakenInMillis(cursor.getLong(3));
                player.setCompletionScale(cursor.getInt(4));
                player.setName(name);

                playerList.add(player);
            } while (cursor.moveToNext());
        }
        Log.d("DB Fetch:", "Getting all player info for player"+ name);
        return playerList;
    }
    public List<Player> getAllPlayers() {
        List<Player> playerList = new ArrayList<Player>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Player player = new Player();
                player.setId(Integer.parseInt(cursor.getString(0)));
                player.setName(cursor.getString(1));
                player.setDatesPlayed(new Date(cursor.getLong(2)));
                player.setTable(cursor.getInt(3));
                player.setTimeTakenInMillis(cursor.getLong(4));
                player.setCompletionScale(cursor.getInt(5));

                playerList.add(player);
            } while (cursor.moveToNext());
        }
         return playerList;
    }

    public void deletePlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_NAME + " = ?",
                new String[] { String.valueOf(player.getName()) });
        db.close();
    }

    /*
    public int updatePlayer(Player contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }
*/
    public static Date loadDate(Cursor cursor, int index) {
        if (cursor.isNull(index)) {
            return null;
        }
        return new Date(cursor.getLong(index));
    }

    public static Long persistDate(Date date) {
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

}
