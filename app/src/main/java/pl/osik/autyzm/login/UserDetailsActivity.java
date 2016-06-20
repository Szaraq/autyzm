package pl.osik.autyzm.login;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.FileHelper;
import pl.osik.autyzm.helpers.FilePickerActivity;
import pl.osik.autyzm.helpers.MyApp;
import pl.osik.autyzm.helpers.listeners.MyOnKeyEnterListener;
import pl.osik.autyzm.main.MainActivity;
import pl.osik.autyzm.sql.Dziecko;
import pl.osik.autyzm.sql.User;
import pl.osik.autyzm.validate.ValidateCommand;
import pl.osik.autyzm.validate.ValidateExistsInDatabase;
import pl.osik.autyzm.validate.ValidateNotNull;

public class UserDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String NEW_ACCOUNT = "newAccount";
    private static final int RESOURCE_NO_PHOTO = R.drawable.ic_user;
    private LinkedHashMap<String, EditText> editTextHashMap;
    private String photoPath = null;
    private ValidateCommand validate = new ValidateCommand();
    HashMap<String, String> userData;

    private static boolean newAccount;

    private Menu menu;

    @Bind(R.id.app_bar)
    AppBarLayout appBar;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.userPhoto)
    ImageView userPhoto;
    @Bind(R.id.imieLayout)
    TextInputLayout imieLayout;
    @Bind(R.id.imie)
    EditText imie;
    @Bind(R.id.nazwiskoLayout)
    TextInputLayout nazwiskoLayout;
    @Bind(R.id.nazwisko)
    EditText nazwisko;
    @Bind(R.id.userLayout)
    TextInputLayout userLayout;
    @Bind(R.id.user)
    EditText user;
    @Bind(R.id.hasloLayout)
    TextInputLayout hasloLayout;
    @Bind(R.id.haslo)
    EditText haslo;
    @Bind(R.id.haslo_hint)
    TextView hasloHint;
    @Bind(R.id.spaceFiller)
    LinearLayout spaceFiller;
    @Bind(R.id.button)
    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.userDetails_title);

        newAccount = getIntent().getBooleanExtra(NEW_ACCOUNT, true);
        editTextHashMap = new LinkedHashMap<String, EditText>() {{
            put(User.COLUMN_IMIE, imie);
            put(User.COLUMN_NAZWISKO, nazwisko);
            put(User.COLUMN_LOGIN, user);
        }};
        if(newAccount) {
            hasloHint.setVisibility(View.GONE);
        } else {
            populate();
        }
        button.setOnClickListener(this);
        userPhoto.setOnClickListener(this);

        addValidations();
        setEditTextsEnterOrder();
        AppHelper.setHeightForSpaceFiller(spaceFiller);
    }

    private void adjustImageToRatio() {
        ViewGroup.LayoutParams params = userPhoto.getLayoutParams();
        params.height = AppHelper.getHeightForRatio(AppHelper.getScreenSize()[0], 3, 2);
        userPhoto.setLayoutParams(params);

        ViewGroup.LayoutParams appBarParams = appBar.getLayoutParams();
        appBarParams.height = AppHelper.getHeightForRatio(AppHelper.getScreenSize()[0], 3, 2);
        appBar.setLayoutParams(appBarParams);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dzieci_details_menu, menu);
        adjustImageToRatio();
        setPhoto(User.getCurrentPhotoPath());
        return true;
    }

    private void setEditTextsEnterOrder() {
        imie.setOnKeyListener(new MyOnKeyEnterListener(nazwisko));
        nazwisko.setOnKeyListener(new MyOnKeyEnterListener(user));
        user.setOnKeyListener(new MyOnKeyEnterListener(haslo));
        haslo.setOnKeyListener(new MyOnKeyEnterListener(button));
    }

    private void addValidations() {
        validate.addValidate(new TextInputLayout[]{imieLayout, nazwiskoLayout, userLayout}, new ValidateNotNull());
        if(newAccount) {
            validate.addValidate(userLayout, new ValidateExistsInDatabase(new User(), User.COLUMN_LOGIN))
                    .addValidate(hasloLayout, new ValidateNotNull());
        } else {
            validate.addValidate(userLayout, new ValidateExistsInDatabase(new User(), User.COLUMN_LOGIN, userData.get(User.COLUMN_LOGIN)));
        }
    }

    private void populate() {
        userData = User.getCurrentData();

        for (Map.Entry<String, EditText> entry : editTextHashMap.entrySet()) {
            entry.getValue().setText(userData.get(entry.getKey()));
        }
    }

    private void setPhoto(@Nullable String path) {
        if(path == null || !(new File(path)).exists()) {
            userPhoto.setImageResource(RESOURCE_NO_PHOTO);
            menu.findItem(R.id.deletePhoto).setVisible(false);
            menu.findItem(R.id.changePhoto).setTitle(R.string.dzieci_details_dodaj_zdjecie);
        } else {
            Glide.with(this)
                    .load(path)
                    .centerCrop()
                    .into(userPhoto);
            menu.findItem(R.id.deletePhoto).setVisible(true);
            menu.findItem(R.id.changePhoto).setTitle(R.string.dzieci_details_zmien_zdjecie);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == button.getId()) {
            saveData();
        } else if(v.getId() == userPhoto.getId()) {
            editPhoto();
        }
    }

    private void editPhoto() {
        FileHelper.FileManager.pickPhoto(this, FileHelper.FileManager.EXTENSION_ARRAY_PHOTO);
    }

    private void saveData() {
        if(validate.doValidateAll()) {
            ContentValues data = new ContentValues();
            for (Map.Entry<String, EditText> entry : editTextHashMap.entrySet()) {
                data.put(entry.getKey(), entry.getValue().getText().toString());
            }
            String newHaslo = haslo.getText().toString();
            if(newHaslo.length() > 0) data.put(User.COLUMN_PASS, newHaslo);
            data.put(User.COLUMN_PHOTO, photoPath);
            User u = new User();
            if(newAccount) {
                u.insert(data);
                User.authenticate(data.getAsString(User.COLUMN_LOGIN), haslo.getText().toString());
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                u.edit(User.getCurrentId(), data);
                MainActivity.instance.setUserInDrawerMenu();
            }
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FileHelper.FileManager.PICK_IMAGE) {
                photoPath = data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH);
                setPhoto(photoPath);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.changePhoto:
                editPhoto();
                return true;
            case R.id.deletePhoto:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage(MyApp.getContext().getString(R.string.message_photo_do_usunięcia))
                        .setTitle(R.string.popup_uwaga)
                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                photoPath = null;
                                setPhoto(null);
                            }
                        })
                        .setNegativeButton(R.string.button_anuluj, null)
                        .setIcon(R.drawable.ic_uwaga);
                dialog.show();
                return true;
            case R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
