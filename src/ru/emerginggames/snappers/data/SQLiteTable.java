package ru.emerginggames.snappers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.lang.reflect.Array;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 1:37
 */
public abstract class SQLiteTable<T> {

    protected static final String KEY_ID = "_id";

    protected SQLiteOpenHelper helper;

    protected Context context;
    protected SQLiteDatabase db;
    protected SQLiteStatement insertStmt;

    protected SQLiteTable(SQLiteDatabase db) {
        this.db = db;
        if (!db.isReadOnly())
            insertStmt = prepareInsertStatement();
    }

    protected SQLiteTable(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    protected SQLiteTable(Context context) {
        this.context = context;
    }

    protected SQLiteTable(Context context, boolean isWriteable) {
        this.context = context;
        open(isWriteable);
    }

    public void open(boolean isWriteable){
        if (helper == null)
            helper = new DbCopyOpenHelper(context);

        if (isWriteable)
        {
            db = helper.getWritableDatabase();
            insertStmt = prepareInsertStatement();
        }
        else
            db = helper.getReadableDatabase();
    }



    public void close(){
        if (db != null)
            db.close();
        db = null;
    }

    public long insert(T object){
        return bindToInsertStatement(object).executeInsert();
    }

    public T load(int id){
        return  getByWhereStr(KEY_ID + "=" + id);
    }
    
    protected T getByWhereStr(String where){
        Cursor mCursor = db.query(true, getTableName(), getColumnList(), where, null, null, null, null, null);
        if (mCursor == null)
            return null;

        if (mCursor.getCount() < 1)
            return null;
        mCursor.moveToFirst();
        T result = parseFromCursor(mCursor);
        mCursor.close();
        return result;
    }

    public long save(T object, int id){
        if (!isExist(id))
            return insert(object);

        update(object, id);
        return 0;
    }
    
    public int count(String where){
        //TODO: do counting
        Cursor mCursor = db.query(true, getTableName(), new String[] { KEY_ID}, where, null, null, null, null, null);
        if (mCursor == null)
            return 0;
        int result = mCursor.getCount();
        mCursor.close();
        return result;

    }

    public boolean isExist(int id){
        Cursor mCursor = db.query(true, getTableName(), new String[] { KEY_ID}, KEY_ID + "=" + id, null, null, null, null, null);
        if (mCursor == null)
            return false;
        boolean result = mCursor.getCount() > 0;
        mCursor.close();
        return result;
    }

    public boolean update(T object, int id){
        ContentValues values = createValues(object);
        return db.update(getTableName(), values, KEY_ID + "=" + id, null) > 0;
    }
    
    protected T[] getAll(Class<T> clazz, String where){
        Cursor mCursor = db.query(true, getTableName(), getColumnList(), where, null, null, null, KEY_ID + " asc" , null);
        if (mCursor == null)
            return null;

        return getAllFromCursor(mCursor, clazz);
    }

    @SuppressWarnings({"unchecked"})
    protected T[] getAllFromCursor(Cursor cursor, Class<T> clazz){
        cursor.moveToFirst();

        T[] objects = (T[]) Array.newInstance(clazz, cursor.getCount());

        for(int i=0; i<cursor.getCount(); i++){
            objects[i] = parseFromCursor(cursor);
            cursor.moveToNext();
        }
        cursor.close();

        return objects;
    }

    protected abstract SQLiteStatement prepareInsertStatement();

    protected abstract SQLiteStatement bindToInsertStatement(T object);

    protected abstract T parseFromCursor(Cursor cursor);
    
    protected abstract String getTableName();

    protected abstract String[] getColumnList();

    protected abstract ContentValues createValues(T object);


    protected static void bindNullable(SQLiteStatement stmt, int n, String str){
        if (str == null)
            stmt.bindNull(n);
        else
            stmt.bindString(n, str);
    }
}
