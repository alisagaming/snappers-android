package ru.emerginggames.snappers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.LevelPack;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 0:52
 */
public class LevelPackTable extends SQLiteTable<LevelPack>{
    protected static final String TABLE_NAME = "level_pack";

    protected static final String KEY_ID = "_id";
    protected static final String KEY_BACKGROUND = "background";
    protected static final String KEY_SHADOWS = "shadows";
    protected static final String KEY_TITLE = "title";
    protected static final String KEY_IS_GOLD = "is_gold";
    protected static final String KEY_IS_UNLOCKED = "is_unlocked";
    protected static final String KEY_UNLOCKED_LEVELS = "unlocked_levels";

    protected static final String[] COLUNM_LIST = new String[] { KEY_ID, KEY_BACKGROUND, KEY_SHADOWS, KEY_TITLE, KEY_IS_GOLD, KEY_IS_UNLOCKED, KEY_UNLOCKED_LEVELS};

    private Context context;
    private SQLiteDatabase db;

    public LevelPackTable(Context context) {
        this.context = context;
    }

    public LevelPackTable(Context context, boolean isWriteable) {
        this.context = context;
        open(isWriteable);
    }


    public LevelPack[] getAll(){
        return getAll(LevelPack.class);
    }

    public boolean setLevelSolved(Level level){
        ContentValues values = new ContentValues();
        values.put(KEY_UNLOCKED_LEVELS, level.number);
        return db.update(TABLE_NAME, values, KEY_ID + "=" + level.packNumber, null) > 0;
    }

    public boolean unlockLevelPack(int levelPackId){
        ContentValues values = new ContentValues();
        values.put(KEY_IS_UNLOCKED, 1);
        return db.update(TABLE_NAME, values, KEY_ID + "=" + levelPackId, null) > 0;
    }

    @Override
    public long insert(LevelPack object) {
        if (insertStmt == null)
            insertStmt = prepareInsertStatement();
        return super.insert(object);
    }

    @Override
    protected SQLiteStatement prepareInsertStatement() {
        String queryStr = "INSERT INTO " + TABLE_NAME + "(" +
                KEY_ID + ", " +
                KEY_BACKGROUND + ", " +
                KEY_SHADOWS + ", " +
                KEY_TITLE + ", " +
                KEY_IS_GOLD + ", " +
                KEY_IS_UNLOCKED + ", " +
                KEY_UNLOCKED_LEVELS + ") values (?, ?, ?, ?, ?, ?, ?)";

        return db.compileStatement(queryStr);
    }

    @Override
    protected SQLiteStatement bindToInsertStatement(LevelPack pack) {

        insertStmt.bindLong(1, pack.id);
        bindNullable(insertStmt, 2, pack.background);
        insertStmt.bindLong(3, pack.shadows? 1 : 0);
        bindNullable(insertStmt, 4, pack.title);
        insertStmt.bindLong(5, pack.isGold ? 1 : 0);
        insertStmt.bindLong(6, pack.isUnlocked ? 1 : 0);
        insertStmt.bindLong(7, pack.levelsUnlocked);
        return insertStmt;
    }

    @Override
    protected LevelPack parseFromCursor(Cursor cursor) {
        LevelPack pack = new LevelPack();

        pack.id = cursor.getInt(0);
        pack.background = cursor.getString(1);
        pack.shadows = cursor.getInt(2)>0;
        pack.title = cursor.getString(3);
        pack.isGold = cursor.getInt(4)>0;
        pack.isUnlocked = cursor.getInt(5)>0;
        pack.levelsUnlocked = cursor.getInt(6);

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
}
