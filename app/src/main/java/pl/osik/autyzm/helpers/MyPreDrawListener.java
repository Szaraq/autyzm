package pl.osik.autyzm.helpers;

import android.app.Activity;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import pl.osik.autyzm.dzieci.DzieciDetailsActivity;
import pl.osik.autyzm.sql.Dziecko;

/**
 * Created by m.osik2 on 2016-05-11.
 */
public class MyPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

    ImageView photo;
    String path;
    Activity activity;

    public MyPreDrawListener(ImageView photo, String path, Activity activity) {
        this.photo = photo;
        this.path = path;
        this.activity = activity;
    }

    @Override
    public boolean onPreDraw() {
        photo.getViewTreeObserver().removeOnPreDrawListener(this);
        int finalHeight = photo.getMeasuredHeight();
        if (path != null)
            AppHelper.placePhoto(activity, photo, path, finalHeight);
        return true;
    }
}
