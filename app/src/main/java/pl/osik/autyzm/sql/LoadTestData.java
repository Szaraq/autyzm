package pl.osik.autyzm.sql;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by m.osik2 on 2016-04-21.
 */
public class LoadTestData {
    static DBHelper helper = new DBHelper();
    static SQLiteDatabase db = helper.getDBRead();
    private static boolean added = false;

    public static void load() {
        for (AbstractDBTable table : DBHelper.tables) {
            db.execSQL("DELETE FROM " + table.getTableName());
        }
        if(!added) {
            loadUser();
            loadDziecko();
            loadLekcja();
            loadLekcjaDziecko();
            loadFolder();
            loadFilm();
            loadModul();
            loadPytanie();
            loadOdpowiedz();
            added = true;
        }
    }

    private static void loadUser() {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put(User.COLUMN_LOGIN, "admin");
        params.put(User.COLUMN_PASS, "pass");
        User u = new User();
        db.execSQL("DELETE FROM " + User.TABLE_NAME);
        u.insert(params);
    }

    private static void loadDziecko() {
        Dziecko d = new Dziecko();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>() {{
            put(Dziecko.COLUMN_IMIE, "Jan");
            put(Dziecko.COLUMN_NAZWISKO, "Kowalski");
            put(Dziecko.COLUMN_DATAURODZENIA, "2001-01-01");
            put(Dziecko.COLUMN_DATAWPROWADZENIA, "2010-02-05");
            put(Dziecko.COLUMN_NOTATKI, "Notsy");
            put(Dziecko.COLUMN_OJCIECIMIE, "Janusz");
            put(Dziecko.COLUMN_OJCIECNAZWISKO, "Kowalski");
            put(Dziecko.COLUMN_OJCIECTELEFON, "500111222");
            put(Dziecko.COLUMN_MATKAIMIE, "Janina");
            put(Dziecko.COLUMN_MATKANAZWISKO, "Kowalska");
            put(Dziecko.COLUMN_MATKATELEFON, "500222333");
            put(Dziecko.COLUMN_USER, 1);
            put(Dziecko.COLUMN_PHOTO, null);
        }};
        for (int i = 0; i < 2; i++) {
            d.insert(params);
        }
    }

    private static void loadLekcja() {
        Lekcja l = new Lekcja();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(Lekcja.COLUMN_TYTUL, "Testowa lekcja");
        l.insert(params);
    }

    private static void loadLekcjaDziecko() {
        LekcjaDziecko l = new LekcjaDziecko();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(LekcjaDziecko.COLUMN_DZIECKO, 1);
        params.put(LekcjaDziecko.COLUMN_LEKCJA, 1);
        l.insert(params);
    }

    private static void loadFolder() {
        Folder f = new Folder();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(Folder.COLUMN_NAZWA, "Testowy folder");
        f.insert(params);
    }

    private static void loadFilm() {
        Film f = new Film();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(Film.COLUMN_PATH, "path/to/film");
        params.put(Film.COLUMN_FOLDER, "Folder_filmu");
        f.insert(params);
    }

    private static void loadModul() {
        Modul m = new Modul();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(Modul.COLUMN_NAZWA, "Testowy moduł");
        params.put(Modul.COLUMN_FILM, 1);
        params.put(Modul.COLUMN_LEKCJA, 1);
        m.insert(params);
    }

    private static void loadPytanie() {
        Pytanie p = new Pytanie();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(Pytanie.COLUMN_TRESC, "Przykładowe pytanie");
        params.put(Pytanie.COLUMN_MODUL, 1);
        p.insert(params);
    }

    private static void loadOdpowiedz() {
        Odpowiedz o = new Odpowiedz();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(Odpowiedz.COLUMN_DATA, "2015-01-02");
        params.put(Odpowiedz.COLUMN_PUNKTY, 1);
        params.put(Odpowiedz.COLUMN_PYTANIE, 1);
        o.insert(params);
        params.put(Odpowiedz.COLUMN_DATA, "2015-01-01");
        params.put(Odpowiedz.COLUMN_PUNKTY, 1);
        params.put(Odpowiedz.COLUMN_PYTANIE, 1);
        o.insert(params);
        params.put(Odpowiedz.COLUMN_DATA, "2015-01-01");
        params.put(Odpowiedz.COLUMN_PUNKTY, 0);
        params.put(Odpowiedz.COLUMN_PYTANIE, 1);
        o.insert(params);

        for(int i = 1; i < 9; i++) {
            params.put(Odpowiedz.COLUMN_DATA, "2015-01-0" + i);
            params.put(Odpowiedz.COLUMN_PUNKTY, 1);
            params.put(Odpowiedz.COLUMN_PYTANIE, 1);
            o.insert(params);
        }
    }
}
