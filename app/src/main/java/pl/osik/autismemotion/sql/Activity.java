package pl.osik.autismemotion.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedHashMap;
import java.util.Map;

import pl.osik.autismemotion.dzieci.DzieciDetailsActivity;
import pl.osik.autismemotion.dzieci.DzieciFragment;
import pl.osik.autismemotion.dzieci.DzieciStatisticsActivity;
import pl.osik.autismemotion.help.HelpFragment;
import pl.osik.autismemotion.helpers.MyEntry;
import pl.osik.autismemotion.lekcje.LekcjeFragment;
import pl.osik.autismemotion.lekcje.LekcjeHelper;
import pl.osik.autismemotion.lekcje.LekcjeModulActivity;
import pl.osik.autismemotion.lekcje.LekcjeTytulActivity;
import pl.osik.autismemotion.lekcje.nowy_modul.PlikActivity;
import pl.osik.autismemotion.lekcje.nowy_modul.PytaniaActivity;
import pl.osik.autismemotion.login.LoginActivity;
import pl.osik.autismemotion.login.UserDetailsActivity;
import pl.osik.autismemotion.main.MainActivity;
import pl.osik.autismemotion.main.StartFragment;
import pl.osik.autismemotion.multimedia.MultimediaFragment;
import pl.osik.autismemotion.multimedia.PickerActivity;
import pl.osik.autismemotion.multimedia.ShowMediaActivity;
import pl.osik.autismemotion.uruchom.UruchomFragment;
import pl.osik.autismemotion.uruchom.WyborDzieckaActivity;
import pl.osik.autismemotion.uruchom.modul.ModulMediaActivity;
import pl.osik.autismemotion.uruchom.modul.ModulPytaniaActivity;

/**
 * Created by m.osik2 on 2016-06-21.
 */
public class Activity extends AbstractDBTable {
    public static final String TABLE_NAME = "Activity";
    public static final String COLUMN_NAME = "name";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_NAME, "TEXT");
    }};

    public static final LinkedHashMap<Class, Map.Entry<Integer, String>> activities = new LinkedHashMap<Class, Map.Entry<Integer, String>>() {{
        put(DzieciDetailsActivity.class, new MyEntry<Integer, String>(1, "DzieciDetails"));
        put(DzieciFragment.class, new MyEntry<Integer, String>(2, "DzieciFragment"));
        put(DzieciStatisticsActivity.class, new MyEntry<Integer, String>(3, "DzieciStatisticsActivity"));
        put(HelpFragment.class, new MyEntry<Integer, String>(4, "HelpFragment"));
        put(PlikActivity.class, new MyEntry<Integer, String>(5, "PlikActivity"));
        put(PytaniaActivity.class, new MyEntry<Integer, String>(6, "PytaniaActivity"));
        put(LekcjeFragment.class, new MyEntry<Integer, String>(7, "LekcjeFragment"));
        put(LekcjeHelper.class, new MyEntry<Integer, String>(8, "LekcjeHelper"));
        put(LekcjeModulActivity.class, new MyEntry<Integer, String>(9, "LekcjeModulActivity"));
        put(LekcjeTytulActivity.class, new MyEntry<Integer, String>(10, "LekcjeTytulActivity"));
        put(LoginActivity.class, new MyEntry<Integer, String>(11, "LoginActivity"));
        put(UserDetailsActivity.class, new MyEntry<Integer, String>(12, "UserDetailsActivity"));
        put(MainActivity.class, new MyEntry<Integer, String>(13, "MainActivity"));
        put(StartFragment.class, new MyEntry<Integer, String>(14, "StartFragment"));
        put(MultimediaFragment.class, new MyEntry<Integer, String>(15, "MultimediaFragment"));
        put(PickerActivity.class, new MyEntry<Integer, String>(16, "PickerActivity"));
        put(ShowMediaActivity.class, new MyEntry<Integer, String>(18, "ShowMediaActivity"));
        put(ModulMediaActivity.class, new MyEntry<Integer, String>(19, "ModulMediaActivity"));
        put(ModulPytaniaActivity.class, new MyEntry<Integer, String>(20, "ModulPytaniaActivity"));
        put(UruchomFragment.class, new MyEntry<Integer, String>(21, "UruchomFragment"));
        put(WyborDzieckaActivity.class, new MyEntry<Integer, String>(22, "WyborDzieckaActivity"));
    }};

    @Override
    protected String create() {
        return getCreateStart() + ")";
    }

    public static void createRows(SQLiteDatabase db) {
        if(!isEmpty(db)) return;
        Activity a = new Activity();
        ContentValues data = new ContentValues();
        for (Map.Entry<Class, Map.Entry<Integer, String>> entry : activities.entrySet()) {
            data.put(COLUMN_ID, entry.getValue().getKey());
            data.put(COLUMN_NAME, entry.getValue().getValue());
            a.insert(data, db);
        }
    }

    private long insert(ContentValues data, SQLiteDatabase db) {
        long out = db.insert(getTableName(), null, data);
        return out;
    }

    private static boolean isEmpty(SQLiteDatabase db) {
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        boolean out = cursor.getCount() == 0;
        cursor.close();
        return out;
    }

    public static int getIdForActivity(Class mClass) {
        return activities.get(mClass).getKey();
    }

    @Override
    protected LinkedHashMap<String, String> getMap() {
        return colTypeMap;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
