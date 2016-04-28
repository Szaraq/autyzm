package pl.osik.autyzm.helpers;

import android.app.FragmentTransaction;
import android.widget.PopupMenu;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import pl.osik.autyzm.MainActivity;
import pl.osik.autyzm.R;
import pl.osik.autyzm.dzieci.DzieciFragment;

/**
 * Created by m.osik2 on 2016-04-22.
 */
public class AppHelper {

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
}