package pl.osik.autyzm.helpers.orm;

import android.content.ContentValues;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pl.osik.autyzm.sql.Lekcja;
import pl.osik.autyzm.sql.User;

/**
 * Created by m.osik2 on 2016-05-06.
 */
public class LekcjaORM implements Serializable {

    public static final long NEVER_USED = Long.MIN_VALUE;
    public static final String NEVER_USED_IN_DB = setNeverUsedConstant();

    private static String setNeverUsedConstant() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(NEVER_USED));
        return sdf.format(cal.getTime());
    }

    int id, user;
    String tytul;
    boolean favourite, ghost;
    private Calendar lastUsed = Calendar.getInstance();

    private void init() {
        user = User.getCurrentId();
    }

    public LekcjaORM(int id, String tytul, String lastUsed, boolean favourite, boolean ghost) {
        init();
        this.id = id;
        this.tytul = tytul;
        this.favourite = favourite;
        setLastUsed(lastUsed);
        this.ghost = ghost;
    }

    public LekcjaORM() {
        init();
        setLastUsedAsNever();
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

    public void setLastUsed(Calendar lastUsed) {
        this.lastUsed = lastUsed;
    }

    public void setLastUsedAsNever() {
        lastUsed.setTime(new Date(NEVER_USED));
    }

    public boolean isNeverUsed() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(NEVER_USED));
        return lastUsed.equals(cal);
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
        out.put(Lekcja.COLUMN_GHOST, ghost);
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

    public boolean isGhost() {
        return ghost;
    }

    public void setGhost(boolean ghost) {
        this.ghost = ghost;
    }
}
