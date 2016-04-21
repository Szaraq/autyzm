package pl.osik.autyzm.sql;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by m.osik2 on 2016-04-21.
 */
public class LoadTestData {
    static DBHelper helper = new DBHelper();
    static SQLiteDatabase db = helper.getDBRead();
    private static boolean added = false;

    public static void load() {
        if(!added) {
            loadUser();
            loadDziecko();
            added = true;
        }
    }

    private static void loadUser() {
        db.execSQL("DELETE FROM " + User.TABLE_NAME);
        db.execSQL("INSERT INTO " + User.TABLE_NAME + " VALUES (1, 'admin', 'pass');");

    }

    private static void loadDziecko() {
        db.execSQL("DELETE FROM " + Dziecko.TABLE_NAME);
        db.execSQL("INSERT INTO " + Dziecko.TABLE_NAME + " VALUES(1, 'Jan', 'Kowalski', '2007-01-01 10:00:00', '2007-01-01 10:00:00', 'notsy', 'Janusz', 'Kowalski', '500111222', 'Janina', 'Kowalska', '500222333', '1', 'null')");
        db.execSQL("INSERT INTO " + Dziecko.TABLE_NAME + " VALUES(2, 'Jan', 'Mirecki', '2007-01-01 10:00:00', '2007-01-01 10:00:00', 'notsy', 'Janusz', 'Kowalski', '500111222', 'Janina', 'Kowalska', '500222333', '1', 'null')");
    }
}
