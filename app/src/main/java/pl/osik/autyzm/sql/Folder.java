package pl.osik.autyzm.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Folder extends AbstractDBTable {
    public static final String TABLE_NAME = "Folder";
    public static final String COLUMN_NAZWA = "nazwa";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_NAZWA, "TEXT");
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

    public ArrayList<String> getPlikiInFolder(int idFolderu) {
        ArrayList<String> out = new ArrayList<>();
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + Plik.TABLE_NAME + " WHERE " + Plik.COLUMN_FOLDER + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(idFolderu) });
        while(cursor.moveToNext()) {
            out.add(cursor.getString(cursor.getColumnIndex(Plik.COLUMN_PATH)));
        }

        return out;
    }
}
