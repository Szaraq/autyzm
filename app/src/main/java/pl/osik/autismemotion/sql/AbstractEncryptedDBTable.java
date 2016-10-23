package pl.osik.autismemotion.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.Map;

import pl.osik.autismemotion.helpers.CipherHelper;

/**
 * Created by User on 2016-10-22.
 */
public abstract class AbstractEncryptedDBTable extends AbstractDBTable {
    protected static final CipherHelper cipherHelper = CipherHelper.getInstance();

    protected abstract LinkedList<String> getEncryptedColumns();

    public boolean isColumnEncrypted(String columnName) {
        return getEncryptedColumns().contains(columnName);
    }

    @Override
    public boolean insert(Map<String, Object> data) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String[] cols = new String[data.size()];
        Object[] params = new Object[data.size()];
        String[] questMarks = new String[data.size()];
        int count = 0;
        for(Map.Entry<String, Object> entry : data.entrySet()) {
            cols[count] = entry.getKey();
            if(isColumnEncrypted(entry.getKey())) {
                params[count] = cipherHelper.encrypt(entry.getValue().toString());
            } else {
                params[count] = entry.getValue();
            }
            questMarks[count] = "?";
            count++;
        }
        String colsQuery = addCommas(cols);
        String valQuery = addCommas(questMarks);
        db.execSQL("INSERT INTO " + getTableName() + "(" + colsQuery + ") VALUES (" + valQuery + ");", params);
        helper.close();
        return true;
    }

    @Override
    public long insert(ContentValues data) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        data = encrypt(data);
        long out = db.insert(getTableName(), null, data);
        helper.close();
        return out;
    }

    @Override
    public boolean edit(int id, ContentValues data) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        data = encrypt(data);
        db.update(getTableName(), data, COLUMN_ID + "= ?", new String[]{String.valueOf(id)});
        helper.close();
        return true;
    }

    protected ContentValues encrypt(ContentValues data) {
        for (String column : getEncryptedColumns()) {
            if(data.containsKey(column) && data.getAsString(column) != null)
                data.put(column, cipherHelper.encrypt(data.getAsString(column)));
        }
        return data;
    }

    protected String safeEncrypt(String column, String text) {
        if(!isColumnEncrypted(column)) return text;
        return cipherHelper.encrypt(text);
    }

    protected String safeDecrypt(String column, String text) {
        if(!isColumnEncrypted(column)) return text;
        return cipherHelper.decrypt(text);
    }

    protected Map<String, Object> decrypt(Map<String, Object> data) {
        for (String column : getEncryptedColumns()) {
            if (data.containsKey(column) && data.get(column) != null)
                data.put(column, cipherHelper.decrypt(data.get(column).toString()));
        }
        return data;
    }

    protected ContentValues decrypt(ContentValues data) {
        for (String column : getEncryptedColumns()) {
            if (data.containsKey(column) && data.getAsString(column) != null)
                data.put(column, cipherHelper.decrypt(data.getAsString(column)));
        }
        return data;
    }

    protected String getFromCursor(Cursor cursor, String column) {
        String value = cursor.getString(cursor.getColumnIndex(column));
        if(value == null) return value;
        return safeDecrypt(column, value);
    }
}
