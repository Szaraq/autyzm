package pl.osik.autyzm.sql;

import java.util.LinkedHashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Modul extends AbstractDBTable {
    public static final String TABLE_NAME = "Modul";
    public static final String COLUMN_NAZWA = "nazwa";
    public static final String COLUMN_FILM = "film";
    public static final String COLUMN_LEKCJA = "lekcja";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_NAZWA, "TEXT");
        put(COLUMN_FILM, "INTEGER");
        put(COLUMN_LEKCJA, "INTEGER");
    }};

    @Override
    protected String create() {
        return getCreateStart()
                + createForeignKey(COLUMN_FILM, Plik.TABLE_NAME)
                + createForeignKey(COLUMN_LEKCJA, Lekcja.TABLE_NAME)
                + ")";
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
