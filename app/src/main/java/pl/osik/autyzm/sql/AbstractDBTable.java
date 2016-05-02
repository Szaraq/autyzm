package pl.osik.autyzm.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public abstract class AbstractDBTable {

    public static final String COLUMN_ID = "id";

    protected abstract String create();

    protected String createArgumentQuery() {
        StringBuilder out = new StringBuilder();
        for(Map.Entry<String, String> entry : getMap().entrySet()) {
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
        return "create table " + getTableName() + " (" + createArgumentQuery();
    }

    protected abstract LinkedHashMap<String, String> getMap();
    public abstract String getTableName();

    public String[] getColumns() {
        LinkedHashMap<String, String> colTypeMap = getMap();
        String[] out = new String[colTypeMap.size()];
        int count = 0;
        for(Map.Entry<String, String> entry : colTypeMap.entrySet()) {
            out[count++] = entry.getKey();
        }
        return out;
    }

    public String getColumnsToInsertQuery() {
        String out = addCommas(getColumns());
        return out;
    }

    protected SQLiteDatabase getDB() {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        return db;
    }

    public boolean insert(Map<String, Object> data) {
        SQLiteDatabase db = getDB();
        String[] cols = new String[data.size()];
        Object[] params = new Object[data.size()];
        String[] questMarks = new String[data.size()];
        int count = 0;
        for(Map.Entry<String, Object> entry : data.entrySet()) {
            cols[count] = entry.getKey();
            params[count] = entry.getValue();
            questMarks[count] = "?";
            count++;
        }
        String colsQuery = addCommas(cols);
        String valQuery = addCommas(questMarks);
        db.execSQL("INSERT INTO " + getTableName() + "(" + colsQuery + ") VALUES (" + valQuery + ");", params);
        db.close();
        return true;
    }

    private String addCommas(String[] in) {
        StringBuilder out = new StringBuilder();
        for(String col : in) {
            if(col.equals(COLUMN_ID)) continue;         //autoinkrementacja kolumny ID
            out.append(col);
            out.append(",");
        }
        out.setLength(out.length() - 1);
        return out.toString();
    }



    public boolean edit(int id, ContentValues data) {
        SQLiteDatabase db = getDB();
        db.update(getTableName(), data, COLUMN_ID + "= ?", new String[]{String.valueOf(id)});
        db.close();
        return true;
    }

    public boolean insert(ContentValues data) {
        SQLiteDatabase db = getDB();
        db.insert(getTableName(), null, data);
        db.close();
        return true;
    }

    public boolean delete(int id) {
        SQLiteDatabase db = getDB();
        db.delete(getTableName(), COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return true;
    }

    protected static String tableAndColumn(String table, String column) {
        return table + "." + column;
    }

    protected static String createJoin(AbstractDBTable joinedTableClass, String thisTable, String thisKey) {
        return " JOIN " + joinedTableClass.getTableName() + " ON " + tableAndColumn(thisTable, thisKey) + " = " + tableAndColumn(joinedTableClass.getTableName(), joinedTableClass.COLUMN_ID);
    }

}
