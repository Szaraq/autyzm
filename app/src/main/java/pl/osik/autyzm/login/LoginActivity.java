package pl.osik.autyzm.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.inputmethodservice.Keyboard;
import android.support.design.widget.TextInputLayout;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.itextpdf.text.Image;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.FileHelper;
import pl.osik.autyzm.main.MainActivity;
import pl.osik.autyzm.R;
import pl.osik.autyzm.sql.DBHelper;
import pl.osik.autyzm.sql.Lekcja;
import pl.osik.autyzm.sql.LoadTestData;
import pl.osik.autyzm.sql.User;
import pl.osik.autyzm.validate.Validate;
import pl.osik.autyzm.validate.ValidateAuthenticate;
import pl.osik.autyzm.validate.ValidateCommand;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener, View.OnFocusChangeListener {
    private static final String BACKGROUND_PATH = "file:///android_asset/login_tlo.jpg";

    //TODO co jeżeli photo nie istnieje?
    //TODO FINALLY prawa autorskie do tła: http://wallpapercave.com/w/tTuFP5q

    private final String UserDetailsFragmentTag = "UserDetailsFragment";
    private String path;
    private ValidateCommand validate;
    private ValidateAuthenticate authenticate;

    @Bind(R.id.backgroundImage)
    ImageView backgroundImage;
    @Bind(R.id.userPhoto)
    CircleImageView userPhoto;
    @Bind(R.id.loginLayout)
    TextInputLayout loginLayout;
    @Bind(R.id.login)
    EditText loginControl;
    @Bind(R.id.passLayout)
    TextInputLayout passLayout;
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
        validate = new ValidateCommand();
        authenticate = new ValidateAuthenticate();
        validate.addValidate(passLayout, authenticate);

        LoadTestData.load();

        if(User.isFirstLogin()) {
            zalozKonto();
        }

        setBackground();
        getSupportActionBar().hide();
        zaloguj.setOnClickListener(this);
        noweKonto.setOnClickListener(this);
        loginControl.setOnKeyListener(this);
        loginControl.setOnFocusChangeListener(this);
        passControl.setOnKeyListener(this);
    }

    private void setBackground() {
        Glide.with(this)
                .load(BACKGROUND_PATH)
                .into(backgroundImage);
        //backgroundImage.setColorFilter(Color.rgb(123, 123, 123), PorterDuff.Mode.MULTIPLY);
        backgroundImage.setColorFilter(Color.argb(50, 0, 0, 0), PorterDuff.Mode.DARKEN);
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
        authenticate.setCredentials(login, pass);
        if(validate.doValidateAll()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String prevPath = path;
        path = User.getPhotoPathByLogin(((EditText) v).getText().toString());

        if(prevPath != null && path == null) {          //Jak zmieniamy z jakiegoś photo na brak
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.fab_scale_down);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    userPhoto.setImageBitmap(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            userPhoto.startAnimation(anim);
        } else if(path == null) {                       //Bez tego nie można wywołać equals na null object
        } else if(!path.equals(prevPath)) {             //Jak zmieniamy
            Bitmap bitmap = FileHelper.rescaleBitmap(path, userPhoto.getWidth(), userPhoto.getHeight());
            userPhoto.setImageBitmap(bitmap);
            userPhoto.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fab_scale_up));
        }
    }
}
