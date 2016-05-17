package pl.osik.autyzm.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import pl.osik.autyzm.helpers.MySortedMap;
import pl.osik.autyzm.helpers.orm.ModulORM;

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

    public static ArrayList<ModulORM> getModulyForLekcja(int lekcjaId) {
        ArrayList<ModulORM> out = new ArrayList<>();
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME
                + createJoin(new Lekcja(), TABLE_NAME, COLUMN_LEKCJA)
                + " WHERE " + tableAndColumn(Lekcja.TABLE_NAME, COLUMN_ID) + " = ?"
                + " ORDER BY " + tableAndColumn(TABLE_NAME, COLUMN_NAZWA);
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(lekcjaId) });
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String nazwa = cursor.getString(cursor.getColumnIndex(COLUMN_NAZWA));
            int plik = cursor.getInt(cursor.getColumnIndex(COLUMN_FILM));
            int lekcja = cursor.getInt(cursor.getColumnIndex(COLUMN_LEKCJA));
            ModulORM modul = new ModulORM(id, nazwa, plik, lekcja);
            out.add(modul);
        }
        helper.close();
        cursor.close();

        return out;
    }
}
