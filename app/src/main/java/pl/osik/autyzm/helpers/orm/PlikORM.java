package pl.osik.autyzm.helpers.orm;

import pl.osik.autyzm.sql.Plik;

/**
 * Created by m.osik2 on 2016-05-05.
 */
public class PlikORM implements Comparable<PlikORM> {
    public final static String EXTRA_PLIK_ID = "plik_id";

    private int id, folder;
    private String path, name;

    public PlikORM(int id, int folder, String path) {
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

    @Override
    public int compareTo(PlikORM another) {
        return getName().compareTo(another.getName());
    }
}
