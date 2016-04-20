package pl.osik.autyzm.sql;

import java.util.HashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class User extends AbstractDBTable {
    public static final String TABLE_NAME = "User";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_PASS = "password";

    protected static final HashMap<String, String> colTypeMap = new HashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY");
        put(COLUMN_LOGIN, "TEXT");
        put(COLUMN_PASS, "TEXT");
    }};

    @Override
    protected String create() {
        return getCreateStart() + createArgumentQuery() + ")";
    }
}
