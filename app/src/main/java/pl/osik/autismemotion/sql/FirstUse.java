package pl.osik.autismemotion.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedHashMap;

/**
 * Created by m.osik2 on 2016-06-21.
 */
public class FirstUse extends AbstractDBTable {
    public static final String TABLE_NAME = "FirstUse";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_ACTIVITY = "activity";
    public static final String COLUMN_FIRST_USE = "first_use";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_USER, "INTEGER");
        put(COLUMN_ACTIVITY, "INTEGER");
        put(COLUMN_FIRST_USE, "BOOLEAN");
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

    public static void createRowsForNewUser(int idUser) {
        FirstUse fu = new FirstUse();
        ContentValues data = new ContentValues();
        data.put(COLUMN_USER, idUser);
        data.put(COLUMN_FIRST_USE, true);
        for (int i = 0; i < Activity.activities.size(); i++) {
            data.put(COLUMN_ACTIVITY, i+1);
            fu.insert(data);
        }
    }

    public static boolean isFirstUsed(Class mClass) {
        int idActivity = Activity.getIdForActivity(mClass);
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT " + COLUMN_FIRST_USE + " FROM " + TABLE_NAME
                + " WHERE " + COLUMN_USER + " = ?"
                + " AND " + COLUMN_ACTIVITY + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(User.getCurrentId()), String.valueOf(idActivity)});
        cursor.moveToFirst();
        boolean out = false;
        try {
            out = cursor.getInt(0) == 1;
        } catch (CursorIndexOutOfBoundsException exc) {             //kiedy zamykany jest drawerMenu a chcemy się wylogować
            //kontynuujemy, bo chcemy pozamykać DB i zwrócić false
        }
        cursor.close();
        helper.close();
        return out;
    }

    public static void setUsed(Class mClass) {
        setUsed(mClass, false);
    }

    public static void setUsed(Class mClass, boolean firstUse) {
        FirstUse fu = new FirstUse();
        ContentValues data = new ContentValues();
        data.put(COLUMN_FIRST_USE, firstUse);
        fu.edit(getFirstUsedIdByActivity(mClass), data);
    }

    public static int getFirstUsedIdByActivity(Class mClass) {
        int idActivity = Activity.getIdForActivity(mClass);
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_NAME
                + " WHERE " + COLUMN_USER + " = ?"
                + " AND " + COLUMN_ACTIVITY + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(User.getCurrentId()), String.valueOf(idActivity)});
        cursor.moveToFirst();
        int out = cursor.getInt(0);
        cursor.close();
        helper.close();
        return out;
    }

    public static void setAllUsed() {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "UPDATE " + TABLE_NAME + " SET " + COLUMN_FIRST_USE + " = ?" + " WHERE " + COLUMN_USER + " = ?";
        db.execSQL(query, new String[]{"0", String.valueOf(User.getCurrentId())});
        helper.close();
    }
}
