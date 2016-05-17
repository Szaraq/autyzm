package pl.osik.autyzm.helpers.orm;

/**
 * Created by m.osik2 on 2016-05-17.
 */
public class ModulORM {
    private int id, plik, lekcja;
    private String name;

    public ModulORM(int id, String name, int plik, int lekcja) {
        this(name, plik, lekcja);
        this.id = id;
    }

    public ModulORM(String name, int plik, int lekcja) {
        this.plik = plik;
        this.lekcja = lekcja;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
