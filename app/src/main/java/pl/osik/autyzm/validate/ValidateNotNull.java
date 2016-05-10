package pl.osik.autyzm.validate;

import android.view.View;
import android.widget.TextView;

import pl.osik.autyzm.R;

/**
 * Created by m.osik2 on 2016-05-09.
 */
public class ValidateNotNull implements Validate {

    @Override
    public boolean validate(View view) {
        TextView mView = (TextView) view;
        if(mView.getText().length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getErrorMsg() {
        return context.getString(R.string.validate_error_notNull);
    }
}
