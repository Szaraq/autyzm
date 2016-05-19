package pl.osik.autyzm.uruchom;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.LinkedList;

import pl.osik.autyzm.helpers.orm.LekcjaORM;
import pl.osik.autyzm.helpers.orm.ModulORM;
import pl.osik.autyzm.helpers.orm.PytanieORM;
import pl.osik.autyzm.sql.Modul;
import pl.osik.autyzm.sql.Pytanie;
import pl.osik.autyzm.uruchom.modul.ModulMediaActivity;
import pl.osik.autyzm.uruchom.modul.ModulPytaniaActivity;

/**
 * Created by m.osik2 on 2016-05-19.
 */
public class UruchomController {
    private static LekcjaORM lekcja;
    private static HashMap<PytanieORM, Integer> punkty = new HashMap<>();
    private static LinkedList<ModulORM> moduly = new LinkedList<>();

    private static ModulORM modul;                                          //OBECNY moduł
    private static LinkedList<PytanieORM> pytania = new LinkedList<>();     //pytania w ramach OBECNEGO modułu
    private static PytanieORM pytanie;                                      //OBECNE pytanie

    private static LinkedList<Activity> listaActivity = new LinkedList<>();     //wszystkie Activity w OBECNYM module

    public static LekcjaORM getLekcja() {
        return lekcja;
    }

    public static void setLekcja(LekcjaORM lekcja) {
        UruchomController.lekcja = lekcja;
    }

    public static void runLekcja(Fragment fragment, LekcjaORM lekcjaToRun) {
        lekcja = lekcjaToRun;
        for (ModulORM m : Modul.getModulyForLekcja(lekcja.getId())) {
            moduly.add(m);
        }
        gotoNextActivity(fragment.getActivity());
    }

    private static void gotoNextActivity(Activity thisActivity) {
        listaActivity.add(thisActivity);
        Activity nextActivity = null;
        if(pytania.size() == 0) {
            closeModul();
            getNextModul();
            nextActivity = new ModulMediaActivity();
        } else {
            getNextPytanie();
            nextActivity = new ModulPytaniaActivity();
        }
        Intent intent = new Intent(thisActivity, nextActivity.getClass());
        thisActivity.startActivity(intent);
    }

    private static ModulORM getNextModul() {
        modul = moduly.pop();
        for (PytanieORM pyt : Pytanie.getPytaniaForModul(modul.getId())) {
            pytania.add(pyt);
        }
        return modul;
    }

    private static PytanieORM getNextPytanie() {
        pytanie = pytania.pop();
        return pytanie;
    }

    public static void addOdpowiedz(int liczbaPunktow) {
        punkty.put(pytanie, liczbaPunktow);
    }

    public static PytanieORM getPytanie() {
        return pytanie;
    }

    public static void clearAll() {
        lekcja = null;
        punkty.clear();
        moduly.clear();
        modul = null;
        pytania.clear();
        pytanie = null;
        listaActivity.clear();
    }

    /**
     * Zamyka wszystkie Activity w ramach tego modułu
     */
    public static void closeModul() {
        for (int i = 0; i < listaActivity.size(); i++)
            listaActivity.pollLast().finish();
    }
}
