package pl.osik.autyzm.sql;

import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class User extends AbstractDBTable {
    public static final String TABLE_NAME = "User";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_PASS = "password";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY");
        put(COLUMN_LOGIN, "TEXT");
        put(COLUMN_PASS, "TEXT");
    }};

    @Override
    protected String create() {
        colTypeMapParent = colTypeMap;
        TABLE_NAME_PARENT = TABLE_NAME;
        Log.d("tag2", getCreateStart() + ")");
        return getCreateStart() + ")";
    }
}
