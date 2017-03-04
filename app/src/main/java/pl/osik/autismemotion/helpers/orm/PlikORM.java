package pl.osik.autismemotion.helpers.orm;

import java.io.Serializable;

import pl.osik.autismemotion.helpers.FileHelper;
import pl.osik.autismemotion.sql.Plik;

/**
 * Created by m.osik2 on 2016-05-05.
 */
public class PlikORM implements Comparable<PlikORM>, Serializable {
    public final static String EXTRA_PLIK_ID = "plik_id";
    public final static String EXTRA_NATIVE = "got_by_native";
    public final static int SHORT_NAME_MAX_LENGTH = 30;

    private int id, folder;
    private String path, name, thumb;
    private boolean ghost, gotByNative;

    public PlikORM(int id, int folder, String path, boolean ghost, String thumb, boolean gotByNative) {
        setId(id);
        setFolder(folder);
        setGotByNative(gotByNative);
        setPath(path);
        setThumb(thumb);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        this.name = Plik.getName(path, gotByNative);
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

    public String getName(boolean extension) {
        if(!extension) {
            return FileHelper.removeExtension(name);
        }
        return name;
    }

    public String getShortName(boolean extension) {
        return getShortName(SHORT_NAME_MAX_LENGTH, extension);
    }

    public String getShortName(int maxLength, boolean extension) {
        String newName = getName(extension);
        if(newName.length() <= maxLength) return newName;
        return newName.substring(0, maxLength - 3) + "...";
    }

    public boolean isGotByNative() {
        return gotByNative;
    }

    public void setGotByNative(boolean gotByNative) {
        this.gotByNative = gotByNative;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public FileHelper.FileTypes getType() {
        return FileHelper.getType(path, gotByNative);
    }

    @Override
    public int compareTo(PlikORM another) {
        return getName(true).compareTo(another.getName(true));
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof PlikORM)
            return getId() == ((PlikORM) object).getId();
        else
            return super.equals(object);
    }
}
