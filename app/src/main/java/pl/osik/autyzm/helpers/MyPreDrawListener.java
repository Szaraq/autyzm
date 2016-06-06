package pl.osik.autyzm.helpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import pl.osik.autyzm.dzieci.DzieciDetailsActivity;

/**
 * Created by m.osik2 on 2016-05-11.
 */
public class MyPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

    private static final String NO_PATH = "no_path";

    ImageView photo;
    String path;
    Activity activity;
    int height, width;
    Bitmap thumbnail;

    public MyPreDrawListener(ImageView photo, String path, Activity activity) {
        this(photo, path, activity, FileHelper.THUMB_HEIGHT, FileHelper.THUMB_WIDTH);
    }

    public MyPreDrawListener(ImageView photo, String path, Activity activity, int height, int width) {
        this.photo = photo;
        this.path = path;
        this.activity = activity;
        this.height = height;
        this.width = width;
    }

    public MyPreDrawListener(ImageView photo, Bitmap bitmap, Activity activity) {
        this(photo, NO_PATH, activity, bitmap.getHeight(), bitmap.getWidth());
        thumbnail = bitmap;
    }

    @Override
    public boolean onPreDraw() {
        photo.getViewTreeObserver().removeOnPreDrawListener(this);
        //int finalHeight = photo.getMeasuredHeight();
        if (path != null) {
            //AppHelper.FileManager.placePhoto(activity, photo, path, finalHeight);
            setThumbnail();
            if(thumbnail != null) photo.setImageBitmap(thumbnail);
        } else {
            photo.setImageResource(DzieciDetailsActivity.RESOURCE_NO_PHOTO);
        }
        return true;
    }

    private void setThumbnail() {
        if(thumbnail != null) return;
        thumbnail = FileHelper.rescaleBitmap(path, width, height);
    }
}
