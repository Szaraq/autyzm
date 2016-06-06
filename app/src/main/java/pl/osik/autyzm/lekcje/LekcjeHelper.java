package pl.osik.autyzm.lekcje;

import android.content.ContentValues;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import pl.osik.autyzm.helpers.OperationsEnum;
import pl.osik.autyzm.helpers.orm.LekcjaORM;
import pl.osik.autyzm.helpers.orm.ModulORM;
import pl.osik.autyzm.helpers.orm.PytanieORM;
import pl.osik.autyzm.lekcje.nowy_modul.PlikActivity;
import pl.osik.autyzm.sql.Lekcja;
import pl.osik.autyzm.sql.Modul;
import pl.osik.autyzm.sql.Pytanie;

/**
 * Created by m.osik2 on 2016-05-17.
 */
public class LekcjeHelper {
    private static PlikActivity plikActivity;
    private static LekcjeTytulActivity lekcjeTytulActivity;
    private static OperationsEnum operacja;
    private static LekcjaORM lekcja;
    private static ModulORM modul;
    private static ArrayList<ModulORM> modulyList = new ArrayList<>();
    private static ArrayList<PytanieORM> pytaniaList = new ArrayList<>();
    private static ArrayList<Integer> pytaniaDoUsuniecia = new ArrayList<>();
    private static ArrayList<Integer> modulyDoUsuniecia = new ArrayList<>();

    public static OperationsEnum getOperacja() {
        return operacja;
    }

    public static void setOperacja(OperationsEnum operacja) {
        LekcjeHelper.operacja = operacja;
    }

    public static LekcjaORM getLekcja() {
        return lekcja;
    }

    public static void setLekcja(LekcjaORM lekcja) {
        LekcjeHelper.lekcja = lekcja;
        setModulyList();
    }

    public static ModulORM getModul() {
        return modul;
    }

    public static void setModul(ModulORM modul) {
        LekcjeHelper.modul = modul;
        setPytaniaList(Pytanie.getPytaniaForModul(modul.getId()));
    }

    public static void setNewModul(ModulORM modul) {
        setModul(modul);
        modul.setNumer(modulyList.size() + 1);
    }

    public static ArrayList<ModulORM> getModulyList() {
        return modulyList;
    }

    public static void setModulyList() {
        modulyList = Modul.getModulyForLekcja(lekcja.getId());
    }

    public static void clearAll() {
        operacja = null;
        lekcja = null;
        modul = null;
        modulyList.clear();
        pytaniaList.clear();
    }

    public static void addModul(ModulORM modul) {
        modulyList.add(modul);
    }

    public static ModulORM getModul(int position) {
        return modulyList.get(position);
    }

    public static void removeModul(int position) {
        int id = modulyList.get(position).getId();
        modulyList.remove(position);
        for(int i = position; i <= modulyList.size(); i++) {        //po usunięciu modułu numery modułów wartość position = numer dla modułów o numer >= position
            modulyList.get(i).setNumer(i);
        }

        if(id != 0) modulyDoUsuniecia.add(id);
    }

    public static boolean hasModules() {
        return modulyList.size() != 0;
    }

    public static boolean isLekcjaNew() {
        return lekcja.getId() == 0;
    }

    public static boolean isModulNew() {
        return modul.getId() == 0;
    }

    /**
     * Wywoływany po dodaniu nowego modułu
     */
    public static void commitAll() {
        /* Lekcja */
        ContentValues data = lekcja.getContentValues();
        Lekcja l = new Lekcja();
        if(isLekcjaNew()) {
            data.remove(Lekcja.COLUMN_ID);
            lekcja.setId((int) l.insert(data));
        } else {
            l.edit(lekcja.getId(), data);
        }
        data.clear();

        /* Moduł */
        Modul m = new Modul();
        modul.setLekcja(lekcja.getId());
        modul.setNumer(modulyList.size()+1);
        data = modul.getContentValues();
        if(isModulNew()) {
            data.remove(Modul.COLUMN_ID);
            modul.setId((int) m.insert(data));
        } else {
            m.edit(modul.getId(), data);
        }
        modulyList.add(modul);
        data.clear();

        for(int i : modulyDoUsuniecia) m.delete(i);

        /* Pytania */
        Pytanie p = new Pytanie();
        for (PytanieORM pyt : pytaniaList) {
            if(pyt.getTresc().length() == 0) continue;
            pyt.setModul(modul.getId());
            data = pyt.getContentValues();
            if(pyt.isNew()) {
                data.remove(Pytanie.COLUMN_ID);
                pyt.setId((int) p.insert(data));
            } else {
                p.edit(pyt.getId(), data);
            }
            data.clear();
        }

    }

    public static void addPytanie(String tresc) {
        PytanieORM pytanie = new PytanieORM();
        pytanie.setTresc(tresc);
        pytanie.setModul(modul.getId());
        pytaniaList.add(pytanie);
    }

    public static PytanieORM getPytanie(int position) {
        return pytaniaList.get(position);
    }

    public static ArrayList<PytanieORM> getPytaniaList() {
        return pytaniaList;
    }

    public static void setPytaniaList(ArrayList<PytanieORM> pytaniaList) {
        LekcjeHelper.pytaniaList = pytaniaList;
    }

    public static void removePytanie(int position) {
        int id = pytaniaList.get(position).getId();
        pytaniaList.remove(position);
        if(id != 0) pytaniaDoUsuniecia.add(id);
    }

    public static void setPlikActivity(PlikActivity plikActivity) {
        LekcjeHelper.plikActivity = plikActivity;
    }

    public static void finishPlikActivity() {
        plikActivity.finish();
    }

    public static void setLekcjeTytulActivity(LekcjeTytulActivity lekcjeTytulActivity) {
        LekcjeHelper.lekcjeTytulActivity = lekcjeTytulActivity;
    }

    public static void finishLekcjeTytulActivity() {
        lekcjeTytulActivity.finish();
    }

    public static void swapModul(boolean up, int numer) {
        Log.d("swap", numer+"");
        ModulORM modulToSwap = modulyList.get(numer-1);
        int newNumer = numer + (up ? -1 : 1);
        ModulORM modulSwapWith = modulyList.get(newNumer-1);

        modulToSwap.setNumer(newNumer);
        modulSwapWith.setNumer(numer);
        Collections.swap(modulyList, numer - 1, newNumer - 1);

        Modul m = new Modul();
        m.edit(modulToSwap.getId(), modulToSwap.getContentValues());
        m.edit(modulSwapWith.getId(), modulSwapWith.getContentValues());
    }
}
