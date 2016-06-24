package pl.osik.autyzm.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import pl.osik.autyzm.helpers.MyApp;

/**
 * Created by m.osik2 on 2016-04-19.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "autyzm";
    public static final AbstractDBTable[] tables = {
            new Lekcja(),
            new Folder(),
            new Plik(),
            new Modul(),
            new Pytanie(),
            new Odpowiedz(),
            new User(),
            new Dziecko(),
            new Activity(),
            new FirstUse()
    };
    private static final DBHelper instance = null;

    public static DBHelper getInstance() {
        if(instance == null) return new DBHelper();
        return instance;
    }

    public static DBHelper getInstance(Context context) {
        if(instance == null) return new DBHelper(context);
        return instance;
    }

    private DBHelper() {
        super(MyApp.getContext(), DB_NAME, null, 1);
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (AbstractDBTable table : tables) {
            db.execSQL(table.create());
        }
        Activity.createRows(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public SQLiteDatabase getDBRead() {
        return getReadableDatabase();
    }

    protected SQLiteDatabase getDBWrite() {
        return getWritableDatabase();
    }
}
