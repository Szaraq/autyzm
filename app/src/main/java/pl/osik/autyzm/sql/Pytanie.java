package pl.osik.autyzm.sql;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Pytanie extends AbstractDBTable {
    public static final String TABLE_NAME = "Pytanie";
    public static final String COLUMN_TRESC = "tresc";
    public static final String COLUMN_MODUL = "modul";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_TRESC, "TEXT");
        put(COLUMN_MODUL, "INTEGER");
    }};

    @Override
    protected String create() {
        return getCreateStart() + createForeignKey(COLUMN_MODUL, Modul.TABLE_NAME) + ")";
    }

    @Override
    protected LinkedHashMap<String, String> getMap() {
        return colTypeMap;
    }
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
