package pl.osik.autyzm.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import pl.osik.autyzm.helpers.orm.ModulORM;
import pl.osik.autyzm.helpers.orm.PytanieORM;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Pytanie extends AbstractDBTable {
    public static final String TABLE_NAME = "Pytanie";
    public static final String COLUMN_TRESC = "tresc";
    public static final String COLUMN_MODUL = "modul";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_TRESC, "TEXT");
        put(COLUMN_MODUL, "INTEGER");
    }};

    @Override
    protected String create() {
        return getCreateStart() + createForeignKey(COLUMN_MODUL, Modul.TABLE_NAME) + ")";
    }

    @Override
    protected LinkedHashMap<String, String> getMap() {
        return colTypeMap;
    }
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public static ArrayList<PytanieORM> getPytaniaForModul(int idModul) {
        ArrayList<PytanieORM> out = new ArrayList<>();
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME
                + createJoin(new Modul(), TABLE_NAME, COLUMN_MODUL)
                + " WHERE " + tableAndColumn(Modul.TABLE_NAME, Modul.COLUMN_ID) + " = ?"
                + " ORDER BY " + tableAndColumn(TABLE_NAME, COLUMN_ID);
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(idModul) });
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String tresc = cursor.getString(cursor.getColumnIndex(COLUMN_TRESC));
            int modul = cursor.getInt(cursor.getColumnIndex(COLUMN_MODUL));
            PytanieORM pytanie = new PytanieORM(id, tresc, modul);
            out.add(pytanie);
        }
        helper.close();
        cursor.close();

        return out;
    }
}
