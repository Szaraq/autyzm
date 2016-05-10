package pl.osik.autyzm.validate;

import android.content.Context;
import android.view.View;

import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.MyApp;

/**
 * Created by m.osik2 on 2016-05-09.
 */
public interface Validate {
    Context context = MyApp.getContext();

    boolean validate(View view);
    String getErrorMsg();
}
