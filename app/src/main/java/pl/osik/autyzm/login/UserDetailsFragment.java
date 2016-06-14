package pl.osik.autyzm.login;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.FileHelper;
import pl.osik.autyzm.helpers.FilePlacingInterface;
import pl.osik.autyzm.helpers.MyApp;
import pl.osik.autyzm.helpers.MyPreDrawListener;
import pl.osik.autyzm.helpers.listeners.MyOnKeyEnterListener;
import pl.osik.autyzm.main.MainActivity;
import pl.osik.autyzm.sql.Dziecko;
import pl.osik.autyzm.sql.User;
import pl.osik.autyzm.validate.ValidateCommand;
import pl.osik.autyzm.validate.ValidateExistsInDatabase;
import pl.osik.autyzm.validate.ValidateNotNull;

public class UserDetailsFragment extends Fragment implements View.OnClickListener, FilePlacingInterface {

    public static final String NEW_ACCOUNT = "newAccount";
    private static final int RESOURCE_NO_PHOTO = R.drawable.ic_user;
    private LinkedHashMap<String, EditText> editTextHashMap;
    private String photoPath = null;
    private ValidateCommand validate = new ValidateCommand();
    HashMap<String, String> userData;

    private static boolean newAccount;

    @Bind(R.id.userPhoto)
    ImageView userPhoto;
    @Bind(R.id.imie)
    EditText imie;
    @Bind(R.id.nazwisko)
    EditText nazwisko;
    @Bind(R.id.user)
    EditText user;
    @Bind(R.id.haslo)
    EditText haslo;
    @Bind(R.id.button)
    Button button;

    public UserDetailsFragment() {
        // Required empty public constructor
    }

    public static UserDetailsFragment newInstance() {
        UserDetailsFragment fragment = new UserDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);
        ButterKnife.bind(this, view);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.userDetails_title);
        Bundle args = getArguments();
        newAccount = args.getBoolean(NEW_ACCOUNT);
        editTextHashMap = new LinkedHashMap<String, EditText>() {{
            put(User.COLUMN_IMIE, imie);
            put(User.COLUMN_NAZWISKO, nazwisko);
            put(User.COLUMN_LOGIN, user);
        }};
        adjustImageToRatio();
        if(!newAccount) populate();
        button.setOnClickListener(this);
        userPhoto.setOnClickListener(this);

        addValidations();
        setEditTextsEnterOrder();
        setHasOptionsMenu(true);

        return view;
    }

    private void adjustImageToRatio() {
        ViewGroup.LayoutParams params = userPhoto.getLayoutParams();
        params.height = AppHelper.getHeightForRatio(AppHelper.getScreenSize()[0], 16, 9);
        userPhoto.setLayoutParams(params);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dzieci_details_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setEditTextsEnterOrder() {
        imie.setOnKeyListener(new MyOnKeyEnterListener(nazwisko));
        nazwisko.setOnKeyListener(new MyOnKeyEnterListener(user));
        user.setOnKeyListener(new MyOnKeyEnterListener(haslo));
        haslo.setOnKeyListener(new MyOnKeyEnterListener(button));
    }

    private void addValidations() {
        validate.addValidate(new EditText[]{imie, nazwisko, user}, new ValidateNotNull());
        if(newAccount) {
            validate.addValidate(user, new ValidateExistsInDatabase(new User(), User.COLUMN_LOGIN))
                    .addValidate(haslo, new ValidateNotNull());
        } else {
            validate.addValidate(user, new ValidateExistsInDatabase(new User(), User.COLUMN_LOGIN, userData.get(User.COLUMN_LOGIN)));
        }
    }

    private void populate() {
        userData = User.getCurrentData();
        if(userData.get(User.COLUMN_PHOTO) != null) {
            setPhoto(userData.get(User.COLUMN_PHOTO));
        } else setPhoto(null);

        for (Map.Entry<String, EditText> entry : editTextHashMap.entrySet()) {
            entry.getValue().setText(userData.get(entry.getKey()));
        }
    }

    private void setPhoto(@Nullable String path) {
        if(path == null) {
            userPhoto.setImageResource(RESOURCE_NO_PHOTO);
            /*menu.findItem(R.id.deletePhoto).setVisible(false);
            menu.findItem(R.id.changePhoto).setTitle(R.string.dzieci_details_dodaj_zdjecie);*/
        } else {
            Glide.with(this)
                    .load(path)
                    .centerCrop()
                    .into(userPhoto);
            /*menu.findItem(R.id.deletePhoto).setVisible(true);
            menu.findItem(R.id.changePhoto).setTitle(R.string.dzieci_details_zmien_zdjecie);*/
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
        FileHelper.FileManager.pickPhoto(this.getActivity(), FileHelper.FileManager.EXTENSION_ARRAY_PHOTO);
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
            } else {
                u.edit(User.getCurrentId(), data);
                //((MainActivity) getActivity()).setUserInDrawerMenu();
            }
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void placeFile(String path) {
        photoPath = path;
        setPhoto(photoPath);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.changePhoto:
                editPhoto();
                return true;
            case R.id.deletePhoto:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
