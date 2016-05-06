package pl.osik.autyzm.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedHashMap;

/**
 * Created by m.osik2 on 2016-04-21.
 */
public class LoadTestData {
    private static boolean added = false;

    public static void load() {
        if(!added) {
            DBHelper helper = DBHelper.getInstance();
            SQLiteDatabase db = helper.getDBWrite();
            for (AbstractDBTable table : DBHelper.tables) {
                db.execSQL("DROP TABLE IF EXISTS " + table.getTableName());
            }
            helper.onCreate(db);
            loadUser();
            loadDziecko();
            loadLekcja();
            loadLekcjaDziecko();
            loadFolder();
            loadPlik();
            loadModul();
            loadPytanie();
            loadOdpowiedz();
            added = true;
            helper.close();
        }
    }

    private static void loadUser() {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        User u = new User();
        params.put(User.COLUMN_IMIE, "Adam");
        params.put(User.COLUMN_NAZWISKO, "Adminowski");
        params.put(User.COLUMN_LOGIN, "a");
        params.put(User.COLUMN_PASS, "p");
        params.put(User.COLUMN_PHOTO, null);
        u.insert(params);
        params.put(User.COLUMN_IMIE, "Bartosz");
        params.put(User.COLUMN_NAZWISKO, "Adminowicz");
        params.put(User.COLUMN_LOGIN, "a2");
        params.put(User.COLUMN_PASS, "p");
        params.put(User.COLUMN_PHOTO, "content://media/external/images/media/12");
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
        for (int i = 0; i < 20; i++) {
            d.insert(params);
        }
        params.put(Dziecko.COLUMN_IMIE, "Mateusz");
        params.put(Dziecko.COLUMN_USER, 2);
        d.insert(params);
    }

    private static void loadLekcja() {
        Lekcja l = new Lekcja();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(Lekcja.COLUMN_TYTUL, "Testowa lekcja");
        params.put(Lekcja.COLUMN_DATA_OSTATNIEGO_UZYCIA, "2016-04-01");
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

        params.put(Folder.COLUMN_NAZWA, Folder.ROOT_NAME);
        params.put(Folder.COLUMN_FOLDER, Folder.NO_PARENT_FOLDER);
        params.put(Folder.COLUMN_USER, 1);
        f.insert(params);

        params.put(Folder.COLUMN_NAZWA, Folder.ROOT_NAME);
        params.put(Folder.COLUMN_FOLDER, Folder.NO_PARENT_FOLDER);
        params.put(Folder.COLUMN_USER, 2);
        f.insert(params);

        for (int i = 0; i < 9; i++) {
            params.put(Folder.COLUMN_NAZWA, "Testowy folder " + i);
            params.put(Folder.COLUMN_FOLDER, 1);
            params.put(Folder.COLUMN_USER, 1);
            f.insert(params);
        }

        params.put(Folder.COLUMN_NAZWA, "Testowy podfolder");
        params.put(Folder.COLUMN_FOLDER, 4);
        params.put(Folder.COLUMN_USER, 1);
        f.insert(params);

        params.put(Folder.COLUMN_NAZWA, "Testowy folder Usera2");
        params.put(Folder.COLUMN_FOLDER, 2);
        params.put(Folder.COLUMN_USER, 2);
        f.insert(params);
    }

    private static void loadPlik() {
        //"content://media/external/images/media/12"
        Plik p = new Plik();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();

        for (int i = 0; i < 10; i++) {
            params.put(Plik.COLUMN_PATH, "content://media/external/images/media/12");
            params.put(Plik.COLUMN_FOLDER, Folder.ROOT_ID);
            p.insert(params);
        }

        for (int i = 0; i < 10; i++) {
            params.put(Plik.COLUMN_PATH, "content://media/external/images/media/12");
            params.put(Plik.COLUMN_FOLDER, "1");
            p.insert(params);
        }

        params.put(Plik.COLUMN_PATH, "content://media/external/images/media/12");
        params.put(Plik.COLUMN_FOLDER, "10");
        p.insert(params);
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
