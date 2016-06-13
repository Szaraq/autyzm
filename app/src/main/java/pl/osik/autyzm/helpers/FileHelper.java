package pl.osik.autyzm.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import pl.osik.autyzm.helpers.orm.PlikORM;
import pl.osik.autyzm.sql.Plik;
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by m.osik2 on 2016-06-03.
 */

public class FileHelper {
    public static final int THUMB_WIDTH = 48;
    public static final int THUMB_HEIGHT = 66;
    public final static int RESCALE_PROPORTIONALLY = -1;

    public static Bitmap getThumbnail(String path) {
        FileTypes type = getType(path);
        if(type == FileTypes.PHOTO) {
            return rescaleBitmap(path, THUMB_WIDTH, THUMB_HEIGHT);
        } else if(type == FileTypes.VIDEO) {
            FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
            retriever.setDataSource(path);
            Bitmap bitmap = retriever.getFrameAtTime(10);
            retriever.release();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outWidth = bitmap.getWidth();
            options.outHeight = bitmap.getHeight();
            int scale = calculateInSampleSize(options, THUMB_WIDTH, THUMB_HEIGHT);
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / scale, bitmap.getHeight() / scale, false);
            return bitmap;
        } else {
            return null;
        }
    }

    public static Bitmap getThumbnailFromStorage(int id) {
        PlikORM plik = Plik.getById(id, false);
        Bitmap out = null;
        String thumbPath = plik.getThumb();
        if(thumbPath == null || thumbPath.length() == 0) {
            out = createThumbnail(plik);
        } else {
            String path = Plik.THUMB_DIR + thumbPath;
            out = BitmapFactory.decodeFile(path);
        }
        return out;
    }

    public static Bitmap createThumbnail(PlikORM plik) {
        Bitmap out;
        out = getThumbnail(plik.getPath());
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String fileName = "thumb_" + plik.getShortName(10, false) + "_" + sdf.format(cal.getTime()) + ".jpeg";
        try {
            File file = new File(Plik.THUMB_DIR);
            file.mkdirs();
            FileOutputStream writer = new FileOutputStream(Plik.THUMB_DIR + fileName);
            out.compress(Bitmap.CompressFormat.JPEG, 100, writer);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Plik.saveThumb(plik.getId(), fileName);
        plik.setThumb(fileName);
        return out;
    }

    public static Bitmap rescaleBitmap(String path, int reqWidth, int reqHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            return bitmap;
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

    public static FileTypes getType(File file) {
        return getType(file.getPath());
    }

    public static FileTypes getType(String path) {
        String ext = getExtension(path);
        if(Arrays.asList(FileManager.EXTENSION_ARRAY_PHOTO).contains(ext)) return FileTypes.PHOTO;
        if(Arrays.asList(FileManager.EXTENSION_ARRAY_VIDEO).contains(ext)) return FileTypes.VIDEO;
        return FileTypes.UNSUPPORTED_TYPE;
    }

    public static String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) extension = fileName.substring(i+1);
        return extension;
    }

    public static String removeExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) return fileName.substring(0, i);
        return fileName;
    }

    public static class FileManager {

        public static final int PICK_IMAGE = 9351;
        public static final int RESIZE_TO_SCREEN = -1;
        public static final String[] EXTENSION_ARRAY_PHOTO = new String[] {"bmp", "jpg", "jpeg", "gif", "tif", "tiff", "png"};
        public static final String[] EXTENSION_ARRAY_VIDEO = new String[] {"avi", "mpg", "mpeg", "3gp", "mov", "mp4"};

        public static void pickPhoto(Activity activity, String[] extensions) {
            Intent intent = new Intent(activity, FilePickerActivity.class);
            if(extensions.length > 0) {
                ArrayList<String> extList = new ArrayList<String>(Arrays.asList(extensions));
                intent.putExtra(FilePickerActivity.EXTRA_ACCEPTED_FILE_EXTENSIONS, extList);
            }
            activity.startActivityForResult(intent, PICK_IMAGE);
        }

        public static void placePhoto(Activity activity, ImageView imgView, String path) {
            placePhoto(activity, imgView, path, imgView);
        }

        public static void placePhoto(Activity activity, ImageView imgView, String path, View resizeTo) {
            placePhoto(activity, imgView, path, resizeTo.getHeight());
        }

        public static void placePhoto(Activity activity, ImageView imgView, String path, int resizeHeight) {
            //Bitmap bitmap = BitmapFactory.decodeFile(path);

            /*int resizeWidth = (int) (((double) resizeHeight / (double) bitmap.getHeight()) * (double) bitmap.getWidth());
            bitmap = Bitmap.createScaledBitmap(bitmap, resizeWidth, resizeHeight, true);*/
            Bitmap bitmap;
            if(resizeHeight == RESIZE_TO_SCREEN) {
                int[] size = AppHelper.getScreenSize();
                bitmap = rescaleBitmap(path, size[0], size[1]);
            } else {
                bitmap = rescaleBitmap(path, 0, resizeHeight);
            }
            Glide.with(imgView.getContext())
                    .load("")
                    .placeholder(new BitmapDrawable(bitmap))
                    .into(imgView);
        }
    }

    public enum FileTypes {
        UNSUPPORTED_TYPE,
        PHOTO,
        VIDEO;
    }
}