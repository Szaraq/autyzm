package pl.osik.autyzm.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.shadowviewhelper.ShadowProperty;
import com.wangjie.shadowviewhelper.ShadowViewHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import pl.osik.autyzm.R;
import pl.osik.autyzm.dzieci.DzieciDetailsActivity;
import pl.osik.autyzm.sql.FirstUse;
import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * Created by m.osik2 on 2016-04-22.
 */
public class AppHelper {

    private final static String SALT = "A%2LmD47";
    private static String today = "";
    private static final Context context = MyApp.getContext();

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

    public static int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, MyApp.getContext().getResources().getDisplayMetrics());
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

    /**
     * Zwraca wysokość dla podanej szerokości dostosowaną do podanych proporcji
     * (np. dla proporcji 16:9, ratioWidth = 16; ratioHeight = 9)
     *
     * @param width szerokość, do jakiej dostosować zwracaną wysokość
     * @param ratioWidth proporcja szerokości
     * @param ratioHeight proporcja wysokości
     * @return wysokość dla podanej szerokości dostosowana do podanych proporcji
     */
    public static int getHeightForRatio(int width, int ratioWidth, int ratioHeight) {
        return ratioHeight * width / ratioWidth;
    }

    /**
     * Ustawia minimalną wysokość dla LinearLayoutu, pełniącego funkcję wypełniacza przestrzeni dla klawiatury ekranowej.
     * Bez tego klawiatura przesłania dolne kontrolki formularza. Zakłada się, że klawiatura zajmuje połowę ekranu.
     *
     * @param spaceFiller LinearLayout, pełniący funkcję wypełniacza przestrzeni
     */
    public static void setHeightForSpaceFiller(LinearLayout spaceFiller) {
        spaceFiller.setMinimumHeight((int) (getScreenSize()[1] * 0.4));
    }

    public static void showMessage(View container, String text, int length) {
        Snackbar.make(container, text, length).show();

    }

    public static void showMessage(View container, String text) {
        showMessage(container, text, Snackbar.LENGTH_SHORT);
    }

    public static void showMessage(View container, @StringRes int resString) {
        showMessage(container, MyApp.getContext().getResources().getString(resString));
    }

    @Nullable
    public static TourGuide makeTourGuide(Activity activity, @StringRes int descriptionRes, int gravity, @Nullable Fragment fragment) {
        return makeTourGuide(activity, activity.getString(descriptionRes), gravity, fragment);
    }

    private static TourGuide tourGuide;

    @Nullable
    public static TourGuide makeTourGuide(Activity activity, String description, int gravity, @Nullable Fragment fragment) {
        Class classToCheck = (fragment == null ? activity.getClass() : fragment.getClass());
        //Uwaga, poniższe ma na celu odróżnić sytuację Dodawanie od Show w DzieciDetailsActivity
        if(classToCheck == DzieciDetailsActivity.class && ((DzieciDetailsActivity) activity).operacja == OperationsEnum.SHOW) classToCheck = DzieciDetailsActivity.classToReplaceShow;
        if(!FirstUse.isFirstUsed(classToCheck)) return null;
        FirstUse.setUsed(classToCheck);

        Overlay overlay = new Overlay();
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tourGuide.cleanUp();
            }
        });

        return tourGuide = TourGuide.init(activity).with(TourGuide.Technique.Click)
                .setPointer(new Pointer())
                .setToolTip(new ToolTip().setDescription(description).setGravity(gravity))
                .setOverlay(overlay);
    }

    @Nullable
    public static ChainTourGuide makeTourGuideSequence(Activity activity, MyChainTourGuideConfig[] tourGuides, @Nullable Fragment fragment) {
        Class classToCheck = (fragment == null ? activity.getClass() : fragment.getClass());
        if(!FirstUse.isFirstUsed(classToCheck)) return null;
        FirstUse.setUsed(classToCheck);

        ChainTourGuide[] chainTourGuides = new ChainTourGuide[tourGuides.length];
        int i = 0;

        for (MyChainTourGuideConfig config : tourGuides) {
            ChainTourGuide temp = ChainTourGuide.init(activity)
                    .setToolTip(new ToolTip().setDescription(config.description).setGravity(config.gravity))
                    .setOverlay(new Overlay())
                    .playLater(config.view);
            chainTourGuides[i++] = temp;
        }

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(chainTourGuides)
                .setDefaultPointer(new Pointer())
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .setDefaultOverlay(new Overlay())
                .build();
        return ChainTourGuide.init(activity).playInSequence(sequence);
    }

    public static void makeToolTip(Activity activity, View view, int gravity, @StringRes int text) {
        new SimpleTooltip.Builder(activity)
                .anchorView(view)
                .text(activity.getString(text))
                .gravity(gravity)
                .animated(false)
                .backgroundColor(activity.getResources().getColor(R.color.colorPrimary))
                .textColor(activity.getResources().getColor(R.color.colorAppBackground))
                .arrowColor(activity.getResources().getColor(R.color.colorPrimary))
                .transparentOverlay(true)
                .build()
                .show();
    }
}

