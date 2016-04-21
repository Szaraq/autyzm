package pl.osik.autyzm.sql;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Folder extends AbstractDBTable {
    public static final String TABLE_NAME = "Folder";
    public static final String COLUMN_NAZWA = "nazwa";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY");
        put(COLUMN_NAZWA, "TEXT");
    }};

    @Override
    protected String create() {
        colTypeMapParent = colTypeMap;
        TABLE_NAME_PARENT = TABLE_NAME;
        return getCreateStart() + ")";
    }
}
