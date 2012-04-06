package ru.emerginggames.snappers.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


public class DbCreatorOpenHelper extends SQLiteOpenHelper {
    Context mContext;
    public static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "snappers_data";
    boolean creareDb = false;


    public DbCreatorOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableLevels(db);
        createTableLevelPacks(db);
        creareDb = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + LevelTable.TABLE_NAME);
        db.execSQL("drop table " + LevelPackTable.TABLE_NAME);

        createTableLevels(db);
        createTableLevelPacks(db);
        creareDb = true;
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
                LevelPackTable.KEY_NAME + " TEXT, " +
                LevelPackTable.KEY_BACKGROUND + " TEXT, " +
                LevelPackTable.KEY_SHADOWS + " INTEGER, " +
                LevelPackTable.KEY_TITLE + " TEXT, " +
                LevelPackTable.KEY_IS_GOLD + " INTEGER, " +
                LevelPackTable.KEY_IS_PREMIUM + " INTEGER " +
                " );";

        db.execSQL(TABLE_CREATE);
    }

    public void initializeDataBase() {

        SQLiteDatabase db = getWritableDatabase();

        if (creareDb){
            new LevelDbLoader(mContext, db).load();
        }
        db.close();
    }


}

