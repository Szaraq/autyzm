package pl.osik.autyzm.helpers.orm;

import android.content.ContentValues;

import pl.osik.autyzm.sql.Pytanie;

/**
 * Created by m.osik2 on 2016-05-18.
 */
public class PytanieORM {
    String tresc;
    int id, modul;

    public PytanieORM() {}

    public PytanieORM(int id, String tresc, int modul) {
        this.id = id;
        this.tresc = tresc;
        this.modul = modul;
    }

    public String getTresc() {
        return tresc;
    }

    public void setTresc(String tresc) {
        this.tresc = tresc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getModul() {
        return modul;
    }

    public void setModul(int modul) {
        this.modul = modul;
    }

    public ContentValues getContentValues() {
        ContentValues out = new ContentValues();
        out.put(Pytanie.COLUMN_ID, id);
        out.put(Pytanie.COLUMN_TRESC, tresc);
        out.put(Pytanie.COLUMN_MODUL, modul);
        return out;
    }

    public boolean isNew() {
        return id == 0;
    }
}
