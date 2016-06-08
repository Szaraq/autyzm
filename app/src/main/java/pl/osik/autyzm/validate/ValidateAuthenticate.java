package pl.osik.autyzm.validate;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import pl.osik.autyzm.R;
import pl.osik.autyzm.sql.User;

/**
 * Created by m.osik2 on 2016-06-08.
 */
public class ValidateAuthenticate implements Validate {

    String login, pass;

    public ValidateAuthenticate(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }

    @Override
    public boolean validate(View view) {
        return User.authenticate(login, pass);
    }

    @Override
    public String getErrorMsg() {
        return context.getString(R.string.login_error);
    }
}
