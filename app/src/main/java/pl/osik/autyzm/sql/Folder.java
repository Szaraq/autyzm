package pl.osik.autyzm.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import pl.osik.autyzm.helpers.MySortedMap;
import pl.osik.autyzm.helpers.orm.FolderORM;
import pl.osik.autyzm.helpers.orm.PlikORM;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Folder extends AbstractDBTable {
    public static final String TABLE_NAME = "Folder";
    public static final String COLUMN_NAZWA = "nazwa";
    public static final String COLUMN_FOLDER = "folder";        //jeżeli folder jest w Root, to = ROOT_ID
    public static final String COLUMN_USER = "user";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_NAZWA, "TEXT");
        put(COLUMN_FOLDER, "INTEGER");
        put(COLUMN_USER, "INTEGER");
    }};

    public static final String ROOT_NAME = "ROOT";
    public static final int NO_PARENT_FOLDER = -1;
    public static int ROOT_ID;

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

    public static ArrayList<FolderORM> getFolderyInFolder(int idFolderu) {
        ArrayList<FolderORM> out = new ArrayList<>();
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + Folder.TABLE_NAME + " WHERE " + Folder.COLUMN_FOLDER + " = ?";        //nie potrzeba order by, bo już jest Comparator
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(idFolderu) });
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(Folder.COLUMN_ID));
            String nazwa = cursor.getString(cursor.getColumnIndex(Folder.COLUMN_NAZWA));
            int folderFK = cursor.getInt(cursor.getColumnIndex(Folder.COLUMN_FOLDER));
            int user = cursor.getInt(cursor.getColumnIndex(Folder.COLUMN_USER));
            FolderORM folder = new FolderORM(id, nazwa, folderFK, user);
            out.add(folder);
        }
        helper.close();
        cursor.close();

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
        cursor.close();
        helper.close();

        return out;
    }

    public static void insertRoot(int idUser) {
        Folder f = new Folder();
        ContentValues data = new ContentValues();
        data.put(Folder.COLUMN_NAZWA, ROOT_NAME);
        data.put(Folder.COLUMN_USER, idUser);
        data.put(Folder.COLUMN_FOLDER, NO_PARENT_FOLDER);
        f.insert(data);
    }

    public static int setRoot() {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USER + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(User.getCurrentId()) });
        cursor.moveToFirst();
        ROOT_ID = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        helper.close();
        cursor.close();
        return ROOT_ID;
    }

    @Override
    public boolean delete(int id) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        db.delete(getTableName(), COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        /* Usuwanie plików */
        ArrayList<PlikORM> plikList = Plik.getPlikiInFolder(id, false);
        Plik p = new Plik();
        for (PlikORM plik : plikList) {
            p.delete(plik.getId());
        }

        helper.close();
        return true;
    }
}
