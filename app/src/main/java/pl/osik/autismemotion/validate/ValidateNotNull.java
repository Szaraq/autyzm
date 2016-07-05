package pl.osik.autismemotion.validate;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.MyApp;

/**
 * Created by m.osik2 on 2016-05-09.
 */
public class ValidateNotNull implements Validate {

    @Override
    public boolean validate(View view) {
        return getStringFromView(view).length() != 0;
    }

    private Editable getStringFromView(View view) {
        EditText editText = null;
        if(view instanceof TextInputLayout) {
            editText = ((TextInputLayout) view).getEditText();
        } else if(view instanceof EditText) {
            editText = (EditText) view;
        } else {
            try {
                throw new Exception(MyApp.getContext().getString(R.string.exception_wrong_view));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return editText.getText();
    }

    @Override
    public String getErrorMsg() {
        return context.getString(R.string.validate_error_notNull);
    }
}
