package pl.osik.autyzm.sql;

import java.util.HashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Pytanie extends AbstractDBTable {
    public static final String TABLE_NAME = "Pytanie";
    public static final String COLUMN_TRESC = "tresc";
    public static final String COLUMN_MODUL = "modul";

    protected static final HashMap<String, String> colTypeMap = new HashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER");
        put(COLUMN_TRESC, "TEXT");
        put(COLUMN_MODUL, "INTEGER");
    }};

    @Override
    protected String create() {
        return getCreateStart() + createArgumentQuery() + createForeignKey(COLUMN_MODUL, Modul.TABLE_NAME) + ")";
    }
}
