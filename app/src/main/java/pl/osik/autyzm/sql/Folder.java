package pl.osik.autyzm.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import pl.osik.autyzm.helpers.MySortedMap;
import pl.osik.autyzm.helpers.orm.PlikORM;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Folder extends AbstractDBTable {
    public static final String TABLE_NAME = "Folder";
    public static final String COLUMN_NAZWA = "nazwa";
    public static final String COLUMN_FOLDER = "folder";        //jeżeli folder jest w Root, to = ROOT_ID

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_NAZWA, "TEXT");
        put(COLUMN_FOLDER, "INTEGER");
    }};

    public static final int ROOT_ID = -1;
    public static final String ROOT_NAME = "ROOT";

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

    public static ArrayList<PlikORM> getPlikiInFolder(int idFolderu) {
        ArrayList<PlikORM> out = new ArrayList<>();
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + Plik.TABLE_NAME + " WHERE " + Plik.COLUMN_FOLDER + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(idFolderu) });
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(Plik.COLUMN_ID));
            int folder  = cursor.getInt(cursor.getColumnIndex(Plik.COLUMN_FOLDER));
            String path = cursor.getString(cursor.getColumnIndex(Plik.COLUMN_PATH));
            PlikORM temp = new PlikORM(id, folder, path);
            out.add(temp);
        }
        Collections.sort(out);

        return out;
    }

    public static MySortedMap getFolderyInFolder(int idFolderu) {
        MySortedMap out = new MySortedMap();
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + Folder.TABLE_NAME + " WHERE " + Folder.COLUMN_FOLDER + " = ?";        //nie potrzeba order by, bo już jest Comparator
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(idFolderu) });
        while(cursor.moveToNext()) {
            out.put(cursor.getString(cursor.getColumnIndex(Folder.COLUMN_NAZWA)), cursor.getInt(cursor.getColumnIndex(Folder.COLUMN_ID)));
        }

        return out;
    }

    public static boolean isRoot(int idFolderu) {
        return idFolderu == ROOT_ID;
    }

    public static HashMap<String, Object> getParentFolder(int myId) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        HashMap<String, Object> out = new HashMap<>();
        String query = "SELECT me." + COLUMN_FOLDER + ", parent." + COLUMN_ID + ", parent." + COLUMN_NAZWA + " FROM " + Folder.TABLE_NAME + " me"
                + " LEFT JOIN " + Folder.TABLE_NAME + " parent ON me." + COLUMN_FOLDER + " = parent." + COLUMN_ID
                + " WHERE me." + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(myId) });
        cursor.moveToFirst();
        if(isRoot(cursor.getInt(cursor.getColumnIndex(COLUMN_FOLDER)))) {           //jeżeli parent jest Rootem (to nie występuje w DB i trzeba go ręcznie dodać do mapy)
            out.put(COLUMN_ID, ROOT_ID);
            out.put(COLUMN_NAZWA, ROOT_NAME);
        } else {
            out.put(COLUMN_ID, cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            out.put(COLUMN_NAZWA, cursor.getString(cursor.getColumnIndex(COLUMN_NAZWA)));
        }

        return out;
    }
}
