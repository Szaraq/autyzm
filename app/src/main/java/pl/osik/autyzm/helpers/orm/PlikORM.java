package pl.osik.autyzm.helpers.orm;

import java.io.Serializable;

import pl.osik.autyzm.sql.Plik;

/**
 * Created by m.osik2 on 2016-05-05.
 */
public class PlikORM implements Comparable<PlikORM>, Serializable {
    public final static String EXTRA_PLIK_ID = "plik_id";
    public final static int SHORT_NAME_MAX_LENGTH = 30;

    private int id, folder;
    private String path, name;
    private boolean ghost;

    public PlikORM(int id, int folder, String path, boolean ghost) {
        setId(id);
        setFolder(folder);
        setPath(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        this.name = Plik.getName(path);
    }

    public int getFolder() {
        return folder;
    }

    public void setFolder(int folder) {
        this.folder = folder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        if(name.length() <= SHORT_NAME_MAX_LENGTH) return name;
        return name.substring(0, SHORT_NAME_MAX_LENGTH - 3) + "...";
    }

    @Override
    public int compareTo(PlikORM another) {
        return getName().compareTo(another.getName());
    }
}
