package pl.osik.autyzm.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by m.osik2 on 2016-04-19.
 */
public class Lekcja implements TableInterface {
    public static final String TABLE_NAME = "Lekcja";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYTUL = "tytul";

    @Override
    public void create() {

    }

}
