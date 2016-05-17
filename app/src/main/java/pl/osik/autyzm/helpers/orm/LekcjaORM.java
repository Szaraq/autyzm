package pl.osik.autyzm.helpers.orm;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by m.osik2 on 2016-05-06.
 */
public class LekcjaORM implements Serializable {

    int id;
    String tytul;
    private Calendar lastUsed = Calendar.getInstance();

    public LekcjaORM(int id, String tytul, String lastUsed) {
        this.id = id;
        this.tytul = tytul;
        setLastUsed(lastUsed);
    }

    public LekcjaORM() { }

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
}
