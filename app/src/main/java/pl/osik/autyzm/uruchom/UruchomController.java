package pl.osik.autyzm.uruchom;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.orm.LekcjaORM;
import pl.osik.autyzm.helpers.orm.ModulORM;
import pl.osik.autyzm.helpers.orm.PytanieORM;
import pl.osik.autyzm.main.MainActivity;
import pl.osik.autyzm.sql.Lekcja;
import pl.osik.autyzm.sql.Modul;
import pl.osik.autyzm.sql.Odpowiedz;
import pl.osik.autyzm.sql.Pytanie;
import pl.osik.autyzm.uruchom.modul.ModulMediaActivity;
import pl.osik.autyzm.uruchom.modul.ModulPytaniaActivity;
import pl.osik.autyzm.uruchom.modul.PodsumowanieActivity;

/**
 * Created by m.osik2 on 2016-05-19.
 */
public class UruchomController {
    private static int idDziecka;
    private static boolean currentIsPytania;

    private static LekcjaORM lekcja;
    private static HashMap<PytanieORM, Integer> punkty = new HashMap<>();
    private static LinkedList<ModulORM> moduly = new LinkedList<>();

    private static ModulORM modul;                                          //OBECNY moduł
    private static LinkedList<PytanieORM> pytania = new LinkedList<>();     //pytania w ramach OBECNEGO modułu

    private static LinkedList<Activity> listaActivity = new LinkedList<>();     //wszystkie Activity w OBECNYM module

    public static LekcjaORM getLekcja() {
        return lekcja;
    }

    public static void setLekcja(LekcjaORM lekcja) {
        UruchomController.lekcja = lekcja;
    }

    public static void runLekcja(Fragment fragment, LekcjaORM lekcjaToRun) {
        lekcja = lekcjaToRun;
        Lekcja.todayUsed(lekcja.getId());
        for (ModulORM m : Modul.getModulyForLekcja(lekcja.getId())) {
            moduly.add(m);
        }
        gotoNextActivity(fragment.getActivity());
    }

    public static void gotoNextActivity(Activity thisActivity) {
        //Log.d("UruchomController", thisActivity.getClass().toString());
        Activity nextActivity = null;
        if(!(thisActivity instanceof MainActivity)) listaActivity.add(thisActivity);

        if(thisActivity instanceof MainActivity) {
            nextActivity = new WyborDzieckaActivity();
        } else if(currentIsPytania || thisActivity instanceof WyborDzieckaActivity) {
            closeModul();
            getNextModul();
            if(modul == null) {
                finishLekcja();
                nextActivity = new PodsumowanieActivity();
            } else nextActivity = new ModulMediaActivity();
        } else {
            nextActivity = new ModulPytaniaActivity();
        }
        Intent intent = new Intent(thisActivity, nextActivity.getClass());
        thisActivity.startActivity(intent);
    }

    private static void finishLekcja() {
        for (Map.Entry<PytanieORM, Integer> entry : punkty.entrySet()) {
            Odpowiedz o = new Odpowiedz();
            ContentValues data = new ContentValues();
            data.put(Odpowiedz.COLUMN_DATA, AppHelper.getToday());
            data.put(Odpowiedz.COLUMN_PUNKTY, entry.getValue());
            data.put(Odpowiedz.COLUMN_PYTANIE, entry.getKey().getId());
            data.put(Odpowiedz.COLUMN_DZIECKO, idDziecka);
            o.insert(data);
        }
    }

    private static ModulORM getNextModul() {
        if(moduly.size() == 0) return modul = null;
        modul = moduly.pop();
        for (PytanieORM pyt : Pytanie.getPytaniaForModul(modul.getId())) {
            pytania.add(pyt);
        }
        return modul;
    }

    public static void addOdpowiedzi(LinkedHashMap<PytanieORM, Integer> odpowiedzi) {
        punkty.putAll(odpowiedzi);
    }


    public static void clearAll() {
        lekcja = null;
        punkty.clear();
        moduly.clear();
        modul = null;
        pytania.clear();
        listaActivity.clear();
        idDziecka = 0;
        currentIsPytania = false;
    }

    /**
     * Zamyka wszystkie Activity w ramach tego modułu i czyści listę pytań
     */
    public static void closeModul() {
        pytania.clear();
        int end = listaActivity.size();
        for (int i = 0; i < end; i++)
            listaActivity.pollLast().finish();
    }

    public static void setIdDziecka(int idDziecka) {
        UruchomController.idDziecka = idDziecka;
    }

    public static LinkedList<PytanieORM> getPytania() {
        return pytania;
    }

    public static void setCurrentIsPytania(boolean currentIsPytania) {
        UruchomController.currentIsPytania = currentIsPytania;
    }
}
