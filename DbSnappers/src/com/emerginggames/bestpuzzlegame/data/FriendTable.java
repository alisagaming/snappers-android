package com.emerginggames.bestpuzzlegame.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import com.emerginggames.bestpuzzlegame.model.FacebookFriend;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.06.12
 * Time: 4:18
 */
public class FriendTable extends SQLiteTable<FacebookFriend> {
    protected static final String TABLE_NAME = "facebook_friends";

    protected static final String KEY_NAME = "name";
    protected static final String KEY_FB_ID = "fb_id";
    protected static final String KEY_LAST_GIFT_SENT = "last_gift_sent";
    protected static final String KEY_XP = "xp";
    protected static final String[] COLUMN_LIST = new String[]{KEY_ID, KEY_NAME, KEY_FB_ID, KEY_LAST_GIFT_SENT, KEY_XP};


    public FriendTable(SQLiteDatabase db) {
        super(db);
    }

    public FriendTable(SQLiteOpenHelper helper) {
        super(helper);
    }

    public FriendTable(Context context) {
        super(context);
    }

    public FriendTable(Context context, boolean isWriteable) {
        super(context, isWriteable);
    }

    @Override
    protected SQLiteStatement bindToInsertStatement(FacebookFriend friend) {
        bindNullable(insertStmt, 1, friend.first_name);
        insertStmt.bindLong(2, friend.facebook_id);
        insertStmt.bindLong(3, friend.lastSendGift);
        insertStmt.bindLong(4, friend.installed ? friend.xp_count : -1);
        return insertStmt;
    }

    @Override
    protected SQLiteStatement prepareInsertStatement() {
        String queryStr = "INSERT INTO " + TABLE_NAME + "(" +
                KEY_NAME + ", " +
                KEY_FB_ID + ", " +
                KEY_LAST_GIFT_SENT + ", " +
                KEY_XP + ") values (?, ?, ?, ?)";

        return db.compileStatement(queryStr);
    }

    @Override
    protected FacebookFriend parseFromCursor(Cursor cursor) {
        FacebookFriend friend = new FacebookFriend();
        friend.id = cursor.getInt(0);
        friend.first_name = cursor.getString(1);
        friend.facebook_id = cursor.getLong(2);
        friend.lastSendGift = cursor.getLong(3);
        friend.xp_count = cursor.getInt(4);
        friend.installed = friend.xp_count != -1;
        return friend;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String[] getColumnList() {
        return COLUMN_LIST;
    }

    @Override
    protected ContentValues createValues(FacebookFriend friend) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, friend.first_name);
        contentValues.put(KEY_FB_ID, friend.facebook_id);
        contentValues.put(KEY_LAST_GIFT_SENT, friend.lastSendGift);
        contentValues.put(KEY_XP, friend.installed ? friend.xp_count : -1);
        return contentValues;
    }

    protected void syncWithDb(FacebookFriend[] friends) {
        FacebookFriend[] friendsInDb = getAll(FacebookFriend.class, null);

        if (friendsInDb != null && friendsInDb.length != 0) {
            for (FacebookFriend friend : friends) {
                for (int i = 0; i < friendsInDb.length; i++) {
                    if (friendsInDb[i] == null)
                        continue;
                    if (friendsInDb[i].facebook_id == friend.facebook_id) {
                        friend.id = friendsInDb[i].id;
                        friend.lastSendGift = friendsInDb[i].lastSendGift;
                        if (!friend.first_name.equals(friendsInDb[i].first_name))
                            update(friend);
                        friendsInDb[i] = null;

                        break;
                    }
                }
            }

            for (FacebookFriend friend : friendsInDb) {
                if (friend != null)
                    delete(friend);
            }
        }

        for (FacebookFriend friend : friends) {
            if (friend.id == 0)
                insert(friend);
        }
    }

    public static void syncWithDb(Context context, FacebookFriend[] friends) {
        synchronized (DbLock) {
            FriendTable tbl = new FriendTable(context, true);
            try {
                tbl.syncWithDb(friends);
            } finally {
                tbl.close();
            }
        }
    }


    public FacebookFriend getByFbId(long fbId) {
        return getByWhereStr(KEY_FB_ID + " = " + Long.toString(fbId));
    }

    public static FacebookFriend getByFbId(Context context, long fbId) {
        synchronized (DbLock) {
            FriendTable tbl = new FriendTable(context, false);
            try {
                return tbl.getByFbId(fbId);
            } finally {
                tbl.close();
            }
        }
    }

    public static boolean update(Context context, FacebookFriend friend) {
        synchronized (DbLock) {
            FriendTable tbl = new FriendTable(context, false);
            try {
                return tbl.update(friend);
            } finally {
                tbl.close();
            }
        }
    }

    public static FacebookFriend[] getFriends(Context context, long[] facebookIds) {
        synchronized (DbLock) {
            FriendTable tbl = new FriendTable(context, false);
            String temp = Arrays.toString(facebookIds);
            String ids = temp.substring(1, temp.length() - 1);
            try {
                return tbl.getAll(FacebookFriend.class, String.format("%s in (%s)", KEY_FB_ID, ids));
            } finally {
                tbl.close();
            }
        }
    }

    public static void clear(Context context) {
        synchronized (DbLock) {
            FriendTable tbl = new FriendTable(context, false);
            try {
                tbl.db.delete(TABLE_NAME, "1=1", null);
            } finally {
                tbl.close();
            }
        }
    }

    public static FacebookFriend[] getAll(Context context) {
        synchronized (DbLock) {
            FriendTable tbl = new FriendTable(context, false);
            try {
                return tbl.getAll(FacebookFriend.class, null);
            } finally {
                tbl.close();
            }
        }
    }

    public static FacebookFriend[] getUnregistered(Context context) {
        synchronized (DbLock) {
            FriendTable tbl = new FriendTable(context, false);
            try {
                return tbl.getAll(FacebookFriend.class, String.format("%s = %d", KEY_XP, -1));
            } finally {
                tbl.close();
            }
        }
    }
}
