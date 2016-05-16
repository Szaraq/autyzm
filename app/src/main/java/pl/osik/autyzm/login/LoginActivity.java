package pl.osik.autyzm.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import pl.osik.autyzm.sql.Lekcja;
import pl.osik.autyzm.sql.LoadTestData;
import pl.osik.autyzm.sql.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private final String UserDetailsFragmentTag = "UserDetailsFragment";

    @Bind(R.id.linearLayout)
    PercentRelativeLayout container;
    @Bind(R.id.login)
    EditText loginControl;
    @Bind(R.id.password)
    EditText passControl;
    @Bind(R.id.button)
    Button zaloguj;
    @Bind(R.id.error)
    TextView error;
    @Bind(R.id.noweKonto)
    TextView noweKonto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        LoadTestData.load();

        if(User.isFirstLogin()) {
            zalozKonto();
        }

        getSupportActionBar().hide();
        zaloguj.setOnClickListener(this);
        noweKonto.setOnClickListener(this);
        loginControl.setOnKeyListener(this);
        passControl.setOnKeyListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == zaloguj.getId()) {
            logowanie();
        } else if(v.getId() == noweKonto.getId()) {
            zalozKonto();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        error.setVisibility(View.INVISIBLE);
        if(v.getId() == passControl.getId() && keyCode == KeyEvent.KEYCODE_ENTER)
            zaloguj.callOnClick();
        return false;
    }

    private void logowanie() {
        String login = loginControl.getText().toString();
        String pass = passControl.getText().toString();
        if(User.authenticate(login, pass)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        } else {
            error.setVisibility(View.VISIBLE);
        }
    }

    private void zalozKonto() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(UserDetailsFragment.NEW_ACCOUNT, true);
        android.support.v4.app.Fragment fragment = new UserDetailsFragment();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerLayout, fragment, UserDetailsFragmentTag);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = (UserDetailsFragment) manager.findFragmentByTag(UserDetailsFragmentTag);
        if(fragment != null && fragment.isVisible()) {
            manager.beginTransaction()
                    .remove(fragment)
                    .commit();
        } else {
            finish();
            System.exit(0);
        }
    }
}
