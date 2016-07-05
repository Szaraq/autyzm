package pl.osik.autismemotion.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;

import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.orm.LekcjaORM;

/**
 * Created by m.osik2 on 2016-04-19.
 */
public class Lekcja extends AbstractDBTable {
    public static final String TABLE_NAME = "Lekcja";
    public static final String COLUMN_TYTUL = "tytul";
    public static final String COLUMN_DATA_OSTATNIEGO_UZYCIA = "data_ostatniego_uzycia";
    public static final String COLUMN_FAVOURITE = "is_favourite";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_GHOST = "ghost";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_TYTUL, "TEXT");
        put(COLUMN_DATA_OSTATNIEGO_UZYCIA, "DATE");
        put(COLUMN_FAVOURITE, "BOOLEAN");
        put(COLUMN_USER, "INTEGER");
        put(COLUMN_GHOST, "BOOLEAN");
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

    public static ArrayList<LekcjaORM> getOstatnieLekcje(int liczbaLekcji, boolean ghostFilter) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME
                + checkUser()
                + " AND " + tableAndColumn(TABLE_NAME, COLUMN_DATA_OSTATNIEGO_UZYCIA) + " <> ?"
                + queryForGhost(ghostFilter, true)
                + " ORDER BY " + COLUMN_DATA_OSTATNIEGO_UZYCIA + " DESC"
                + " LIMIT " + liczbaLekcji;
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(User.getCurrentId()), LekcjaORM.NEVER_USED_IN_DB});
        ArrayList<LekcjaORM> out = fillTheList(cursor);
        cursor.close();
        helper.close();
        return out;
    }

    private static String checkUser() {
        return " WHERE " + tableAndColumn(TABLE_NAME, COLUMN_USER) + " = ?";
    }

    public static ArrayList<LekcjaORM> getLekcjaList(boolean ghostFilter) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME
                + checkUser()
                + queryForGhost(ghostFilter, true)
                + " ORDER BY " + COLUMN_TYTUL;
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(User.getCurrentId())});
        ArrayList<LekcjaORM> out = fillTheList(cursor);
        cursor.close();
        helper.close();
        return out;
    }

    private static ArrayList<LekcjaORM> fillTheList(Cursor cursor) {
        ArrayList<LekcjaORM> out = new ArrayList<>(cursor.getCount());
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_TYTUL));
            String lastUsed = cursor.getString(cursor.getColumnIndex(COLUMN_DATA_OSTATNIEGO_UZYCIA));
            boolean favourite = cursor.getInt(cursor.getColumnIndex(COLUMN_FAVOURITE)) == 1;
            boolean ghost = cursor.getInt(cursor.getColumnIndex(COLUMN_GHOST)) == 1;
            LekcjaORM temp = new LekcjaORM(id, name, lastUsed, favourite, ghost);
            out.add(temp);
        }
        return out;
    }

    public static ArrayList<LekcjaORM> getFavourites(boolean ghostFilter) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME
                + checkUser()
                + queryForGhost(ghostFilter, true)
                + " AND " + COLUMN_FAVOURITE
                + " ORDER BY " + COLUMN_TYTUL;
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(User.getCurrentId())});
        ArrayList<LekcjaORM> out = fillTheList(cursor);
        cursor.close();
        helper.close();
        return out;
    }

    public static void setFavourite(int lekcjaId, boolean fav, ImageView icon) {
        setFavourite(lekcjaId, fav);
        changeFavouriteIcon(icon, fav);
    }

    public static void setFavourite(int lekcjaId, boolean fav) {
        Lekcja l = new Lekcja();
        ContentValues data = new ContentValues();
        data.put(COLUMN_FAVOURITE, fav);
        l.edit(lekcjaId, data);
    }

    public static void changeFavouriteIcon(ImageView icon, boolean newFavourite) {
        if(newFavourite)
            icon.setImageResource(R.drawable.ic_favourite_remove);
        else
            icon.setImageResource(R.drawable.ic_favourite_add);
    }

    public static void todayUsed(int lekcjaId) {
        Lekcja l = new Lekcja();
        ContentValues data = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
        String out = sdf.format(Calendar.getInstance().getTime());
        data.put(COLUMN_DATA_OSTATNIEGO_UZYCIA, out);
        l.edit(lekcjaId, data);
    }

    public static void setGhost(int id, boolean set) {
        Lekcja l = new Lekcja();
        ContentValues data = new ContentValues();
        data.put(COLUMN_GHOST, set);
        l.edit(id, data);
    }

    public static void cleanLekcje() {
        ArrayList<LekcjaORM> list = getLekcjaList(false);
        for (LekcjaORM lekcja : list) {
            int listSize = Modul.getModulyForLekcja(lekcja.getId(), true).size();
            if (listSize == 0 && !lekcja.isGhost()) setGhost(lekcja.getId(), true);
            if (listSize > 0 && lekcja.isGhost()) setGhost(lekcja.getId(), false);
        }
    }

    private static String queryForGhost(boolean addQuery, boolean multipleQuery) {
        if(addQuery) return (multipleQuery ? " AND " : "") + tableAndColumn(TABLE_NAME, COLUMN_GHOST) + " = 0"; else return "";
    }
}
