package pl.osik.autyzm.login;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.dzieci.DzieciDetailsActivity;
import pl.osik.autyzm.filters.WithCapitalLetter;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.main.MainActivity;
import pl.osik.autyzm.sql.Dziecko;
import pl.osik.autyzm.sql.User;
import pl.osik.autyzm.validate.ValidateCommand;
import pl.osik.autyzm.validate.ValidateExistsInDatabase;
import pl.osik.autyzm.validate.ValidateNotNull;

public class UserDetailsFragment extends Fragment implements View.OnClickListener {

    public static final String NEW_ACCOUNT = "newAccount";
    private HashMap<String, EditText> editTextHashMap;
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
        editTextHashMap = new HashMap<String, EditText>() {{
            put(User.COLUMN_IMIE, imie);
            put(User.COLUMN_NAZWISKO, nazwisko);
            put(User.COLUMN_LOGIN, user);
        }};

        if(!newAccount) populate();
        button.setOnClickListener(this);
        userPhoto.setOnClickListener(this);
        addValidations();

        return view;
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
        if(userData.get(User.COLUMN_PHOTO) != null)
            AppHelper.placePhoto(this.getActivity(), userPhoto, userData.get(User.COLUMN_PHOTO));
        for (Map.Entry<String, EditText> entry : editTextHashMap.entrySet()) {
            entry.getValue().setText(userData.get(entry.getKey()));
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
        photoPath = AppHelper.pickPhoto(this.getActivity());
        AppHelper.placePhoto(this.getActivity(), userPhoto, photoPath);
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
}
