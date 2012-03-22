package ru.emerginggames.snappers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import ru.emerginggames.snappers.model.Level;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 0:52
 */
public class LevelTable  extends SQLiteTable<Level>{
    protected static final String TABLE_NAME = "level";
    
    protected static final String KEY_LEVEL_PACK_ID = "level_pack_id";
    protected static final String KEY_COMPLEXITY = "complexity";
    protected static final String KEY_ZAPPERS = "zappers";
    protected static final String KEY_SOLUTIONS = "solutions";
    protected static final String KEY_TAPS_COUNT = "taps_count";

    protected static final String[] COLUNM_LIST = new String[] { KEY_ID, KEY_LEVEL_PACK_ID, KEY_COMPLEXITY, KEY_ZAPPERS, KEY_SOLUTIONS, KEY_TAPS_COUNT};

    public LevelTable(Context context) {
        this.context = context;
        open(false);
    }

    public LevelTable(Context context, boolean isWriteable) {
        this.context = context;
        openForInsertion(isWriteable);
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
    protected SQLiteStatement prepareInsertStatement(){
        String queryStr = "INSERT INTO " + TABLE_NAME + "(" +
                KEY_ID + ", " +
                KEY_LEVEL_PACK_ID + ", " +
                KEY_COMPLEXITY + ", " +
                KEY_ZAPPERS + ", " +
                KEY_SOLUTIONS + ", " +
                KEY_TAPS_COUNT + ") values (?, ?, ?, ?, ?, ?)";

        return db.compileStatement(queryStr);
    }

    @Override
    protected SQLiteStatement bindToInsertStatement(Level level){
        insertStmt.bindLong(1, level.number);
        insertStmt.bindLong(2, level.packNumber);
        insertStmt.bindLong(3, level.complexity);
        bindNullable(insertStmt, 4, level.zappers);
        bindNullable(insertStmt, 5, level.solutions);
        insertStmt.bindLong(6, level.tapsCount);
        return insertStmt;
    }

    @Override
    public Level parseFromCursor(Cursor cursor){
        Level level = new Level();

        level.number = cursor.getInt(0);
        level.packNumber = cursor.getInt(1);
        level.complexity = cursor.getInt(2);
        level.zappers = cursor.getString(3);
        level.solutions = cursor.getString(4);
        level.tapsCount = cursor.getInt(5);

        return level;
    }

    @Override
    protected ContentValues createValues(Level object) {
        throw new UnsupportedOperationException();
    }
}
