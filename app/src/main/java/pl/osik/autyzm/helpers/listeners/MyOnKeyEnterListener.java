package pl.osik.autyzm.helpers.listeners;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by m.osik2 on 2016-05-16.
 */
public class MyOnKeyEnterListener implements View.OnKeyListener {
    private View view;

    public MyOnKeyEnterListener(View view) {
        this.view = view;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER) {
            if(view instanceof Button)
                view.callOnClick();
            else if(view instanceof EditText)
                view.requestFocus();
        }
        return false;
    }
}
