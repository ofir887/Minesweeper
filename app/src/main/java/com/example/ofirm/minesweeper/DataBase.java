package com.example.ofirm.minesweeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by OfirMonis on 29/12/2015.
 */
public class DataBase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "scores.db";

    // Contacts table name
    private static final String TABLE_SCORES = "scores";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_NAME = "name";
    private static final String KEY_TIME = "time";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SCORES_TABLE = "CREATE TABLE " + TABLE_SCORES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_LEVEL + " INTEGER," + KEY_TIME + " INTEGER," + KEY_LATITUDE
                + " DOUBLE," + KEY_LONGITUDE + " DOUBLE" + ")";
        db.execSQL(CREATE_SCORES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);

        // Create tables again
        onCreate(db);
    }

    public void addScore(Score score) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, score.getName());
        values.put(KEY_LEVEL, score.getLevel()); // Contact Name
        values.put(KEY_LATITUDE,score.getLatitude());
        values.put(KEY_LONGITUDE,score.getLogitude());
        values.put(KEY_TIME, score.getTime());
       // values.put(KEY_TIME, score.getTime()); // Contact Phone Number

        // Inserting Row
        db.insert(TABLE_SCORES, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<Score> getAllScoresByLevel(int level) {
        ArrayList<Score> ScoreList = new ArrayList<Score>();
        // Select All Query
        String selectQuery;
        if (level == 4) {
            selectQuery = "SELECT  * FROM " + TABLE_SCORES + " ORDER BY level";
        }
        else {
            selectQuery = "SELECT  * FROM " + TABLE_SCORES + " WHERE level=" + level
                    + " ORDER BY time";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Score score = new Score();
                score.setId(Integer.parseInt(cursor.getString(0)));
                score.setName(cursor.getString(1));
                score.setLevel(Integer.parseInt(cursor.getString(2)));
                score.setTime(Integer.parseInt(cursor.getString(3)));
                score.setLatitude(Double.parseDouble(cursor.getString(4)));
                score.setLogitude(Double.parseDouble(cursor.getString(5)));


                // Adding contact to list
                ScoreList.add(score);
            } while (cursor.moveToNext());
        }

        // return contact list
        return ScoreList;
    }
    public void deleteScore(Score score) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCORES, KEY_ID + " = ?",
                new String[] { String.valueOf(score.getId()) });
        db.close();
    }
    public void deleteAllDatabase(ArrayList<Score> scores){
        for (int i=0; i < scores.size(); i++){
            deleteScore(scores.get(i));
        }
    }
}
