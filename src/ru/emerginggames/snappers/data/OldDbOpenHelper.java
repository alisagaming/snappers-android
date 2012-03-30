package ru.emerginggames.snappers.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


public class OldDbOpenHelper  extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "snappers_data";


    public OldDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableLevels(db);
        createTableLevelPacks(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + LevelTable.TABLE_NAME);
        db.execSQL("drop table " + LevelPackTable.TABLE_NAME);

        createTableLevels(db);
        createTableLevelPacks(db);
    }

    private void createTableLevels(SQLiteDatabase db){
        String TABLE_CREATE = "CREATE TABLE " + LevelTable.TABLE_NAME + " (" +
                LevelTable.KEY_ID + " INTEGER PRIMARY KEY, " +
                LevelTable.KEY_NUMBER + " INTEGER, " +
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
            super.getWritableDatabase().close();
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

