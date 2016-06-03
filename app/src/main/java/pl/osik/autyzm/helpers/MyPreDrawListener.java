package pl.osik.autyzm.helpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import pl.osik.autyzm.dzieci.DzieciDetailsActivity;
import pl.osik.autyzm.sql.Dziecko;
import pl.osik.autyzm.sql.Plik;

/**
 * Created by m.osik2 on 2016-05-11.
 */
public class MyPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

    ImageView photo;
    String path;
    Activity activity;
    int height, width;

    public MyPreDrawListener(ImageView photo, String path, Activity activity) {
        this.photo = photo;
        this.path = path;
        this.activity = activity;
        height = Plik.THUMB_HEIGHT;
        width = Plik.THUMB_WIDTH;
    }

    public MyPreDrawListener(ImageView photo, String path, Activity activity, int height, int width) {
        this.photo = photo;
        this.path = path;
        this.activity = activity;
        this.height = height;
        this.width = width;
    }

    @Override
    public boolean onPreDraw() {
        photo.getViewTreeObserver().removeOnPreDrawListener(this);
        //int finalHeight = photo.getMeasuredHeight();
        if (path != null) {
            //AppHelper.FileManager.placePhoto(activity, photo, path, finalHeight);
            Bitmap thumbnail = Plik.rescaleBitmap(path, width, height);
            if(thumbnail != null) photo.setImageBitmap(thumbnail);
        } else {
            photo.setImageResource(DzieciDetailsActivity.RESOURCE_NO_PHOTO);
        }
        return true;
    }
}
