package pl.osik.autyzm.helpers.orm;

import android.content.ContentValues;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pl.osik.autyzm.sql.Lekcja;
import pl.osik.autyzm.sql.User;

/**
 * Created by m.osik2 on 2016-05-06.
 */
public class LekcjaORM implements Serializable {

    int id, user;
    String tytul;
    boolean favourite;
    private Calendar lastUsed = Calendar.getInstance();

    public LekcjaORM(int id, String tytul, String lastUsed, boolean favourite) {
        this();
        this.id = id;
        this.tytul = tytul;
        this.favourite = favourite;
        setLastUsed(lastUsed);
    }

    public LekcjaORM() {
        user = User.getCurrentId();
    }

    public String getLastUsedAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
        return sdf.format(getLastUsed().getTime());
    }

    public Calendar getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(String lastUsed) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
        try {
            this.lastUsed.setTime(sdf.parse(lastUsed));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getTytul() {
        return tytul;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContentValues getContentValues() {
        ContentValues out = new ContentValues();
        out.put(Lekcja.COLUMN_ID, id);
        out.put(Lekcja.COLUMN_TYTUL, tytul);
        out.put(Lekcja.COLUMN_DATA_OSTATNIEGO_UZYCIA, getLastUsedAsString());
        out.put(Lekcja.COLUMN_FAVOURITE, false);
        out.put(Lekcja.COLUMN_USER, user);
        return out;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public boolean isFavourite() {
        return favourite;
    }
}
