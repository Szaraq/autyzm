package pl.osik.autyzm.helpers;

import android.widget.PopupMenu;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
}
