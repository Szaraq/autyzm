package pl.osik.autyzm.sql;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public abstract class AbstractDBTable {
    public static final String TABLE_NAME = "";
    public static final String COLUMN_ID = "id";

    protected static final HashMap<String, String> colTypeMap = null;

    protected abstract String create();

    protected String createArgumentQuery() {
        StringBuilder out = new StringBuilder();
        for(Map.Entry<String, String> entry : colTypeMap.entrySet()) {
            out.append(entry.getKey());
            out.append(" ");
            out.append(entry.getValue());
            out.append(",");
        }
        out.setLength(out.length() - 1);
        return out.toString();
    }

    protected String createForeignKey(String colFrom, String tabTo) {
        return ", FOREIGN KEY(" + colFrom + ") REFERENCES " + tabTo + "(id)";
    }

    protected String getCreateStart() {
        return "create table " + TABLE_NAME + " (" + createArgumentQuery();
    }

}
