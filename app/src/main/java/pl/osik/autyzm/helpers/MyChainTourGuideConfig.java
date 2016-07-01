package pl.osik.autyzm.helpers;

import android.support.annotation.StringRes;
import android.view.View;

/**
 * Created by m.osik2 on 2016-07-01.
 */
public class MyChainTourGuideConfig {
    String description;
    View view;
    int gravity;

    public MyChainTourGuideConfig(@StringRes int descriptionRes, View view, int gravity) {
        description = MyApp.getContext().getString(descriptionRes);
        this.view = view;
        this.gravity = gravity;
    }
}
