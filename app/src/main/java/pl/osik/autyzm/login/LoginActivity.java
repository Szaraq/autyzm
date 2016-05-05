package pl.osik.autyzm.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.main.MainActivity;
import pl.osik.autyzm.R;
import pl.osik.autyzm.sql.DBHelper;
import pl.osik.autyzm.sql.LoadTestData;
import pl.osik.autyzm.sql.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    //TODO First Login

    @Bind(R.id.linearLayout)
    LinearLayout linearLayout;
    @Bind(R.id.login)
    EditText loginControl;
    @Bind(R.id.password)
    EditText passControl;
    @Bind(R.id.button)
    Button button;
    @Bind(R.id.error)
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        LoadTestData.load();

        if(User.isFirstLogin()) {
            Intent intent = new Intent(this, UserDetailsActivity.class);
            startActivity(intent);
        }

        getSupportActionBar().hide();
        button.setOnClickListener(this);
        loginControl.setOnKeyListener(this);
        passControl.setOnKeyListener(this);
    }

    @Override
    public void onClick(View v) {
        String login = loginControl.getText().toString();
        String pass = passControl.getText().toString();
        if(User.authenticate(login, pass)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            error.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        error.setVisibility(View.INVISIBLE);
        return false;
    }
}
