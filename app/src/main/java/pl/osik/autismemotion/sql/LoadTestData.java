package pl.osik.autismemotion.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedHashMap;

/**
 * Created by m.osik2 on 2016-04-21.
 */
public class LoadTestData {

    private static boolean added = false;

    public static void load() {
        if(added) return;
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBWrite();
        for (AbstractDBTable table : DBHelper.tables) {
            db.execSQL("DROP TABLE IF EXISTS " + table.getTableName());
        }
        helper.onCreate(db);
        loadUser();
        loadDziecko();
        loadLekcja();
        //loadLekcjaDziecko();
        loadFolder();
        loadPlik();
        loadModul();
        loadPytanie();
        loadOdpowiedz();
        added = true;
        helper.close();
    }

    private static void loadUser() {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        User u = new User();
        params.put(User.COLUMN_IMIE, "Adam");
        params.put(User.COLUMN_NAZWISKO, "Adminowski");
        params.put(User.COLUMN_LOGIN, "a");
        params.put(User.COLUMN_PASS, "p");
        params.put(User.COLUMN_PHOTO, "/storage/sdcard/Download/Miranda_de_Miranda.jpg");
        //params.put(User.COLUMN_PHOTO, null);
        u.insert(params);
        params.put(User.COLUMN_IMIE, "Bartosz");
        params.put(User.COLUMN_NAZWISKO, "Adminowicz");
        params.put(User.COLUMN_LOGIN, "a2");
        params.put(User.COLUMN_PASS, "p");
        params.put(User.COLUMN_PHOTO, "/storage/sdcard/Download/z19402644Q,Wizja-artystyczna-czlowieka-z-Jaskini-Czerwonego-J.jpg");
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
            //put(Dziecko.COLUMN_OJCIECTELEFON, "500111222");
            put(Dziecko.COLUMN_OJCIECTELEFON, "502299667");
            put(Dziecko.COLUMN_MATKAIMIE, "Janina");
            put(Dziecko.COLUMN_MATKANAZWISKO, "Kowalska");
            put(Dziecko.COLUMN_MATKATELEFON, "500222333");
            put(Dziecko.COLUMN_USER, 1);
            //put(Dziecko.COLUMN_PHOTO, null);
            put(Dziecko.COLUMN_PHOTO, "/storage/sdcard/Download/Miranda_de_Miranda.jpg");
        }};
        for (int i = 0; i < 20; i++) {
            d.insert(params);
        }
        params.put(Dziecko.COLUMN_IMIE, "Mateusz");
        params.put(Dziecko.COLUMN_NAZWISKO, "Azdjęciowy");
        params.put(Dziecko.COLUMN_USER, 1);
        params.put(Dziecko.COLUMN_OJCIECNAZWISKO, null);
        params.put(Dziecko.COLUMN_MATKATELEFON, null);
        params.put(Dziecko.COLUMN_OJCIECTELEFON, null);
        params.put(Dziecko.COLUMN_PHOTO, "/storage/sdcard/Download/Miranda_de_Mirandaa.jpg");
        d.insert(params);

        params.put(Dziecko.COLUMN_IMIE, "Mateusz");
        params.put(Dziecko.COLUMN_USER, 2);
        d.insert(params);
    }

    private static void loadLekcja() {
        Lekcja l = new Lekcja();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(Lekcja.COLUMN_TYTUL, "Testowa lekcja");
        params.put(Lekcja.COLUMN_USER, 1);
        params.put(Lekcja.COLUMN_DATA_OSTATNIEGO_UZYCIA, "2016-04-01");
        params.put(Lekcja.COLUMN_FAVOURITE, false);
        params.put(Lekcja.COLUMN_GHOST, 0);
        l.insert(params);

        params.put(Lekcja.COLUMN_TYTUL, "Testowa ulubiona lekcja");
        params.put(Lekcja.COLUMN_USER, 1);
        params.put(Lekcja.COLUMN_DATA_OSTATNIEGO_UZYCIA, "2016-04-02");
        params.put(Lekcja.COLUMN_FAVOURITE, true);
        params.put(Lekcja.COLUMN_GHOST, 0);
        l.insert(params);

        params.put(Lekcja.COLUMN_TYTUL, "Nieistniejaca lekcja");
        params.put(Lekcja.COLUMN_USER, 1);
        params.put(Lekcja.COLUMN_DATA_OSTATNIEGO_UZYCIA, "2016-04-02");
        params.put(Lekcja.COLUMN_FAVOURITE, true);
        params.put(Lekcja.COLUMN_GHOST, 0);
        l.insert(params);

        params.put(Lekcja.COLUMN_TYTUL, "Lekcja drugiego usera");
        params.put(Lekcja.COLUMN_USER, 2);
        params.put(Lekcja.COLUMN_DATA_OSTATNIEGO_UZYCIA, "2016-04-02");
        params.put(Lekcja.COLUMN_FAVOURITE, true);
        params.put(Lekcja.COLUMN_GHOST, 0);
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

        for (int i = 0; i < 8; i++) {
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
        Plik p = new Plik();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();

        params.put(Plik.COLUMN_PATH, "/storage/sdcard/Download/Miranda_de_Miranda.jpg");
        params.put(Plik.COLUMN_FOLDER, "1");
        params.put(Plik.COLUMN_GHOST, 0);
        p.insert(params);

        params.put(Plik.COLUMN_PATH, "/storage/sdcard/SampleVideo_1280x720_1mb.mp4");
        params.put(Plik.COLUMN_FOLDER, "1");
        params.put(Plik.COLUMN_GHOST, 0);
        p.insert(params);

        params.put(Plik.COLUMN_PATH, "/jakis/nieistniejacy/plik");
        params.put(Plik.COLUMN_FOLDER, "1");
        params.put(Plik.COLUMN_GHOST, 0);
        p.insert(params);
    }

    private static void loadModul() {
        Modul m = new Modul();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(Modul.COLUMN_NAZWA, "Miranda_de_Miranda");
        params.put(Modul.COLUMN_FILM, 1);
        params.put(Modul.COLUMN_LEKCJA, 1);
        params.put(Modul.COLUMN_NUMER, 1);
        params.put(Modul.COLUMN_GHOST, 0);
        m.insert(params);

        params.put(Modul.COLUMN_NAZWA, "SampleVideo_1280x720_1mb");
        params.put(Modul.COLUMN_FILM, 2);
        params.put(Modul.COLUMN_LEKCJA, 2);
        params.put(Modul.COLUMN_NUMER, 1);
        params.put(Modul.COLUMN_GHOST, 0);
        m.insert(params);

        params.put(Modul.COLUMN_NAZWA, "Nieistniejacy modul");
        params.put(Modul.COLUMN_FILM, 3);
        params.put(Modul.COLUMN_LEKCJA, 3);
        params.put(Modul.COLUMN_NUMER, 1);
        params.put(Modul.COLUMN_GHOST, 0);
        m.insert(params);

        params.put(Modul.COLUMN_NAZWA, "Nieistniejacy modul2");
        params.put(Modul.COLUMN_FILM, 3);
        params.put(Modul.COLUMN_LEKCJA, 2);
        params.put(Modul.COLUMN_NUMER, 2);
        params.put(Modul.COLUMN_GHOST, 0);
        m.insert(params);
    }

    private static void loadPytanie() {
        Pytanie p = new Pytanie();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(Pytanie.COLUMN_TRESC, "Przykładowe pytanie");
        params.put(Pytanie.COLUMN_MODUL, 1);
        p.insert(params);
        params.put(Pytanie.COLUMN_TRESC, "Przykładowe pytanie 2");
        params.put(Pytanie.COLUMN_MODUL, 1);
        p.insert(params);

        params.put(Pytanie.COLUMN_TRESC, "Przykładowe pytanie do modułu 2");
        params.put(Pytanie.COLUMN_MODUL, 2);
        p.insert(params);
    }

    private static void loadOdpowiedz() {
        Odpowiedz o = new Odpowiedz();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(Odpowiedz.COLUMN_DATA, "2015-01-02");
        params.put(Odpowiedz.COLUMN_PUNKTY, 1);
        params.put(Odpowiedz.COLUMN_PYTANIE, 1);
        params.put(Odpowiedz.COLUMN_DZIECKO, 1);
        o.insert(params);
        params.put(Odpowiedz.COLUMN_DATA, "2015-01-01");
        params.put(Odpowiedz.COLUMN_PUNKTY, 1);
        params.put(Odpowiedz.COLUMN_PYTANIE, 1);
        params.put(Odpowiedz.COLUMN_DZIECKO, 1);
        o.insert(params);
        params.put(Odpowiedz.COLUMN_DATA, "2015-01-01");
        params.put(Odpowiedz.COLUMN_PUNKTY, 0);
        params.put(Odpowiedz.COLUMN_PYTANIE, 1);
        params.put(Odpowiedz.COLUMN_DZIECKO, 1);
        o.insert(params);

        for(int i = 1; i < 9; i++) {
            params.put(Odpowiedz.COLUMN_DATA, "2015-01-0" + i);
            params.put(Odpowiedz.COLUMN_PUNKTY, (int) (Math.random() * 5));
            params.put(Odpowiedz.COLUMN_PYTANIE, 1);
            params.put(Odpowiedz.COLUMN_DZIECKO, 1);
            o.insert(params);
        }
    }
}
