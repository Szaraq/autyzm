package pl.osik.autyzm.sql;

import android.graphics.Path;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Plik extends AbstractDBTable {
    public static final String TABLE_NAME = "Plik";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_FOLDER = "folder";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_PATH, "TEXT");
        put(COLUMN_FOLDER, "INTEGER");
    }};

    @Override
    protected String create() {
        return getCreateStart()
                + createForeignKey(COLUMN_FOLDER, Folder.TABLE_NAME)
                + ")";
    }

    @Override
    protected LinkedHashMap<String, String> getMap() {
        return colTypeMap;
    }
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public static String getName(String path) {
        File f = new File(path);
        return f.getName();
    }
}
