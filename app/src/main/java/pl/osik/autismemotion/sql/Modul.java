package pl.osik.autismemotion.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import pl.osik.autismemotion.helpers.orm.ModulORM;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Modul extends AbstractDBTable {
    public static final String TABLE_NAME = "Modul";
    public static final String COLUMN_NAZWA = "nazwa";
    public static final String COLUMN_FILM = "film";
    public static final String COLUMN_LEKCJA = "lekcja";
    public static final String COLUMN_NUMER = "numer";          //który z kolei jest ten moduł w lekcjach
    public static final String COLUMN_GHOST = "ghost";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_NAZWA, "TEXT");
        put(COLUMN_FILM, "INTEGER");
        put(COLUMN_LEKCJA, "INTEGER");
        put(COLUMN_NUMER, "INTEGER");
        put(COLUMN_GHOST, "BOOLEAN");
    }};

    public static final int LAST = 999;

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

    public static ArrayList<ModulORM> getModulyForLekcja(int lekcjaId, boolean ghostFilter) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME
                + createJoin(new Lekcja(), TABLE_NAME, COLUMN_LEKCJA)
                + " WHERE " + tableAndColumn(Lekcja.TABLE_NAME, Lekcja.COLUMN_ID) + " = ?"
                + queryForGhost(ghostFilter, true)
                + " ORDER BY " + tableAndColumn(TABLE_NAME, COLUMN_NUMER);
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(lekcjaId)});
        ArrayList<ModulORM> out = fillTheList(cursor);
        helper.close();
        cursor.close();

        return out;
    }

    public static ModulORM getModulById(int id, boolean ghostFilter) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + COLUMN_ID + " = ?"
                + queryForGhost(ghostFilter, true);
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        ModulORM out = fillTheList(cursor).get(0);
        helper.close();
        cursor.close();

        return out;
    }

    /**
     * Nie tylko ghostuje/unghostuje wybrany moduł, ale też zmienia kolejność modułów tak, żeby ghost był ostatni.
     * @param id
     * @param set
     */
    public static void setGhost(int id, boolean set) {
        ModulORM thisModul = getModulById(id, false);
        Modul m = new Modul();

        ArrayList<ModulORM> allModuls = getModulyForLekcja(thisModul.getLekcja(), true);
        for(int i = thisModul.getNumer(); i < allModuls.size(); i++) {
            ModulORM iModul = allModuls.get(i);
            if(iModul.getId() == thisModul.getId()) continue;

            ContentValues otherData = new ContentValues();
            otherData.put(COLUMN_NUMER, i);
            m.edit(iModul.getId(), otherData);
        }

        ContentValues data = new ContentValues();
        data.put(COLUMN_GHOST, set);
        if(set)
            data.put(COLUMN_NUMER, LAST);
        else
            data.put(COLUMN_NUMER, allModuls.size() + 1);
        m.edit(id, data);
    }

    public static ArrayList<ModulORM> getModulyForPlik(int idPlik, boolean ghostFilter) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + COLUMN_FILM + " = ?"
                + queryForGhost(ghostFilter, true);
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idPlik)});
        ArrayList<ModulORM> out = fillTheList(cursor);
        helper.close();
        cursor.close();

        return out;
    }

    private static ArrayList<ModulORM> fillTheList(Cursor cursor) {
        ArrayList<ModulORM> out = new ArrayList<>(cursor.getCount());
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String nazwa = cursor.getString(cursor.getColumnIndex(COLUMN_NAZWA));
            int plik = cursor.getInt(cursor.getColumnIndex(COLUMN_FILM));
            int lekcja = cursor.getInt(cursor.getColumnIndex(COLUMN_LEKCJA));
            int numer = cursor.getInt(cursor.getColumnIndex(COLUMN_NUMER));
            boolean ghost = cursor.getInt(cursor.getColumnIndex(COLUMN_GHOST)) == 1;
            ModulORM modul = new ModulORM(id, nazwa, plik, lekcja, numer, ghost);
            out.add(modul);
        }
        return out;
    }

    private static String queryForGhost(boolean addQuery, boolean multipleQuery) {
        if(addQuery) return (multipleQuery ? " AND " : "") + tableAndColumn(TABLE_NAME, COLUMN_GHOST) + " = 0"; else return "";
    }
}
