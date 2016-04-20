package pl.osik.autyzm.sql;

import java.util.HashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Dziecko extends AbstractDBTable {
    public static final String TABLE_NAME = "Dziecko";
    public static final String COLUMN_IMIE = "imie";
    public static final String COLUMN_NAZWISKO = "nazwisko";
    public static final String COLUMN_DATAURODZENIA = "data_urodzenia";
    public static final String COLUMN_DATAWPROWADZENIA = "data_wprowadzenia";
    public static final String COLUMN_NOTATKI = "notatki";
    public static final String COLUMN_OJCIECIMIE = "imie_ojca";
    public static final String COLUMN_OJCIECNAZWISKO = "nazwisko_ojca";
    public static final String COLUMN_OJCIECTELEFON = "telefon_ojca";
    public static final String COLUMN_MATKAIMIE = "imie_matki";
    public static final String COLUMN_MATKANAZWISKO = "nazwisko_matki";
    public static final String COLUMN_MATKATELEFON = "telefon_matki";
    public static final String COLUMN_USER = "user";

    protected static final HashMap<String, String> colTypeMap = new HashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER");
        put(COLUMN_IMIE, "TEXT");
        put(COLUMN_NAZWISKO, "TEXT");
        put(COLUMN_DATAURODZENIA, "DATE");
        put(COLUMN_DATAWPROWADZENIA, "DATE");
        put(COLUMN_NOTATKI, "TEXT");
        put(COLUMN_OJCIECIMIE, "TEXT");
        put(COLUMN_OJCIECNAZWISKO, "TEXT");
        put(COLUMN_OJCIECTELEFON, "TEXT");
        put(COLUMN_MATKAIMIE, "TEXT");
        put(COLUMN_MATKANAZWISKO, "TEXT");
        put(COLUMN_MATKATELEFON, "TEXT");
        put(COLUMN_USER, "INTEGER");
    }};

    @Override
    protected String create() {
        return getCreateStart() + createArgumentQuery() + createForeignKey(COLUMN_USER, User.TABLE_NAME) + ")";
    }
}
