package pl.osik.autyzm.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import pl.osik.autyzm.helpers.AppHelper;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class User extends AbstractDBTable {
    public static final String TABLE_NAME = "User";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_PASS = "password";
    private static final int NO_USER = -1;
    private static int currentId = NO_USER;

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_LOGIN, "TEXT");
        put(COLUMN_PASS, "TEXT");
    }};

    public static int getCurrentId() {
        return currentId;
    }

    public static void setCurrentId(int currentId) {
        User.currentId = currentId;
    }

    @Override
    protected String create() {
        return getCreateStart() + ")";
    }

    @Override
    protected LinkedHashMap<String, String> getMap() {
        return colTypeMap;
    }
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public boolean insert(Map<String, Object> map) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        ContentValues data = new ContentValues();
        data.put(COLUMN_LOGIN, (String) map.get(COLUMN_LOGIN));
        data.put(COLUMN_PASS, AppHelper.hash((String) map.get(COLUMN_PASS)));
        db.insert(getTableName(), null, data);
        helper.close();
        return true;
    }

    @Override
    public boolean insert(ContentValues data) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        data.put(COLUMN_PASS, AppHelper.hash((String) data.get(COLUMN_PASS)));
        db.insert(getTableName(), null, data);
        helper.close();
        return true;
    }

    public static boolean authenticate(String user, String pass) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String hash = AppHelper.hash(pass);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN + " = ? AND " + COLUMN_PASS + " = ?", new String[] { user, hash });

        /* DEBUG */
        /*Cursor cursor2 = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN + " = ?", new String[] { user });
        cursor2.moveToNext();
        Log.d("DB", cursor2.getString(cursor2.getColumnIndex(COLUMN_PASS)) + " != " + hash);*/
        /* END */

        if(cursor.getCount() == 0) return false;
        cursor.moveToNext();
        currentId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        cursor.close();
        helper.close();
        return true;
    }

    public static void logout() {
        currentId = NO_USER;
    }

    public static boolean isFirstLogin() {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, new String[] { });
        int count = cursor.getCount();
        cursor.close();
        helper.close();
        if(count == 0) return true;
        return false;
    }

    public static String getCurrentUser() {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[] { String.valueOf(getCurrentId()) });
        cursor.moveToNext();
        String out = cursor.getString(cursor.getColumnIndex(COLUMN_LOGIN));
        cursor.close();
        helper.close();
        return out;
    }
}
