package pl.osik.autyzm.sql;

import java.util.HashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class LekcjaDziecko extends AbstractDBTable {
    public static final String TABLE_NAME = "LekcjaDziecko";
    public static final String COLUMN_LEKCJA = "lekcja";
    public static final String COLUMN_DZIECKO = "dziecko";

    protected static final HashMap<String, String> colTypeMap = new HashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY");
        put(COLUMN_LEKCJA, "INTEGER");
        put(COLUMN_DZIECKO, "INTEGER");
    }};

    @Override
    protected String create() {
        return getCreateStart() + createArgumentQuery()
                + createForeignKey(COLUMN_LEKCJA, Lekcja.TABLE_NAME)
                + createForeignKey(COLUMN_DZIECKO, Dziecko.TABLE_NAME)
                + ")";
    }
}
