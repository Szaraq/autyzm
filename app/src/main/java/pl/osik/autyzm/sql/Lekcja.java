package pl.osik.autyzm.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import pl.osik.autyzm.helpers.orm.LekcjaORM;

/**
 * Created by m.osik2 on 2016-04-19.
 */
public class Lekcja extends AbstractDBTable {
    public static final String TABLE_NAME = "Lekcja";
    public static final String COLUMN_TYTUL = "tytul";
    public static final String COLUMN_DATA_OSTATNIEGO_UZYCIA = "data_ostatniego_uzycia";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_TYTUL, "TEXT");
        put(COLUMN_DATA_OSTATNIEGO_UZYCIA, "DATE");
    }};

    @Override
    protected String create() {
        return getCreateStart() + ")";
    }

    @Override
    protected LinkedHashMap<String, String> getMap() {
        return colTypeMap;
    }
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public static ArrayList<LekcjaORM> getOstatnieLekcje(int liczbaLekcji) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        ArrayList<LekcjaORM> out = new ArrayList<>();
        //TODO dodać Where lekcja jest użytkownika
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DATA_OSTATNIEGO_UZYCIA + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        for (int i = 0; i < liczbaLekcji; i++) {
            if(cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_TYTUL));
                String lastUsed = cursor.getString(cursor.getColumnIndex(COLUMN_DATA_OSTATNIEGO_UZYCIA));
                LekcjaORM temp = new LekcjaORM(id, name, lastUsed);
                out.add(temp);
            } else {
                break;
            }
        }

        return out;
    }

}
