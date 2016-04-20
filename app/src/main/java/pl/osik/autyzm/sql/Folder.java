package pl.osik.autyzm.sql;

import java.util.HashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Folder extends AbstractDBTable {
    public static final String TABLE_NAME = "Folder";
    public static final String COLUMN_NAZWA = "nazwa";

    protected static final HashMap<String, String> colTypeMap = new HashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY");
        put(COLUMN_NAZWA, "TEXT");
    }};

    @Override
    protected String create() {
        return getCreateStart() + ")";
    }
}
