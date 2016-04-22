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
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
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

    static DBHelper helper = new DBHelper();
    static SQLiteDatabase db = helper.getDBRead();

    @Override
    protected String create() {
        return getCreateStart() + createForeignKey(COLUMN_USER, User.TABLE_NAME) + ")";
    }

    @Override
    protected LinkedHashMap<String, String> getMap() {
        return colTypeMap;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public static ArrayList<HashMap<String, Object>> getDzieciList() {
        ArrayList<HashMap<String, Object>>  out = new ArrayList<>();
        Cursor resultSet = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_NAZWISKO, null);
        int count = 0;
        while (resultSet.moveToNext()) {
            HashMap<String, Object> temp = new HashMap<>();
            temp.put(COLUMN_NAZWISKO, resultSet.getString(resultSet.getColumnIndex(COLUMN_NAZWISKO)));
            temp.put(COLUMN_IMIE, resultSet.getString(resultSet.getColumnIndex(COLUMN_IMIE)));
            temp.put(COLUMN_PHOTO, resultSet.getString(resultSet.getColumnIndex(COLUMN_PHOTO)));
            temp.put(COLUMN_ID, resultSet.getInt(resultSet.getColumnIndex(COLUMN_ID)));
            out.add(temp);
        }
        return out;
    }

    public static HashMap<String, String> getDzieckoById(int id) {
        //TODO zmienić na <String, Object>

        Dziecko d = new Dziecko();
        HashMap<String, String> out = new HashMap<>();
        Cursor resultSet = db.rawQuery("SELECT * FROM " + d.getTableName() + " WHERE id = ?", new String[] { String.valueOf(id) });
        resultSet.moveToNext();
        String[] cols = d.getColumns();
        for (String c: cols) {
            String input = resultSet.getString(resultSet.getColumnIndex(c));
            out.put(c, input);
        }
        return out;
    }
}
