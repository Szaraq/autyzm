package pl.osik.autyzm.sql;

import java.util.HashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Modul extends AbstractDBTable {
    public static final String TABLE_NAME = "Modul";
    public static final String COLUMN_NAZWA = "nazwa";
    public static final String COLUMN_FILM = "film";
    public static final String COLUMN_LEKCJA = "lekcja";

    protected static final HashMap<String, String> colTypeMap = new HashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER");
        put(COLUMN_NAZWA, "TEXT");
        put(COLUMN_FILM, "INTEGER");
        put(COLUMN_LEKCJA, "INTEGER");
    }};

    @Override
    protected String create() {
        return getCreateStart()
                + createForeignKey(COLUMN_FILM, Film.TABLE_NAME)
                + createForeignKey(COLUMN_LEKCJA, Lekcja.TABLE_NAME)
                + ")";
    }
}
