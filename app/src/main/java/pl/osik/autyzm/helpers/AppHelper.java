package pl.osik.autyzm.helpers;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import pl.osik.autyzm.dzieci.DzieciDetailsActivity;

/**
 * Created by m.osik2 on 2016-04-22.
 */
public class AppHelper {

    private final static String SALT = "A%2LmD47";
    public static final int PICK_IMAGE = 93518;

    public static void setForceIconInPopupMenu(PopupMenu popupMenu) {
        try {
            Class<?> classPopupMenu = Class.forName(popupMenu
                    .getClass().getName());
            Field mPopup = classPopupMenu.getDeclaredField("mPopup");
            mPopup.setAccessible(true);
            Object menuPopupHelper = mPopup.get(popupMenu);
            Class<?> classPopupHelper = Class.forName(menuPopupHelper
                    .getClass().getName());
            Method setForceIcons = classPopupHelper.getMethod(
                    "setForceShowIcon", boolean.class);
            setForceIcons.invoke(menuPopupHelper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String hash(String txt) {
        txt = SALT + txt;
        String out = null;
        try {
            MessageDigest code = MessageDigest.getInstance("MD5");
            byte[] inputBytes = txt.getBytes();
            code.update(inputBytes, 0, txt.length());
            byte[] digest = code.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String pickPhoto(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, PICK_IMAGE);
        return intent.getData().toString();
    }

    public static void placePhoto(Activity activity, ImageView imgView, String path) {
        Uri selectedImage = Uri.parse(path);
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = activity.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imgDecodableString = cursor.getString(columnIndex);
        cursor.close();
        imgView.setImageBitmap(BitmapFactory
                .decodeFile(imgDecodableString));
    }

    public static void changeListItemHeight(LinearLayout listItem) {
        final int NUMBER_OF_ON_THE_SCREEN = 15;
        final int MIN_SIZE = 50;

        DisplayMetrics displayMetrics = listItem.getContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        int newDpHeight = (int) dpHeight / NUMBER_OF_ON_THE_SCREEN;
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newDpHeight < MIN_SIZE ? MIN_SIZE : newDpHeight, listItem.getResources().getDisplayMetrics());
        listItem.getLayoutParams().height = height;
    }
}