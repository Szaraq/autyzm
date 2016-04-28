package pl.osik.autyzm.sql;

import android.content.ContentValues;
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
    private static int currentId;

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
    public boolean insert(ContentValues data) {
        data.put(COLUMN_PASS, AppHelper.hash((String) data.get(COLUMN_PASS)));
        db.insert(getTableName(), null, data);
        return true;
    }
}
