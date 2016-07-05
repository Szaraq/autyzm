package pl.osik.autismemotion.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        ArrayList<HashMap<String, Object>>  out = new ArrayList<>();
        String query = "SELECT " + TABLE_NAME + ".*" + " FROM " + TABLE_NAME
                + createJoin(new User(), TABLE_NAME, COLUMN_USER)
                + " WHERE " + tableAndColumn(User.TABLE_NAME, COLUMN_ID) + " = ?"
                + " ORDER BY " + COLUMN_NAZWISKO;
        Cursor resultSet = db.rawQuery(query, new String[] { String.valueOf(User.getCurrentId()) });
        int count = 0;
        while (resultSet.moveToNext()) {
            HashMap<String, Object> temp = new HashMap<>();
            temp.put(COLUMN_NAZWISKO, resultSet.getString(resultSet.getColumnIndex(COLUMN_NAZWISKO)));
            temp.put(COLUMN_IMIE, resultSet.getString(resultSet.getColumnIndex(COLUMN_IMIE)));
            temp.put(COLUMN_PHOTO, resultSet.getString(resultSet.getColumnIndex(COLUMN_PHOTO)));
            temp.put(COLUMN_ID, resultSet.getInt(resultSet.getColumnIndex(COLUMN_ID)));
            out.add(temp);
        }
        resultSet.close();
        helper.close();
        return out;
    }

    public static HashMap<String, String> getDzieckoById(int id) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();

        if(!checkDzieckoForCurrentUser(id)) return null;
        Dziecko d = new Dziecko();
        HashMap<String, String> out = new HashMap<>();
        Cursor resultSet = db.rawQuery("SELECT * FROM " + d.getTableName() + " WHERE id = ?", new String[] { String.valueOf(id) });
        resultSet.moveToNext();
        String[] cols = d.getColumns();
        for (String c: cols) {
            String input = resultSet.getString(resultSet.getColumnIndex(c));
            out.put(c, input);
        }
        resultSet.close();
        helper.close();
        return out;
    }

    public static String getImieINazwiskoByID(int id) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        if(!checkDzieckoForCurrentUser(id)) return null;
        Dziecko d = new Dziecko();
        String out;
        Cursor resultSet = db.rawQuery("SELECT " + COLUMN_NAZWISKO + ", " + COLUMN_IMIE + " FROM " + d.getTableName() + " WHERE id = ?", new String[]{String.valueOf(id)});
        resultSet.moveToNext();
        out = resultSet.getString(resultSet.getColumnIndex(COLUMN_IMIE)) + " " + resultSet.getString(resultSet.getColumnIndex(COLUMN_NAZWISKO));
        resultSet.close();
        helper.close();
        return out;
    }

    public static LinkedHashMap<String, Float> getStatistics(int idDziecka) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        if(!checkDzieckoForCurrentUser(idDziecka)) return null;
        LinkedHashMap<String, Float> out = new LinkedHashMap<>();
        String query = "SELECT " + Odpowiedz.COLUMN_DATA + ", AVG("+ Odpowiedz.COLUMN_PUNKTY + ") AS " + Odpowiedz.COLUMN_PUNKTY + " FROM " + Odpowiedz.TABLE_NAME
                + " WHERE " + Odpowiedz.COLUMN_DZIECKO + " = ?"
                + " GROUP BY " + Odpowiedz.COLUMN_DATA
                + " ORDER BY " + tableAndColumn(Odpowiedz.TABLE_NAME, Odpowiedz.COLUMN_DATA);
        Cursor resultSet = db.rawQuery(query, new String[] { String.valueOf(idDziecka) });
        while (resultSet.moveToNext()) {
            out.put(resultSet.getString(resultSet.getColumnIndex(Odpowiedz.COLUMN_DATA)),
                    resultSet.getFloat(resultSet.getColumnIndex(Odpowiedz.COLUMN_PUNKTY)));
        }
        resultSet.close();
        helper.close();
        return out;
    }

    public static boolean checkDzieckoForCurrentUser(int idDziecko) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + TABLE_NAME
                + createJoin(new User(), TABLE_NAME, COLUMN_USER)
                + " WHERE " + tableAndColumn(TABLE_NAME, COLUMN_ID) + " = ?"
                + " AND " + tableAndColumn(User.TABLE_NAME, User.COLUMN_ID) + " = ?";
        Cursor resultSet = db.rawQuery(query, new String[]{String.valueOf(idDziecko), String.valueOf(User.getCurrentId()) });

        if(resultSet.getCount() == 0) {
            Cursor resultSet2 = db.rawQuery("SELECT " + COLUMN_USER + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(idDziecko) });
            resultSet2.moveToNext();
            Log.d("checkDziecko", "Dziecko: " + idDziecko + " User: " + resultSet2.getInt(0));
            resultSet.close();
            resultSet2.close();
            return false;
        }
        resultSet.close();
        helper.close();
        return true;
    }

    public static void changePhoto(int id, String path) {
        Dziecko d = new Dziecko();
        ContentValues data = new ContentValues();
        data.put(COLUMN_PHOTO, path);
        d.edit(id, data);
    }
}
