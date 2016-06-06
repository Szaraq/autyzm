package pl.osik.autyzm.sql;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedHashMap;

import pl.osik.autyzm.helpers.MyApp;

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
            //loadLekcjaDziecko();
            loadFolder();
            loadPlik();
            loadModul();
            loadPytanie();
            loadOdpowiedz();
            added = true;
            helper.close();
        }

        //saveTestFile();
    }

    private static void saveTestFile() {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Testowa nazwa");
        File file2 = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/Miranda_de_Miranda.jpg");
        try {
            PrintWriter writer = new PrintWriter("/storage/sdcard/Download/text.txt");
            writer.println("aaa");
            writer.close();

            BufferedReader reader = new BufferedReader(new FileReader(file2));
            Log.d("Load reader", reader.readLine());
            Bitmap bm = BitmapFactory.decodeFile(file2.getPath());
            Log.d("Load reader", bm.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!file.mkdirs()) {
            Log.e("Load", "Directory not created");
        }
        Log.d("Load", file2.getAbsolutePath());
        Log.d("Load", file2.exists() ? "tak" : "nie");

        //return file;

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
        //params.put(User.COLUMN_PHOTO, "content://media/external/images/media/12");
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
        params.put(Dziecko.COLUMN_PHOTO, "/storage/sdcard/Download/Miranda_de_Miranda.jpg");
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
        l.insert(params);

        params.put(Lekcja.COLUMN_TYTUL, "Testowa ulubiona lekcja");
        params.put(Lekcja.COLUMN_USER, 1);
        params.put(Lekcja.COLUMN_DATA_OSTATNIEGO_UZYCIA, "2016-04-02");
        params.put(Lekcja.COLUMN_FAVOURITE, true);
        l.insert(params);

        params.put(Lekcja.COLUMN_TYTUL, "Lekcja drugiego usera");
        params.put(Lekcja.COLUMN_USER, 2);
        params.put(Lekcja.COLUMN_DATA_OSTATNIEGO_UZYCIA, "2016-04-02");
        params.put(Lekcja.COLUMN_FAVOURITE, true);
        l.insert(params);
    }

    private static void loadLekcjaDziecko() {
        LekcjaDziecko l = new LekcjaDziecko();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(LekcjaDziecko.COLUMN_DZIECKO, 1);
        params.put(LekcjaDziecko.COLUMN_LEKCJA, 1);
        l.insert(params);

        params.put(LekcjaDziecko.COLUMN_DZIECKO, 1);
        params.put(LekcjaDziecko.COLUMN_LEKCJA, 2);
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

        params.put(Plik.COLUMN_PATH, "/storage/sdcard/Download/Miranda_de_Miranda.jpg");
        params.put(Plik.COLUMN_FOLDER, "1");
        p.insert(params);

        params.put(Plik.COLUMN_PATH, "/storage/sdcard/SampleVideo_1280x720_1mb.mp4");
        params.put(Plik.COLUMN_FOLDER, "1");
        p.insert(params);

        /*for (int i = 0; i < 10; i++) {
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
        p.insert(params);*/
    }

    private static void loadModul() {
        Modul m = new Modul();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(Modul.COLUMN_NAZWA, "Miranda_de_Miranda.jpg");
        params.put(Modul.COLUMN_FILM, 1);
        params.put(Modul.COLUMN_LEKCJA, 1);
        params.put(Modul.COLUMN_NUMER, 1);
        m.insert(params);

        params.put(Modul.COLUMN_NAZWA, "Miranda_de_Miranda.jpg");
        params.put(Modul.COLUMN_FILM, 1);
        params.put(Modul.COLUMN_LEKCJA, 2);
        params.put(Modul.COLUMN_NUMER, 1);
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
