package ru.emerginggames.snappers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import ru.emerginggames.snappers.model.Level;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 1:37
 */
public abstract class SQLiteTable<T> {

    protected static final String KEY_ID = "_id";

    protected Context context;
    protected SQLiteDatabase db;
    protected SQLiteStatement insertStmt;

    public void open(boolean isWriteable){
        DbOpenHelper openHelper = new DbOpenHelper(this.context);

        if (isWriteable)
        {
            db = openHelper.getWritableDatabase();
        }
        else
            db = openHelper.getReadableDatabase();
    }

    public void openForInsertion(boolean isWriteable){
        open(isWriteable);
        if (isWriteable)
            insertStmt = prepareInsertStatement();
    }

    public long insert(T object){
        return bindToInsertStatement(object).executeInsert();
    }

    public T load(int id){
        Cursor mCursor = db.query(true, getTableName(), getColumnList(), KEY_ID + "=" + id, null, null, null, null, null);
        if (mCursor == null)
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
    
    protected T[] getAll(Class<T> clazz){
        Cursor mCursor = db.query(true, getTableName(), getColumnList(), null, null, null, null, KEY_ID + " asc" , null);
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
