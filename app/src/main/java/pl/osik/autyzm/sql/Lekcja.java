package pl.osik.autyzm.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

/**
 * Created by m.osik2 on 2016-04-19.
 */
public class Lekcja extends AbstractDBTable {
    public static final String TABLE_NAME = "Lekcja";
    public static final String COLUMN_TYTUL = "tytul";

    protected static final HashMap<String, String> colTypeMap = new HashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY");
        put(COLUMN_TYTUL, "TEXT");
    }};

    @Override
    protected String create() {
        return getCreateStart() + ")";
    }

}
