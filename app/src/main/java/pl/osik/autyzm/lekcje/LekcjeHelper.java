package pl.osik.autyzm.lekcje;

import android.util.Log;

import java.util.ArrayList;

import pl.osik.autyzm.helpers.OperationsEnum;
import pl.osik.autyzm.helpers.orm.LekcjaORM;
import pl.osik.autyzm.helpers.orm.ModulORM;
import pl.osik.autyzm.sql.Modul;

/**
 * Created by m.osik2 on 2016-05-17.
 */
public class LekcjeHelper {
    private static OperationsEnum operacja;
    private static LekcjaORM lekcja;
    private static ModulORM modul;
    private static ArrayList<ModulORM> modulyList = new ArrayList<>();

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
        modulyList = Modul.getModulyForLekcja(lekcja.getId());
    }

    public static ModulORM getModul() {
        return modul;
    }

    public static void setModul(ModulORM modul) {
        LekcjeHelper.modul = modul;
    }

    public static ArrayList<ModulORM> getModulyList() {
        return modulyList;
    }

    public static void setModulyList(ArrayList<ModulORM> modulyList) {
        LekcjeHelper.modulyList = modulyList;
    }

    public static void clearAll() {
        operacja = null;
        lekcja = null;
        modul = null;
        modulyList.clear();
    }

    public static void addModul(ModulORM modul) {
        modulyList.add(modul);
    }

    public static ModulORM getModul(int position) {
        return modulyList.get(position);
    }

    public static boolean hasModules() {
        return modulyList.size() == 0;
    }

    public static boolean isLekcjaNew() {
        return lekcja.getId() == 0;
    }
}
