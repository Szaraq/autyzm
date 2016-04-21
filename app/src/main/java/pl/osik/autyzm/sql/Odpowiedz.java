package pl.osik.autyzm.sql;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Odpowiedz extends AbstractDBTable {
    public static final String TABLE_NAME = "Odpowiedz";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_PUNKTY = "punkty";
    public static final String COLUMN_PYTANIE = "pytanie";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY");
        put(COLUMN_DATA, "DATE");
        put(COLUMN_PUNKTY, "INTEGER");
        put(COLUMN_PYTANIE, "INTEGER");
    }};

    @Override
    protected String create() {
        colTypeMapParent = colTypeMap;
        TABLE_NAME_PARENT = TABLE_NAME;
        return getCreateStart() + createForeignKey(COLUMN_PYTANIE, Pytanie.TABLE_NAME) + ")";
    }
}
