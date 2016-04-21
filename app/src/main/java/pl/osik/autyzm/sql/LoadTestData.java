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
            //loadDziecko();
            added = true;
        }
    }

    private static void loadUser() {
        String query = "INSERT INTO " + User.TABLE_NAME + " VALUES (1, 'admin', 'pass');";
        /*for(String s : db.rawQuery("SELECT * FROM User", null).getColumnNames())
            Log.d("tag", s);*/
        db.execSQL("INSERT INTO " + User.TABLE_NAME + " VALUES ('pass', 1, 'admin');");

    }

    private static void loadDziecko() {
        db.execSQL("INSERT INTO " + Dziecko.TABLE_NAME + " VALUES(1, 'Jan', 'Kowalski', '2007-01-01 10:00:00', '2007-01-01 10:00:00', 'notsy', 'Janusz', 'Kowalski', '500111222', 'Janina', 'Kowalska', '500222333', '1')");
    }
}
