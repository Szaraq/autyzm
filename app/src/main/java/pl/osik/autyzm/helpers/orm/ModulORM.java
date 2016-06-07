package pl.osik.autyzm.helpers.orm;

import android.content.ContentValues;

import pl.osik.autyzm.sql.Modul;

/**
 * Created by m.osik2 on 2016-05-17.
 */
public class ModulORM {

    private int id, plik, lekcja, numer;
    private String name;
    private boolean ghost;

    public ModulORM(int id, String name, int plik, int lekcja, int numer, boolean ghost) {
        this(name, plik, lekcja, numer, ghost);
        this.id = id;
    }

    public ModulORM(String name, int plik, int lekcja, int numer, boolean ghost) {
        this.plik = plik;
        this.lekcja = lekcja;
        this.name = name;
        this.numer = numer;
        this.ghost = ghost;
    }

    public ModulORM() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlik() {
        return plik;
    }

    public void setPlik(int plik) {
        this.plik = plik;
    }

    public int getLekcja() {
        return lekcja;
    }

    public void setLekcja(int lekcja) {
        this.lekcja = lekcja;
    }

    public int getNumer() {
        return numer;
    }

    public void setNumer(int numer) {
        this.numer = numer;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        if(name.length() <= PlikORM.SHORT_NAME_MAX_LENGTH) return name;
        return name.substring(0, PlikORM.SHORT_NAME_MAX_LENGTH - 3) + "...";
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContentValues getContentValues() {
        ContentValues out = new ContentValues();
        out.put(Modul.COLUMN_ID, id);
        out.put(Modul.COLUMN_NAZWA, name);
        out.put(Modul.COLUMN_FILM, plik);
        out.put(Modul.COLUMN_LEKCJA, lekcja);
        out.put(Modul.COLUMN_NUMER, numer);
        return out;
    }

    public boolean isGhost() {
        return ghost;
    }

    public void setGhost(boolean ghost) {
        this.ghost = ghost;
    }
}
