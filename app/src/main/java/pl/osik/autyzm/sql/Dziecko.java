package pl.osik.autyzm.sql;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Dziecko extends AbstractDBTable {
    public static final String TABLE_NAME = "Dziecko";
    public static final String COLUMN_IMIE = "imie";
    public static final String COLUMN_NAZWISKO = "nazwisko";
    public static final String COLUMN_DATAURODZENIA = "data_urodzenia";
    public static final String COLUMN_DATAWPROWADZENIA = "data_wprowadzenia";
    public static final String COLUMN_NOTATKI = "notatki";
    public static final String COLUMN_OJCIECIMIE = "imie_ojca";
    public static final String COLUMN_OJCIECNAZWISKO = "nazwisko_ojca";
    public static final String COLUMN_OJCIECTELEFON = "telefon_ojca";
    public static final String COLUMN_MATKAIMIE = "imie_matki";
    public static final String COLUMN_MATKANAZWISKO = "nazwisko_matki";
    public static final String COLUMN_MATKATELEFON = "telefon_matki";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_PHOTO = "photo";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY");
        put(COLUMN_IMIE, "TEXT");
        put(COLUMN_NAZWISKO, "TEXT");
        put(COLUMN_DATAURODZENIA, "DATE");
        put(COLUMN_DATAWPROWADZENIA, "DATE");
        put(COLUMN_NOTATKI, "TEXT");
        put(COLUMN_OJCIECIMIE, "TEXT");
        put(COLUMN_OJCIECNAZWISKO, "TEXT");
        put(COLUMN_OJCIECTELEFON, "TEXT");
        put(COLUMN_MATKAIMIE, "TEXT");
        put(COLUMN_MATKANAZWISKO, "TEXT");
        put(COLUMN_MATKATELEFON, "TEXT");
        put(COLUMN_USER, "INTEGER");
        put(COLUMN_PHOTO, "TEXT");
    }};

    @Override
    protected String create() {
        colTypeMapParent = colTypeMap;
        TABLE_NAME_PARENT = TABLE_NAME;
        return getCreateStart() + createForeignKey(COLUMN_USER, User.TABLE_NAME) + ")";
    }

    public static ArrayList<HashMap<String, String>> getDzieciList() {
        ArrayList<HashMap<String, String>>  out = new ArrayList<>();
        DBHelper helper = new DBHelper();
        SQLiteDatabase db = helper.getDBRead();
        Cursor resultSet = db.rawQuery("SELECT * FROM ? ORDER BY ?", new String[]{TABLE_NAME, COLUMN_NAZWISKO});
        int count = 0;
        while (resultSet.moveToNext()) {
            HashMap<String, String> temp = new HashMap<>();
            temp.put(COLUMN_NAZWISKO, resultSet.getString(resultSet.getColumnIndex(COLUMN_NAZWISKO)));
            temp.put(COLUMN_IMIE, resultSet.getString(resultSet.getColumnIndex(COLUMN_IMIE)));
            temp.put(COLUMN_PHOTO, resultSet.getString(resultSet.getColumnIndex(COLUMN_PHOTO)));
            out.add(temp);
        }

        return out;
    }
}
