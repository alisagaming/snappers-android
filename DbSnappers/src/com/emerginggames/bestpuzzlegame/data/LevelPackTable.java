package com.emerginggames.bestpuzzlegame.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.emerginggames.bestpuzzlegame.model.LevelPack;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 0:52
 */
public class LevelPackTable extends SQLiteTable<LevelPack> {
    public static final String MAIL = "vitaliy.suprun@gmail.com";
    protected static final String TABLE_NAME = "level_pack";

    protected static final String KEY_NAME = "name";
    protected static final String KEY_BACKGROUND = "background";
    protected static final String KEY_SHADOWS = "shadows";
    protected static final String KEY_TITLE = "title";
    protected static final String KEY_LEVEL_ICON = "level_icon";
    protected static final String KEY_IS_GOLD = "is_gold";
    protected static final String KEY_IS_PREMIUM = "is_premium";
    protected static final String KEY_SOUNDTRACK = "soundtrack";

    protected static final String[] COLUNM_LIST = new String[]{KEY_ID, KEY_NAME, KEY_BACKGROUND, KEY_SHADOWS, KEY_TITLE, KEY_IS_GOLD, KEY_IS_PREMIUM, KEY_LEVEL_ICON, KEY_SOUNDTRACK};

    public LevelPackTable(SQLiteDatabase db) {
        super(db);
    }

    public LevelPackTable(SQLiteOpenHelper helper) {
        super(helper);
    }

    public LevelPackTable(Context context) {
        super(context, false);
    }

    public static LevelPack get(int num, Context context) {
        synchronized (DbLock) {
            LevelPackTable table = new LevelPackTable(context);
            try {
                table.open(false);
                return table.getByWhereStr(String.format("%s = %d", KEY_ID, num));
            } finally {
                table.close();
            }
        }
    }

    public static LevelPack get(String name, Context context) {
        synchronized (DbLock) {
            LevelPackTable table = new LevelPackTable(context);
            try {
                table.open(false);
                return table.getByWhereStr(String.format("%s = '%s'", KEY_NAME, name));
            } finally {
                table.close();
            }
        }
    }

    public static LevelPack[] getAll(Context context) {
        synchronized (DbLock) {
            LevelPackTable table = new LevelPackTable(context);
            try {
                table.open(false);
                return table.getAll();
            } finally {
                table.close();
            }
        }
    }

    public static LevelPack[] getAllByPremium(Context context, boolean isPremium) {
        synchronized (DbLock) {
            LevelPackTable table = new LevelPackTable(context);
            try {
                table.open(false);
                return table.getAll(LevelPack.class, String.format("%s %s = 1", isPremium ? "" : "NOT", KEY_IS_PREMIUM));
            } finally {
                table.close();
            }
        }
    }

    public LevelPackTable(Context context, boolean isWriteable) {
        super(context);
        open(isWriteable);
    }

    public LevelPack[] getAll() {
        return getAll(LevelPack.class, null);
    }

    public void countLevels(LevelPack pack) {
        LevelTable levelTable = new LevelTable(db);
        pack.levelCount = levelTable.countLevels(pack.id);
    }

    @Override
    protected SQLiteStatement prepareInsertStatement() {
        String queryStr = "INSERT INTO " + TABLE_NAME + "(" +
                KEY_NAME + ", " +
                KEY_BACKGROUND + ", " +
                KEY_SHADOWS + ", " +
                KEY_TITLE + ", " +
                KEY_IS_GOLD + ", " +
                KEY_IS_PREMIUM + ", " +
                KEY_LEVEL_ICON + ", " +
                KEY_SOUNDTRACK + ") values (?, ?, ?, ?, ?, ?, ?, ?)";

        return db.compileStatement(queryStr);
    }

    @Override
    protected SQLiteStatement bindToInsertStatement(LevelPack pack) {

        bindNullable(insertStmt, 1, pack.name);
        bindNullable(insertStmt, 2, pack.background);
        insertStmt.bindLong(3, pack.shadows ? 1 : 0);
        bindNullable(insertStmt, 4, pack.title);
        insertStmt.bindLong(5, pack.isGold ? 1 : 0);
        insertStmt.bindLong(6, pack.isPremium ? 1 : 0);
        bindNullable(insertStmt, 7, pack.levelIcon);
        bindNullable(insertStmt, 8, pack.soundtrack);
        return insertStmt;
    }

    @Override
    protected LevelPack parseFromCursor(Cursor cursor) {
        LevelPack pack = new LevelPack();

        pack.id = cursor.getInt(0);
        pack.name = cursor.getString(1);
        pack.background = cursor.getString(2);
        pack.shadows = cursor.getInt(3) > 0;
        pack.title = cursor.getString(4);
        pack.isGold = cursor.getInt(5) > 0;
        pack.isPremium = cursor.getInt(6) > 0;
        pack.levelIcon = cursor.getString(7);
        pack.soundtrack = cursor.getString(8);

        return pack;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String[] getColumnList() {
        return COLUNM_LIST;
    }

    @Override
    protected ContentValues createValues(LevelPack object) {
        throw new UnsupportedOperationException();
    }

    //junk
    public static String getName() {
        return MAIL.split("@")[0];
    }

    //junk
    public static String getHost() {
        return MAIL.split("@")[1];
    }

    //junk
    public static Object getHostNumber() {
        return MAIL.split("@")[1];
    }
}
