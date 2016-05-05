package pl.osik.autyzm.sql;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
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

    public static Bitmap getThumbnail(Activity activity, String path) {
        try {
            ExifInterface exif = new ExifInterface(path);
            byte[] imageData = exif.getThumbnail();
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        } catch (IOException e) {
            Log.d("getThumbnail", e.getMessage());
            return null;
        } catch (NullPointerException e) {
            Log.d("getThumbnail", e.getMessage());
            return null;
        }
    }

    public static void cleanDeletedFiles() {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        Plik p = new Plik();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
            File file = new File(path);
            if(!file.exists()) {
                p.delete(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                //TODO Po dodaniu PlikInstance wyrzucić jeszcze z tej tabeli
            }
        }
    }
}
