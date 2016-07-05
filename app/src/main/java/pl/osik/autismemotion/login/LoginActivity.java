package pl.osik.autismemotion.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.AppHelper;
import pl.osik.autismemotion.helpers.FileHelper;
import pl.osik.autismemotion.helpers.MyApp;
import pl.osik.autismemotion.main.MainActivity;
import pl.osik.autismemotion.sql.User;
import pl.osik.autismemotion.validate.ValidateAuthenticate;
import pl.osik.autismemotion.validate.ValidateCommand;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener, View.OnFocusChangeListener {
    private static final String BACKGROUND_PATH = "file:///android_asset/login_tlo.jpg";

    //TODO FINALLY prawa autorskie do tła: http://wallpapercave.com/w/tTuFP5q

    private String path;
    private ValidateCommand validate;
    private ValidateAuthenticate authenticate;

    @Bind(R.id.containerLayout)
    RelativeLayout containerLayout;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.backgroundImage)
    ImageView backgroundImage;
    @Bind(R.id.logo)
    TextView logo;
    @Bind(R.id.userPhoto)
    CircleImageView userPhoto;
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
        askForPermissions();
        validate = new ValidateCommand();
        authenticate = new ValidateAuthenticate();
        validate.addValidate(passLayout, authenticate);

        //TODO FINALLY Wyrzucić
//        LoadTestData.load();

        if(User.isFirstLogin()) {
            zalozKonto();
        }

        setLogoHeight();
        setBackground();
        getSupportActionBar().hide();
        zaloguj.setOnClickListener(this);
        noweKonto.setOnClickListener(this);
        loginControl.setOnKeyListener(this);
        loginControl.setOnFocusChangeListener(this);
        loginControl.setOnClickListener(this);
        passControl.setOnKeyListener(this);

//        logForTest();
    }

    private void askForPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MyApp.MY_PERMISSIONS_REQUEST_FILES);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case MyApp.MY_PERMISSIONS_REQUEST_FILES:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //NIC
                } else {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setMessage(R.string.permissions_denied)
                            .setTitle(R.string.popup_uwaga)
                            .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            })
                            .setIcon(R.drawable.ic_uwaga);
                    dialog.show();
                }
        }
    }

    private void setLogoHeight() {
        ViewGroup.LayoutParams params = logo.getLayoutParams();
        params.height = (int) (AppHelper.getScreenSize()[1] * 0.3);
        logo.setLayoutParams(params);
    }

    private void setBackground() {
        /*Glide.with(this)
                .load(BACKGROUND_PATH)
                .dontAnimate()
                .into(backgroundImage);*/
        backgroundImage.setColorFilter(Color.argb(50, 0, 0, 0), PorterDuff.Mode.DARKEN);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == zaloguj.getId()) {
            logowanie();
        } else if(v.getId() == noweKonto.getId()) {
            zalozKonto();
        } else if(v.getId() == loginControl.getId()) {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    scrollView.pageScroll(View.FOCUS_DOWN);
                }
            })).start();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        error.setVisibility(View.INVISIBLE);
        if(keyCode == KeyEvent.KEYCODE_ENTER) {
            if(v.getId() == passControl.getId()) zaloguj.callOnClick();
        }
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
            finish();
        }
    }

    private void zalozKonto() {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        intent.putExtra(UserDetailsActivity.NEW_ACCOUNT, true);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
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

    //TODO FINALLY usunąć
    private void logForTest() {
        loginControl.setText("a");
        passControl.setText("p");
        zaloguj.callOnClick();
    }

    @Override
    public void onPause() {
        super.onPause();
        AppHelper.hideKeyboard(containerLayout);
    }
}
