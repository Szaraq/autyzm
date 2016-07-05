package pl.osik.autismemotion.helpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import pl.osik.autismemotion.dzieci.DzieciDetailsActivity;

/**
 * Created by m.osik2 on 2016-05-11.
 */
public class MyPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

    private static final String NO_PATH = "no_path";

    final ImageView photo;
    final String path;
    final Activity activity;
    Bitmap thumbnail;

    public MyPreDrawListener(ImageView photo, String path, Activity activity) {
        this(photo, path, activity, FileHelper.THUMB_HEIGHT, FileHelper.THUMB_WIDTH);
    }

    @Deprecated
    public MyPreDrawListener(ImageView photo, String path, Activity activity, int height, int width) {
        this.photo = photo;
        this.path = path;
        this.activity = activity;
    }

    public MyPreDrawListener(ImageView photo, Bitmap bitmap, Activity activity) {
        this.photo = photo;
        this.path = null;
        this.thumbnail = bitmap;
        this.activity = activity;
    }

    @Override
    public boolean onPreDraw() {
        photo.getViewTreeObserver().removeOnPreDrawListener(this);
        if (path != null) {
            Glide.with(activity)
                    .load(path)
                    .centerCrop()
                    .into(photo);
        } else if(thumbnail != null) {
            Glide.with(activity)
                    .load("")
                    .placeholder(new BitmapDrawable(thumbnail))
                    .centerCrop()
                    .into(photo);
        } else {
            photo.setImageResource(DzieciDetailsActivity.RESOURCE_NO_PHOTO);
        }
        return true;
    }
}
