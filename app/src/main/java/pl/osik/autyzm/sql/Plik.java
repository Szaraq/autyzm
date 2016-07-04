package pl.osik.autyzm.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import pl.osik.autyzm.helpers.FileHelper;
import pl.osik.autyzm.helpers.MyApp;
import pl.osik.autyzm.helpers.orm.ModulORM;
import pl.osik.autyzm.helpers.orm.PlikORM;

/**
 * Created by m.osik2 on 2016-04-20.
 */
public class Plik extends AbstractDBTable {
    //TODO Wytestować clean na unghostowanie

    public static final String TABLE_NAME = "Plik";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_FOLDER = "folder";
    public static final String COLUMN_GHOST = "ghost";
    public static final String COLUMN_THUMB = "thumbnail";

    protected static final LinkedHashMap<String, String> colTypeMap = new LinkedHashMap<String, String>() {{
        put(COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(COLUMN_PATH, "TEXT");
        put(COLUMN_FOLDER, "INTEGER");
        put(COLUMN_GHOST, "BOOLEAN");
        put(COLUMN_THUMB, "TEXT");
    }};

    public static final String THUMB_DIR = MyApp.getContext().getFilesDir() + "/thumbnails/";
    public static final String ASSETS_FILMY_DIR = MyApp.getContext().getFilesDir() + "/filmy/";

    public static final ArrayList<String> assetsList = new ArrayList<String>() {{
        add("obrzydzenie.mp4");
        add("smutek.mp4");
        add("strach.mp4");
        add("strach2.mp4");
        add("zdziwienie.mp4");
    }};

    @Override
    protected String create() {
        return getCreateStart()
                + createForeignKey(COLUMN_FOLDER, Folder.TABLE_NAME)
                + ")";
    }

    public static void createAssets() {
        Folder.createAssetsFolder();
        new File(ASSETS_FILMY_DIR).mkdirs();
        for (String fileName : assetsList) {
            loadAssetFile(fileName);
        }
    }

    private static void loadAssetFile(String fileName) {
        File f = new File(ASSETS_FILMY_DIR+fileName);
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
        insertAssetToDb(fileName);
    }

    private static void insertAssetToDb(String fileName) {
        ContentValues data = new ContentValues();
        data.put(COLUMN_PATH, ASSETS_FILMY_DIR+fileName);
        data.put(COLUMN_FOLDER, Folder.ASSETS_FILMY_FOLDER_ID);
        data.put(COLUMN_GHOST, 0);
        new Plik().insert(data);
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

    public static void cleanDeletedFiles() {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
            File file = new File(path);
            boolean ghost = cursor.getInt(cursor.getColumnIndex(COLUMN_GHOST)) == 1;
            if(!file.exists() && !ghost) {
                hideFile(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)), true);
            } else if(file.exists() && ghost) {
                hideFile(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)), false);
            }
            Lekcja.cleanLekcje();
        }
        helper.close();
        cursor.close();
    }

    public static PlikORM getById(int id, boolean ghostFilter) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?" + queryForGhost(ghostFilter, true);
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
        int folder = cursor.getInt(cursor.getColumnIndex(COLUMN_FOLDER));
        boolean ghost = cursor.getInt(cursor.getColumnIndex(COLUMN_GHOST)) == 1;
        String thumb = cursor.getString(cursor.getColumnIndex(COLUMN_THUMB));
        PlikORM plik = new PlikORM(id, folder, path, ghost, thumb);
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

    private static String queryForGhost(boolean addQuery, boolean multipleQuery) {
        if(addQuery) return (multipleQuery ? " AND " : "") + tableAndColumn(TABLE_NAME, COLUMN_GHOST) + " = 0"; else return "";
    }

    public static ArrayList<PlikORM> getPlikiInFolder(int idFolderu, boolean ghostFilter) {
        ArrayList<PlikORM> out = new ArrayList<>();
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_FOLDER + " = ?" + queryForGhost(ghostFilter, true);
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idFolderu)});
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(Plik.COLUMN_ID));
            int folder  = cursor.getInt(cursor.getColumnIndex(COLUMN_FOLDER));
            String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
            boolean ghost = cursor.getInt(cursor.getColumnIndex(COLUMN_GHOST)) == 1;
            String thumb = cursor.getString(cursor.getColumnIndex(COLUMN_THUMB));
            PlikORM temp = new PlikORM(id, folder, path, ghost, thumb);
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
        PlikORM plik = getById(id, false);
        return getThumbAbsolutePath(plik);
    }

    public static String getThumbAbsolutePath(PlikORM plik) {
        if(plik.getThumb() == null || plik.getThumb().length() == 0) FileHelper.createThumbnail(plik);
        return THUMB_DIR + plik.getThumb();
    }
}
