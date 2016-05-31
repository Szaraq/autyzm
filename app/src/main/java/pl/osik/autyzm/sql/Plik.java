package pl.osik.autyzm.sql;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import pl.osik.autyzm.helpers.orm.PlikORM;

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

    public static Bitmap getThumbnail(String path) {
        try {
            /*ExifInterface exif = new ExifInterface(path);
            byte[] imageData = exif.getThumbnail();
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);*/
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = calculateInSampleSize(options, 48, 66);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            return bitmap;
        /*} catch (IOException e) {
            Log.d("getThumbnail", e.getMessage());
            return null;*/
        } catch (NullPointerException e) {
            Log.d("getThumbnail", e.getMessage());
            return null;
        }
    }

    /**
     * https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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
                //TODO zastanowić się co po usunięciu pliku - usuwanie modułów, ale tak żeby statystyki zostały
            }
        }
        helper.close();
        cursor.close();
    }

    public static PlikORM getById(int id) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(id)});
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
        int folder = cursor.getInt(cursor.getColumnIndex(COLUMN_FOLDER));
        PlikORM plik = new PlikORM(id, folder, path);
        cursor.close();
        helper.close();
        return plik;
    }
}
