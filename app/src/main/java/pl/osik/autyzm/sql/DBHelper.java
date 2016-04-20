package pl.osik.autyzm.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by m.osik2 on 2016-04-19.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "autyzm";
    public static final AbstractDBTable[] tables = {
            new Lekcja(),
            new Folder(),
            new Film(),
            new Modul(),
            new Pytanie(),
            new Odpowiedz(),
            new User(),
            new Dziecko(),
            new LekcjaDziecko()
    };

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (AbstractDBTable table : tables) {
            db.execSQL(table.create());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
