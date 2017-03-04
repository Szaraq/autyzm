package pl.osik.autismemotion.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import pl.osik.autismemotion.helpers.FileHelper;
import pl.osik.autismemotion.helpers.MyApp;
import pl.osik.autismemotion.helpers.orm.ModulORM;
import pl.osik.autismemotion.helpers.orm.PlikORM;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Plik extends AbstractDBTable {

    public static final String TABLE_NAME = "Plik";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_FOLDER = "folder";
    public static final String COLUMN_GHOST = "ghost";
    public static final String COLUMN_THUMB = "thumbnail";
    public static final String COLUMN_NATIVE_BROWSER = "got_by_native_browser";
    public static final String COLUMN_DELETED = "deleted";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_PATH, "TEXT");
        put(COLUMN_FOLDER, "INTEGER");
        put(COLUMN_GHOST, "BOOLEAN");
        put(COLUMN_THUMB, "TEXT");
        put(COLUMN_NATIVE_BROWSER, "BOOLEAN");
        put(COLUMN_DELETED, "BOOLEAN");
    }};

    public static final String THUMB_DIR = MyApp.getContext().getFilesDir() + "/thumbnails/";
    private static final String ASSETS_FILMY_DIR_NAME = "filmy";
    public static final String ASSETS_FILMY_DIR_PATH = MyApp.getContext().getFilesDir() + "/" + ASSETS_FILMY_DIR_NAME + "/";

    @Override
    protected String create() {
        return getCreateStart()
                + createForeignKey(COLUMN_FOLDER, Folder.TABLE_NAME)
                + ")";
    }

    public static void createAssets() {
        Folder.createAssetsFolder();
        new File(ASSETS_FILMY_DIR_PATH).mkdirs();
        try {
            String[] fileNames = MyApp.getContext().getAssets().list(ASSETS_FILMY_DIR_NAME);
            for (String fileName : fileNames) {
                loadAssetFile(fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadAssetFile(String fileName) {
        File f = new File(ASSETS_FILMY_DIR_PATH +fileName);
        if (!f.exists()) try {
            InputStream is = MyApp.getContext().getAssets().open("filmy/" + fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) { throw new RuntimeException(e); }
        PlikORM plik = insertAssetToDb(fileName);
        FileHelper.createThumbnail(plik);
    }

    private static PlikORM insertAssetToDb(String fileName) {
        ContentValues data = new ContentValues();
        data.put(COLUMN_PATH, ASSETS_FILMY_DIR_PATH + fileName);
        data.put(COLUMN_FOLDER, Folder.ASSETS_FILMY_FOLDER_ID);
        data.put(COLUMN_GHOST, 0);
        data.put(COLUMN_NATIVE_BROWSER, 0);
        data.put(COLUMN_DELETED, 0);
        int id = (int) new Plik().insert(data);
        return new PlikORM(
                id,
                data.getAsInteger(COLUMN_FOLDER),
                data.getAsString(COLUMN_PATH),
                data.getAsBoolean(COLUMN_GHOST),
                null,
                data.getAsBoolean(COLUMN_NATIVE_BROWSER),
                data.getAsBoolean(COLUMN_DELETED));
    }

    @Override
    protected LinkedHashMap<String, String> getMap() {
        return colTypeMap;
    }
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public static String getName(String path, boolean gotByNative) {
        if(gotByNative) {
            return FileHelper.getNativeFileName(path);
        } else {
            File f = new File(path);
            return f.getName();
        }
    }

    public static void cleanDeletedFiles() {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
            boolean gotByNative = cursor.getInt(cursor.getColumnIndex(COLUMN_NATIVE_BROWSER)) == 1;
            boolean exists = exists(path, gotByNative);
            boolean ghost = cursor.getInt(cursor.getColumnIndex(COLUMN_GHOST)) == 1;
            if(!exists && !ghost) {
                hideFile(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)), true);
            } else if(exists && ghost) {
                hideFile(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)), false);
            }
            Lekcja.cleanLekcje();
        }
        helper.close();
        cursor.close();
    }

    private static boolean exists(String path, boolean gotByNative) {
        boolean exists = true;
        try {
            if(path == null) {
                return false;
            }
            if(gotByNative) {
                MyApp.getContext().getContentResolver().openFileDescriptor(Uri.parse(path), "r");
            } else {
                exists = new File(path).exists();
            }
        } catch (FileNotFoundException e) {
            exists = false;
        }
        return exists;
    }

    public static PlikORM getById(int id, boolean ghostFilter, boolean deletedFilter) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?" + queryForGhost(ghostFilter, true) + queryForDeleted(deletedFilter, true);
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
        int folder = cursor.getInt(cursor.getColumnIndex(COLUMN_FOLDER));
        boolean ghost = cursor.getInt(cursor.getColumnIndex(COLUMN_GHOST)) == 1;
        boolean gotByNative = cursor.getInt(cursor.getColumnIndex(COLUMN_NATIVE_BROWSER)) == 1;
        boolean deleted = cursor.getInt(cursor.getColumnIndex(COLUMN_DELETED)) == 1;
        String thumb = cursor.getString(cursor.getColumnIndex(COLUMN_THUMB));
        PlikORM plik = new PlikORM(id, folder, path, ghost, thumb, gotByNative, deleted);
        cursor.close();
        helper.close();
        return plik;
    }

    public static void hideFile(int id, boolean hide) {
        setGhost(id, hide);
        ArrayList<ModulORM> moduly = Modul.getModulyForPlik(id, false);
        for (ModulORM modul : moduly) {
            Modul.setGhost(modul.getId(), hide);
        }
    }

    public static void setGhost(int id, boolean set) {
        Plik p = new Plik();
        ContentValues data = new ContentValues();
        data.put(COLUMN_GHOST, set);
        p.edit(id, data);
    }

    public static void setDeleted(int id, boolean set) {
        Plik p = new Plik();
        ContentValues data = new ContentValues();
        data.put(COLUMN_DELETED, set);
        p.edit(id, data);
    }

    private static String queryForGhost(boolean addQuery, boolean multipleQuery) {
        if(addQuery) return (multipleQuery ? " AND " : "") + tableAndColumn(TABLE_NAME, COLUMN_GHOST) + " = 0"; else return "";
    }

    public static String queryForDeleted(boolean addQuery, boolean multipleQuery) {
        if(addQuery) return (multipleQuery ? " AND " : "") + tableAndColumn(TABLE_NAME, COLUMN_DELETED) + " = 0"; else return "";
    }

    public static ArrayList<PlikORM> getPlikiInFolder(int idFolderu, boolean ghostFilter, boolean deletedFilter) {
        ArrayList<PlikORM> out = new ArrayList<>();
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_FOLDER + " = ?" + queryForGhost(ghostFilter, true) + queryForDeleted(deletedFilter, true);
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idFolderu)});
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(Plik.COLUMN_ID));
            int folder  = cursor.getInt(cursor.getColumnIndex(COLUMN_FOLDER));
            String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
            boolean ghost = cursor.getInt(cursor.getColumnIndex(COLUMN_GHOST)) == 1;
            boolean gotByNative = cursor.getInt(cursor.getColumnIndex(COLUMN_NATIVE_BROWSER)) == 1;
            boolean deleted = cursor.getInt(cursor.getColumnIndex(COLUMN_DELETED)) == 1;
            String thumb = cursor.getString(cursor.getColumnIndex(COLUMN_THUMB));
            PlikORM temp = new PlikORM(id, folder, path, ghost, thumb, gotByNative, deleted);
            out.add(temp);
        }
        Collections.sort(out);
        helper.close();
        cursor.close();

        return out;
    }

    public static void saveThumb(int id, String fileName) {
        Plik p = new Plik();
        ContentValues data = new ContentValues();
        data.put(COLUMN_THUMB, fileName);
        p.edit(id, data);
    }

    public static String getThumbAbsolutePath(int id) {
        PlikORM plik = getById(id, false, false);
        return getThumbAbsolutePath(plik);
    }

    public static String getThumbAbsolutePath(PlikORM plik) {
        if(plik.getThumb() == null || plik.getThumb().length() == 0) FileHelper.createThumbnail(plik);
        return THUMB_DIR + plik.getThumb();
    }

    public long insert(ContentValues data) {
        PlikORM plik = getByPath(data.getAsString(COLUMN_PATH), true, false);
        long out = 0;
        if(plik == null) {
            DBHelper helper = DBHelper.getInstance();
            SQLiteDatabase db = helper.getDBRead();
            out = db.insert(getTableName(), null, data);
            helper.close();
        } else {
            setDeleted(plik.getId(), false);
            out = plik.getId();
        }
        return out;
    }

    public static PlikORM getByPath(String path, boolean ghostFilter, boolean deletedFilter) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME
                + createJoin(new Folder(), TABLE_NAME, COLUMN_FOLDER)
                + " WHERE " + COLUMN_PATH + " = ?"
                + " AND " + tableAndColumn(Folder.TABLE_NAME, Folder.COLUMN_USER) + " = " + User.getCurrentId()
                + queryForGhost(ghostFilter, true) + queryForDeleted(deletedFilter, true);
        Cursor cursor = db.rawQuery(query, new String[]{path});
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        int folder = cursor.getInt(cursor.getColumnIndex(COLUMN_FOLDER));
        boolean ghost = cursor.getInt(cursor.getColumnIndex(COLUMN_GHOST)) == 1;
        boolean gotByNative = cursor.getInt(cursor.getColumnIndex(COLUMN_NATIVE_BROWSER)) == 1;
        boolean deleted = cursor.getInt(cursor.getColumnIndex(COLUMN_DELETED)) == 1;
        String thumb = cursor.getString(cursor.getColumnIndex(COLUMN_THUMB));
        PlikORM plik = new PlikORM(id, folder, path, ghost, thumb, gotByNative, deleted);
        cursor.close();
        helper.close();
        return plik;
    }

    @Override
    public boolean delete(int id) {
        setDeleted(id, true);
        return true;
    }
}
