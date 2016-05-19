package pl.osik.autyzm.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
    public static final String COLUMN_FAVOURITE = "is_favourite";
    public static final String COLUMN_USER = "user";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_TYTUL, "TEXT");
        put(COLUMN_DATA_OSTATNIEGO_UZYCIA, "DATE");
        put(COLUMN_FAVOURITE, "BOOLEAN");
        put(COLUMN_USER, "INTEGER");
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
        String query = "SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME
                + checkUser()
                + " ORDER BY " + COLUMN_DATA_OSTATNIEGO_UZYCIA + " DESC"
                + " LIMIT " + liczbaLekcji;
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(User.getCurrentId())});
        out = fillTheList(cursor);
        cursor.close();
        helper.close();
        return out;
    }

    private static String checkUser() {
        /*return " JOIN " + LekcjaDziecko.TABLE_NAME + " ON " + tableAndColumn(LekcjaDziecko.TABLE_NAME, LekcjaDziecko.COLUMN_LEKCJA) + " = " + tableAndColumn(Lekcja.TABLE_NAME, Lekcja.COLUMN_ID)
                + createJoin(new Dziecko(), LekcjaDziecko.TABLE_NAME, LekcjaDziecko.COLUMN_DZIECKO)
                + createJoin(new User(), Dziecko.TABLE_NAME, Dziecko.COLUMN_USER)
                + " WHERE " + tableAndColumn(User.TABLE_NAME, COLUMN_ID) + " = ?";*/

        return " WHERE " + tableAndColumn(TABLE_NAME, COLUMN_USER) + " = ?";
    }

    public static ArrayList<LekcjaORM> getLekcjaList() {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        ArrayList<LekcjaORM> out = new ArrayList<>();
        String query = "SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME
                + checkUser()
                + " ORDER BY " + COLUMN_TYTUL;
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(User.getCurrentId())});
        out = fillTheList(cursor);
        cursor.close();
        helper.close();
        return out;
    }

    private static ArrayList<LekcjaORM> fillTheList(Cursor cursor) {
        ArrayList<LekcjaORM> out = new ArrayList<>();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_TYTUL));
            String lastUsed = cursor.getString(cursor.getColumnIndex(COLUMN_DATA_OSTATNIEGO_UZYCIA));
            boolean favourite = cursor.getInt(cursor.getColumnIndex(COLUMN_FAVOURITE)) == 1;
            LekcjaORM temp = new LekcjaORM(id, name, lastUsed, favourite);
            out.add(temp);
        }
        return out;
    }

    public static ArrayList<LekcjaORM> getFavourites() {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        ArrayList<LekcjaORM> out = new ArrayList<>();
        String query = "SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME
                + checkUser()
                + " AND " + COLUMN_FAVOURITE
                + " ORDER BY " + COLUMN_TYTUL;
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(User.getCurrentId())});
        out = fillTheList(cursor);
        cursor.close();
        helper.close();
        return out;
    }

    public static void setFavourite(int lekcjaId, boolean fav) {
        Lekcja l = new Lekcja();
        ContentValues data = new ContentValues();
        data.put(COLUMN_FAVOURITE, fav);
        l.edit(lekcjaId, data);
    }
}
