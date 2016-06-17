package pl.osik.autyzm.helpers.orm;

/**
 * Created by m.osik2 on 2016-06-17.
 */
public class FolderORM {

    String nazwa;
    int id, folder, user;

    public FolderORM(int id, String nazwa, int folder, int user) {
        this(nazwa, folder, user);
        this.id = id;
    }

    public FolderORM(String nazwa, int folder, int user) {
        this.nazwa = nazwa;
        this.folder = folder;
        this.user = user;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFolder() {
        return folder;
    }

    public void setFolder(int folder) {
        this.folder = folder;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}
