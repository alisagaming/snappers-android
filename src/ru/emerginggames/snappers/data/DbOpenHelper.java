package ru.emerginggames.snappers.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 1:00
 */
public class DbOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "app_data";


    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableLevels(db);
        createTableLevelPacks(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTableLevels(SQLiteDatabase db){
        String TABLE_CREATE = "CREATE TABLE " + LevelTable.TABLE_NAME + " (" +
                LevelTable.KEY_ID + " INTEGER PRIMARY KEY, " +
                LevelTable.KEY_LEVEL_PACK_ID + " INTEGER, " +
                LevelTable.KEY_COMPLEXITY + " INTEGER, " +
                LevelTable.KEY_ZAPPERS + " TEXT, " +
                LevelTable.KEY_SOLUTIONS + " TEXT, " +
                LevelTable.KEY_TAPS_COUNT + " INTEGER " +
                " );";
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        try {
            return super.getReadableDatabase();
        } catch (SQLiteException e){
            super.getWritableDatabase();
            return super.getReadableDatabase();
        }
    }

    private void createTableLevelPacks(SQLiteDatabase db){
        String TABLE_CREATE = "CREATE TABLE " + LevelPackTable.TABLE_NAME + " (" +
                LevelPackTable.KEY_ID + " INTEGER PRIMARY KEY, " +
                LevelPackTable.KEY_BACKGROUND + " TEXT, " +
                LevelPackTable.KEY_SHADOWS + " INTEGER, " +
                LevelPackTable.KEY_TITLE + " TEXT, " +
                LevelPackTable.KEY_IS_GOLD + " INTEGER, " +
                LevelPackTable.KEY_IS_UNLOCKED + " INTEGER, " +
                LevelPackTable.KEY_UNLOCKED_LEVELS + " INTEGER " +
                " );";

        db.execSQL(TABLE_CREATE);
    }


}
