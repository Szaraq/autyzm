package pl.osik.autyzm.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.shadowviewhelper.ShadowProperty;
import com.wangjie.shadowviewhelper.ShadowViewHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by m.osik2 on 2016-04-22.
 */
public class AppHelper {

    private final static String SALT = "A%2LmD47";
    private static String today = "";
    private static Context context = MyApp.getContext();

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

    public static void addShadow(Context context, View view, float dy) {
        ShadowProperty shadowProperty = new ShadowProperty();
        shadowProperty.setShadowColor(0x77000000)
                .setShadowDy(ABTextUtil.dip2px(context, dy))
                .setShadowRadius(1);
        ShadowViewHelper.bindShadowHelper(shadowProperty, view);
    }

    public static String getToday() {
        if(today.length() == 0) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            today = sdf.format(cal.getTime());
        }
        return today;
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    /**
     * This value will not collide with ID values generated at build time by aapt for R.id.
     * Źródło: Android API >= 21
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     *
     * @return int[0] -> width<br>
     *     int[1] -> height
     */
    public static int[] getScreenSize() {
        WindowManager wm = (WindowManager) MyApp.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int[] out = new int[2];
        out[0] = size.x;
        out[1] = size.y;
        return out;
    }

    public static boolean canDeviceMakeCall() {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
            Log.d("canDeviceMakeCall", "To urządzenie nie jest telefonem");
            return false;
        } else {
            Log.d("canDeviceMakeCall", "To urządzenie jest telefonem");
            return true;
        }
    }

}

