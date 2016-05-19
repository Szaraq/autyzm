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
    public static final String COLUMN_DZIECKO = "dziecko";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_DATA, "DATE");
        put(COLUMN_PUNKTY, "INTEGER");
        put(COLUMN_PYTANIE, "INTEGER");
        put(COLUMN_DZIECKO, "INTEGER");
    }};

    @Override
    protected String create() {
        return getCreateStart() + createForeignKey(COLUMN_PYTANIE, Pytanie.TABLE_NAME) + ")";
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
