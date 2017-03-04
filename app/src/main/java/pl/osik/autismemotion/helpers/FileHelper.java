package pl.osik.autismemotion.helpers;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.orm.PlikORM;
import pl.osik.autismemotion.sql.Plik;

/**
 * Created by m.osik2 on 2016-06-03.
 */

public class FileHelper {
    public static final int THUMB_WIDTH = 96;
    public static final int THUMB_HEIGHT = 132;
    public final static int RESCALE_PROPORTIONALLY = -1;

    public static Bitmap buildThumbnail(String path) {
        return buildThumbnail(path, THUMB_WIDTH, THUMB_HEIGHT);
    }

    public static Bitmap buildThumbnail(String path, boolean gotByNative) {
        return buildThumbnail(path, THUMB_WIDTH, THUMB_HEIGHT, gotByNative);
    }

    public static Bitmap buildThumbnail(String path, int width, int height) {
        return buildThumbnail(path, width, height, false);
    }

    public static Bitmap buildThumbnail(String path, int width, int height, boolean gotByNative) {
        FileTypes type = getType(path, gotByNative);
        if(type == FileTypes.PHOTO) {
            return rescaleBitmap(path, width, height);
        } else if(type == FileTypes.VIDEO) {
            /*FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
            retriever.setDataSource(path);
            Bitmap bitmap = retriever.getFrameAtTime(10);
            retriever.release();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outWidth = bitmap.getWidth();
            options.outHeight = bitmap.getHeight();
            int scale = calculateInSampleSize(options, width, height);
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / scale, bitmap.getHeight() / scale, false);
            return bitmap;*/
            Resources res = MyApp.getContext().getResources();
            int id = R.drawable.ic_file_movie;
            Bitmap bmp = getBitmap((VectorDrawable) MyApp.getContext().getResources().getDrawable(id));
            return bmp;
        } else {
            throw new IllegalArgumentException("UNSUPPORTED TYPE is not available here.");
        }
    }

    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getThumbnailFromStorage(int plikId) {
        PlikORM plik = Plik.getById(plikId, false, false);
        Bitmap out = null;
        String thumbPath = plik.getThumb();
        if(thumbPath == null || thumbPath.length() == 0) {
            out = createThumbnail(plik);
        } else {
            out = getThumbnailFromStorage(thumbPath);
        }
        return out;
    }

    public static Bitmap getThumbnailFromStorage(String thumbPath) {
        String path = Plik.THUMB_DIR + thumbPath;
        return BitmapFactory.decodeFile(path);
    }

    public static Bitmap createThumbnail(PlikORM plik) {
        Bitmap out;
        out = buildThumbnail(plik.getPath(), plik.isGotByNative());
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
            ParcelFileDescriptor parcelFileDescriptor =
                    MyApp.getContext().getContentResolver().openFileDescriptor(Uri.parse(path), "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            int width = image.getWidth();
            int height = image.getHeight();
            int proportion = calculateInSampleSize(width, height, reqWidth, reqHeight);
            image = Bitmap.createScaledBitmap(image, width/proportion, height/proportion, false);
            parcelFileDescriptor.close();
            return image;
        } catch(FileNotFoundException e) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                return bitmap;
            } catch (NullPointerException exc) {
                Log.d("buildThumbnail", exc.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        final int height = options.outHeight;
        final int width = options.outWidth;
        return calculateInSampleSize(width, height, reqWidth, reqHeight);
    }

    private static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        // Raw height and width of image
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

    public static FileTypes getType(String path, boolean gotByNative) {
        String ext = getExtension(path, gotByNative);
        if(Arrays.asList(FileManager.EXTENSION_ARRAY_PHOTO).contains(ext)) return FileTypes.PHOTO;
        if(Arrays.asList(FileManager.EXTENSION_ARRAY_VIDEO).contains(ext)) return FileTypes.VIDEO;
        return FileTypes.UNSUPPORTED_TYPE;
    }

    public static String getExtension(String path, boolean gotByNative) {
        String extension = "";
        if(gotByNative) {
            path = getNativeFileName(path);
        }
        int i = path.lastIndexOf('.');
        if (i > 0) extension = path.substring(i+1);
        return extension;
    }

    public static String removeExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) return fileName.substring(0, i);
        return fileName;
    }

    public static String getFilePath(Intent data) {
        final int FLAGS = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Uri uri = data.getData();
        MyApp.getContext().getContentResolver().takePersistableUriPermission(uri, FLAGS);
        return uri.toString();
    }

    public static String getNativeFileName(String path) {
        Uri uri = Uri.parse(path);
        Cursor cursor = MyApp.getContext().getContentResolver()
                .query(uri, null, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        }
        throw new NullPointerException("File Metadata cursor is null");
    }

    public static class FileManager {

        public static final int PICK_IMAGE_DEFAULT = 9351;
        public static final int PICK_IMAGE = 9352;
        public static final int RESIZE_TO_SCREEN = -1;
        public static final String[] EXTENSION_ARRAY_PHOTO = new String[] {"bmp", "jpg", "jpeg", "gif", "tif", "tiff", "png"};
        public static final String[] EXTENSION_ARRAY_VIDEO = new String[] {"avi", "mpg", "mpeg", "3gp", "mov", "mp4"};

        public static void pickPhoto(Activity activity, String[] extensions) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            if(Collections.disjoint(Arrays.asList(EXTENSION_ARRAY_VIDEO), Arrays.asList(extensions))) {
                intent.setType("image/*");
            } else {
                intent.setType("*/*");
            }
            try{
                activity.startActivityForResult(intent, PICK_IMAGE);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                defaultPicker(activity, extensions);
            }
        }

        private static void defaultPicker(Activity activity, String[] extensions) {
            Intent intent = new Intent(activity, FilePickerActivity.class);
            if(extensions.length > 0) {
                ArrayList<String> extList = new ArrayList<String>(Arrays.asList(extensions));
                intent.putExtra(FilePickerActivity.EXTRA_ACCEPTED_FILE_EXTENSIONS, extList);
            }
            activity.startActivityForResult(intent, PICK_IMAGE_DEFAULT);
        }

        public static boolean isFileType(FileTypes type, String extension) {
            String[] extensionsToCompare;
            if(type.equals(FileTypes.PHOTO)) {
                extensionsToCompare = EXTENSION_ARRAY_PHOTO;
            } else if(type.equals(FileTypes.VIDEO)) {
                extensionsToCompare = EXTENSION_ARRAY_VIDEO;
            } else if(type.equals(FileTypes.UNSUPPORTED_TYPE)) {
                return !Arrays.asList(EXTENSION_ARRAY_PHOTO).contains(extension)
                        && !Arrays.asList(EXTENSION_ARRAY_VIDEO).contains(extension);
            } else {
                throw new IllegalArgumentException("There is no implementation for this FileType");
            }
            return Arrays.asList(extensionsToCompare).contains(extension);
        }
    }

    public enum FileTypes {
        UNSUPPORTED_TYPE,
        PHOTO,
        VIDEO
    }
}