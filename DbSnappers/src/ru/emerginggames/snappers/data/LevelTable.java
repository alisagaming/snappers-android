package ru.emerginggames.snappers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.LevelPack;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 0:52
 */
public class LevelTable  extends SQLiteTable<Level>{
    private static final String MAIL = "babay.v@gmail.com";

    protected static final String TABLE_NAME = "level";
    protected static final String KEY_NUMBER = "number";
    protected static final String KEY_LEVEL_PACK_ID = "level_pack_id";
    protected static final String KEY_COMPLEXITY = "complexity";
    protected static final String KEY_ZAPPERS = "zappers";
    protected static final String KEY_SOLUTIONS = "solutions";
    protected static final String KEY_TAPS_COUNT = "taps_count";

    protected static final String[] COLUNM_LIST = new String[] { KEY_ID, KEY_LEVEL_PACK_ID, KEY_NUMBER, KEY_COMPLEXITY, KEY_ZAPPERS, KEY_SOLUTIONS, KEY_TAPS_COUNT};

    public LevelTable(SQLiteDatabase db) {
        super(db);
    }

    public LevelTable(SQLiteOpenHelper helper) {
        super(helper);
    }

    public LevelTable(Context context) {
        super(context);
    }

    public LevelTable(Context context, boolean isWriteable) {
        super(context, isWriteable);
    }

    public Level getNextLevel(Level level){
        if (level.pack == null)
            level.pack = LevelPackTable.get(level.packNumber, context);
        return getLevel(level.number+1, level.pack);
    }

    public static Level getLevel(Context context, int number, LevelPack pack){
        LevelTable table = new LevelTable(context, false);
        try {
            return table.getLevel(number, pack);
        }finally {
            table.close();
        }
    }

    public Level getLevel(int number, LevelPack pack){
        String where = String.format("%s = %d AND %s = %d", KEY_LEVEL_PACK_ID, pack.id, KEY_NUMBER, number);
        Level level = getByWhereStr(where);
        if (level != null)
            level.pack = pack;
        return level;
    }
    
    public static int countLevels(Context context, int levelPackId){
        LevelTable table = new LevelTable(context, false);
        try{
            return table.countLevels(levelPackId);
        } finally {
            table.close();
        }
    }
    
    public int countLevels(int packId){
        return count(String.format("%s = %d", KEY_LEVEL_PACK_ID, packId));
    }

    public static Level[] getLevels(Context context, int packId){
        LevelTable table = new LevelTable(context, false);
        try {
            return table.getAll(Level.class, String.format("%s = %d", KEY_LEVEL_PACK_ID, packId));
        }
        finally {
            table.close();
        }
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
                KEY_LEVEL_PACK_ID + ", " +
                KEY_NUMBER + ", " +
                KEY_COMPLEXITY + ", " +
                KEY_ZAPPERS + ", " +
                KEY_SOLUTIONS + ", " +
                KEY_TAPS_COUNT + ") values (?, ?, ?, ?, ?, ?)";

        return db.compileStatement(queryStr);
    }

    @Override
    protected SQLiteStatement bindToInsertStatement(Level level){
        String encSolution;
        try{
            encSolution = CryptHelperDES.encrypt(MAIL, level.solutions);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        insertStmt.bindLong(1, level.packNumber);
        insertStmt.bindLong(2, level.number);
        insertStmt.bindLong(3, level.complexity);
        bindNullable(insertStmt, 4, level.zappers);
        insertStmt.bindString(5, encSolution);
        insertStmt.bindLong(6, level.tapsCount);
        return insertStmt;
    }

    @Override
    public Level parseFromCursor(Cursor cursor){
        Level level = new Level();
        
        String solutions;
        try {
            solutions = CryptHelperDES.decrypt(MAIL, cursor.getString(5));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }

        level.id = cursor.getInt(0);
        level.packNumber = cursor.getInt(1);
        level.number = cursor.getInt(2);
        level.complexity = cursor.getInt(3);
        level.zappers = cursor.getString(4);
        level.solutions = solutions;
        level.tapsCount = cursor.getInt(6);

        return level;
    }

    @Override
    protected ContentValues createValues(Level object) {
        throw new UnsupportedOperationException();
    }

    public static String getMail(){
        return MAIL;
    }
}
