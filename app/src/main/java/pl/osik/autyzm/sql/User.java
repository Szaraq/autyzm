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
    public static final String COLUMN_IMIE = "imie";
    public static final String COLUMN_NAZWISKO = "nazwisko";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_PASS = "password";
    public static final String COLUMN_PHOTO = "photo";
    private static final int NO_USER = -1;
    private static int currentId = NO_USER;

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_IMIE, "TEXT");
        put(COLUMN_NAZWISKO, "TEXT");
        put(COLUMN_LOGIN, "TEXT");
        put(COLUMN_PASS, "TEXT");
        put(COLUMN_PHOTO, "TEXT");
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
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if(entry.getValue() instanceof String) {
                data.put(entry.getKey(), (String) entry.getValue());
            } else if(entry.getValue() instanceof Integer) {
                data.put(entry.getKey(), (Integer) entry.getValue());
            } else if(entry.getValue() == null) {
                //nie rób nic
            } else {
                try {
                    throw new Exception("Niewłaściwy typ danych " + entry.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    helper.close();
                }
            }
        }
        data.put(COLUMN_LOGIN, (String) map.get(COLUMN_LOGIN));
        data.put(COLUMN_PASS, AppHelper.hash((String) map.get(COLUMN_PASS)));
        long id = db.insert(getTableName(), null, data);
        helper.close();
        FirstUse.createRowsForNewUser((int) id);
        return true;
    }

    @Override
    public long insert(ContentValues data) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        data.put(COLUMN_PASS, AppHelper.hash((String) data.get(COLUMN_PASS)));
        long out = db.insert(getTableName(), null, data);
        helper.close();
        Folder.insertRoot((int) out);
        FirstUse.createRowsForNewUser((int) out);
        return out;
    }

    @Override
    public boolean edit(int id, ContentValues data) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String newHaslo = (String) data.get(COLUMN_PASS);
        if(newHaslo != null) data.put(COLUMN_PASS, AppHelper.hash(newHaslo));
        db.update(getTableName(), data, COLUMN_ID + "= ?", new String[]{String.valueOf(id)});
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
        Folder.setRoot();
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

    public static String getCurrentName() {
        if(!isAuthenticated()) return null;
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[] { String.valueOf(getCurrentId()) });
        cursor.moveToNext();
        String out = cursor.getString(cursor.getColumnIndex(COLUMN_IMIE)) + " " + cursor.getString(cursor.getColumnIndex(COLUMN_NAZWISKO));
        cursor.close();
        helper.close();
        return out;
    }

    public static String getCurrentPhotoPath() {
        if(!isAuthenticated()) return null;
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[] { String.valueOf(getCurrentId()) });
        cursor.moveToNext();
        String out = cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO));
        cursor.close();
        helper.close();
        return out;
    }

    public static String getPhotoPathByLogin(String login) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN + " = ?", new String[] { login });
        if(cursor.getCount() == 0) return null;
        cursor.moveToNext();
        String out = cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO));
        cursor.close();
        helper.close();
        return out;
    }
    
    public static HashMap<String, String> getCurrentData() {
        if(!isAuthenticated()) return null;
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        HashMap<String, String> out = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(getCurrentId())});
        cursor.moveToFirst();
        for (Map.Entry<String, String> entry : colTypeMap.entrySet()) {
            out.put(entry.getKey(), cursor.getString(cursor.getColumnIndex(entry.getKey())));
        }
        cursor.close();
        helper.close();
        return out;
    }

    public static boolean isAuthenticated() {
        return getCurrentId() != NO_USER;
    }
}
