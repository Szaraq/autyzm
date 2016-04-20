package pl.osik.autyzm.sql;

import java.util.HashMap;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Film extends AbstractDBTable {
    public static final String TABLE_NAME = "Film";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_FOLDER = "folder";

    protected static final HashMap<String, String> colTypeMap = new HashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER");
        put(COLUMN_PATH, "TEXT");
        put(COLUMN_FOLDER, "INTEGER");
    }};

    @Override
    protected String create() {
        return getCreateStart()
                + createForeignKey(COLUMN_FOLDER, Folder.TABLE_NAME)
                + ")";
    }
}
