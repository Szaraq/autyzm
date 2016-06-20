package pl.osik.autyzm.dzieci;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.FileHelper;
import pl.osik.autyzm.helpers.FilePickerActivity;
import pl.osik.autyzm.helpers.MyApp;
import pl.osik.autyzm.helpers.OperationsEnum;
import pl.osik.autyzm.helpers.listeners.MyOnKeyEnterListener;
import pl.osik.autyzm.sql.Dziecko;
import pl.osik.autyzm.sql.User;
import pl.osik.autyzm.validate.ValidateCommand;
import pl.osik.autyzm.validate.ValidateNotNull;

public class DzieciDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int DATE_PICKER_CODE = 1178;
    public static final int RESOURCE_NO_PHOTO = R.drawable.ic_no_photo;

    HashMap<String, EditText> all;
    int id;
    OperationsEnum operacja;
    ValidateCommand validate = new ValidateCommand();

    private String photoPath;
    private Calendar calendar = Calendar.getInstance();
    private EditText dateChosenFor;
    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            putDate(year, monthOfYear, dayOfMonth);
        }
    };

    @Bind(R.id.containerLayout)
    CoordinatorLayout containerLayout;
    @Bind(R.id.app_bar)
    AppBarLayout appBar;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.imieLayout)
    TextInputLayout imieLayout;
    @Bind(R.id.imie)
    EditText imie;
    @Bind(R.id.nazwiskoLayout)
    TextInputLayout nazwiskoLayout;
    @Bind(R.id.nazwisko)
    EditText nazwisko;
    @Bind(R.id.data_urodzenia)
    EditText dataUrodzenia;
    @Bind(R.id.rozpoczecie)
    EditText rozpoczecie;
    @Bind(R.id.notatki)
    EditText notatki;
    @Bind(R.id.imie_ojca)
    EditText imieOjca;
    @Bind(R.id.nazwisko_ojca)
    EditText nazwiskoOjca;
    @Bind(R.id.telefon_ojcaLayout)
    TextInputLayout telefonOjcaLayout;
    @Bind(R.id.telefon_ojca)
    EditText telefonOjca;
    @Bind(R.id.telefon_ojca_view_container)
    LinearLayout telefonOjcaViewContainer;
    @Bind(R.id.telefon_ojca_label)
    TextView telefonOjcaLabel;
    @Bind(R.id.telefon_ojca_view)
    TextView telefonOjcaView;
    @Bind(R.id.telefon_ojca_icon)
    ImageView telefonOjcaIcon;
    @Bind(R.id.imie_matki)
    EditText imieMatki;
    @Bind(R.id.nazwisko_matki)
    EditText nazwiskoMatki;
    @Bind(R.id.telefon_matkiLayout)
    TextInputLayout telefonMatkiLayout;
    @Bind(R.id.telefon_matki)
    EditText telefonMatki;
    @Bind(R.id.telefon_matki_view_container)
    LinearLayout telefonMatkiViewContainer;
    @Bind(R.id.telefon_matki_label)
    TextView telefonMatkiLabel;
    @Bind(R.id.telefon_matki_view)
    TextView telefonMatkiView;
    @Bind(R.id.telefon_matki_icon)
    ImageView telefonMatkiIcon;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.photo)
    ImageView photo;
    @Bind(R.id.spaceFiller)
    LinearLayout spaceFiller;
    @Bind(R.id.zapiszButton)
    Button button;

    private Menu menu;

    private HashMap<String, String> dziecko;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dzieci_details);
        ButterKnife.bind(this);
        all = new HashMap<String, EditText>() {{
            put(Dziecko.COLUMN_IMIE, imie);
            put(Dziecko.COLUMN_NAZWISKO, nazwisko);
            put(Dziecko.COLUMN_DATAURODZENIA, dataUrodzenia);
            put(Dziecko.COLUMN_DATAWPROWADZENIA, rozpoczecie);
            put(Dziecko.COLUMN_NOTATKI, notatki);
            put(Dziecko.COLUMN_OJCIECIMIE, imieOjca);
            put(Dziecko.COLUMN_OJCIECNAZWISKO, nazwiskoOjca);
            put(Dziecko.COLUMN_OJCIECTELEFON, telefonOjca);
            put(Dziecko.COLUMN_MATKAIMIE, imieMatki);
            put(Dziecko.COLUMN_MATKANAZWISKO, nazwiskoMatki);
            put(Dziecko.COLUMN_MATKATELEFON, telefonMatki);
        }};

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();

        operacja = (OperationsEnum) bundle.getSerializable(DzieciAdapter.BUNDLE_SWITCH_OPERACJA);

        /** Switch operacji begin **/

        /* Edycja + Show */
        if(operacja != OperationsEnum.DODAWANIE) {
            id = bundle.getInt(Dziecko.COLUMN_ID);
            dziecko = Dziecko.getDzieckoById(id);
            getSupportActionBar().setTitle(dziecko.get(Dziecko.COLUMN_IMIE) + " " + dziecko.get(Dziecko.COLUMN_NAZWISKO));
            populate();
        }

        /* Dodawanie + Edycja */
        if(operacja != OperationsEnum.SHOW) {
            addValidations();
            AppHelper.setHeightForSpaceFiller(spaceFiller);
        }

        /* Pojedyncze operacje */
        if(operacja == OperationsEnum.EDYCJA) {
            //nic?
        } else if(operacja == OperationsEnum.SHOW) {
            blockEditTexts();
            if(AppHelper.canDeviceMakeCall()) {
                enableCalling(telefonMatkiIcon, telefonMatkiView, telefonMatkiLabel, telefonMatkiViewContainer);
                enableCalling(telefonOjcaIcon, telefonOjcaView, telefonOjcaLabel, telefonOjcaViewContainer);
            }
            button.setVisibility(View.GONE);
        } else if(operacja == OperationsEnum.DODAWANIE) {
            getSupportActionBar().setTitle(R.string.dziecko_dodaj_title);
            imie.requestFocus();
            telefonOjca.setOnKeyListener(new MyOnKeyEnterListener(imieMatki));
            Calendar cal = Calendar.getInstance();
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.forLanguageTag("pl-PL"));
            rozpoczecie.setText(df.format(cal.getTime()));
            fab.setVisibility(View.GONE);
        }
        /** END **/
        button.setOnClickListener(this);
        fab.setOnClickListener(this);
        dataUrodzenia.setOnClickListener(this);
        rozpoczecie.setOnClickListener(this);
    }

    private void enableCalling(ImageView icon, TextView text, TextView label, LinearLayout container) {
        if(text.length() > 0) {
            PhoneCallOnClickListener phoneCallOnClickListenerMatki = new PhoneCallOnClickListener(this, text.getText().toString().trim());
            text.setOnClickListener(phoneCallOnClickListenerMatki);
            icon.setOnClickListener(phoneCallOnClickListenerMatki);
        } else {
            text.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
            container.setVisibility(View.GONE);
        }
    }

    private void setPhoto(@Nullable String path) {
        if(path == null || !(new File(path)).exists()) {
            photo.setImageResource(RESOURCE_NO_PHOTO);
            menu.findItem(R.id.deletePhoto).setVisible(false);
            menu.findItem(R.id.changePhoto).setTitle(R.string.dzieci_details_dodaj_zdjecie);
        } else {
            Glide.with(this)
                    .load(path)
                    .centerCrop()
                    .into(photo);
            menu.findItem(R.id.deletePhoto).setVisible(true);
            menu.findItem(R.id.changePhoto).setTitle(R.string.dzieci_details_zmien_zdjecie);
        }
    }

    private void addValidations() {
        validate.addValidate(new View[]{imieLayout, nazwiskoLayout}, new ValidateNotNull());
    }

    private void populate() {
        for(Map.Entry<String, EditText> entry : all.entrySet()) {
            entry.getValue().setText(dziecko.get(entry.getKey()));
        }
        telefonMatkiView.setText(dziecko.get(Dziecko.COLUMN_MATKATELEFON));
        telefonOjcaView.setText(dziecko.get(Dziecko.COLUMN_OJCIECTELEFON));
    }

    private void blockEditTexts() {
        telefonMatki.setVisibility(View.GONE);
        telefonMatkiLayout.setVisibility(View.GONE);
        telefonMatkiViewContainer.setVisibility(View.VISIBLE);
        telefonMatkiView.setVisibility(View.VISIBLE);
        telefonMatkiLabel.setVisibility(View.VISIBLE);
        telefonMatkiIcon.setVisibility(View.VISIBLE);
        telefonOjca.setVisibility(View.GONE);
        telefonOjcaLayout.setVisibility(View.GONE);
        telefonOjcaViewContainer.setVisibility(View.VISIBLE);
        telefonOjcaView.setVisibility(View.VISIBLE);
        telefonOjcaLabel.setVisibility(View.VISIBLE);
        telefonOjcaIcon.setVisibility(View.VISIBLE);

        for(Map.Entry<String, EditText> entry : all.entrySet()) {
            if(entry.getValue().getText().length() == 0) {
                ((View) entry.getValue().getParent()).setVisibility(View.GONE);
                entry.getValue().setVisibility(View.GONE);
            } else {
                entry.getValue().setEnabled(false);
                entry.getValue().setBackground(null);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.changePhoto:
                FileHelper.FileManager.pickPhoto(DzieciDetailsActivity.this, FileHelper.FileManager.EXTENSION_ARRAY_PHOTO);
                return true;
            case R.id.deletePhoto:
                AlertDialog.Builder dialog = new AlertDialog.Builder(DzieciDetailsActivity.this);
                dialog.setMessage(MyApp.getContext().getString(R.string.message_photo_do_usunięcia))
                        .setTitle(R.string.popup_uwaga)
                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Dziecko d = new Dziecko();
                                d.changePhoto(id, null);
                                setPhoto(null);
                            }
                        })
                        .setNegativeButton(R.string.button_anuluj, null)
                        .setIcon(R.drawable.ic_uwaga);
                dialog.show();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Zapisz lub Statystyki oraz przekierowanie do setDate dla daty urodzenia i rozpoczecia */
    @Override
    public void onClick(View v) {
        if(v.getId() == dataUrodzenia.getId() || v.getId() == rozpoczecie.getId()) {
            setDate(v);
            return;
        }
        Dziecko d = new Dziecko();
        if(operacja == OperationsEnum.SHOW || (operacja == OperationsEnum.EDYCJA && v.getId() == fab.getId())) {            //jeżeli jest show (wtedy button nie ma) LUB jeżeli jest edycja i przyciśnięty fab
            //Idź do statystyk
            Intent intent = new Intent(this, DzieciStatisticsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Dziecko.TABLE_NAME, dziecko);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if(operacja == OperationsEnum.EDYCJA) {                                                          //jeżeli jest edycja i przyciśnięty button (fab obsłużony już w poprzednim if)
            //Edytuj wpis
            if(validate.doValidateAll()) {
                ContentValues data = new ContentValues();
                for(Map.Entry<String, EditText> entry : all.entrySet()) {
                    data.put(entry.getKey(), entry.getValue().getText().toString());
                }
                if(d.edit(id, data)) {
                    AppHelper.showMessage(containerLayout, R.string.message_dziecko_edytowane);
                    onBackPressed();
                }
            }
        } else if(operacja == OperationsEnum.DODAWANIE) {                                                   //jeżeli jest dodawanie (wtedy fab nie ma)
            //Dodaj wpis
            if(validate.doValidateAll()) {
                ContentValues data = new ContentValues();
                for(Map.Entry<String, EditText> entry : all.entrySet()) {
                    data.put(entry.getKey(), entry.getValue().getText().toString());
                }
                data.put(Dziecko.COLUMN_USER, User.getCurrentId());
                data.put(Dziecko.COLUMN_PHOTO, photoPath);
                d.insert(data);
                AppHelper.showMessage(containerLayout, R.string.message_dziecko_dodane);
                onBackPressed();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FileHelper.FileManager.PICK_IMAGE) {
                String path = data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH);
                photoPath = path;
                Dziecko.changePhoto(id, path);
                setPhoto(path);
            }
        }
    }

    public void setDate(View view) {
        dateChosenFor = (EditText) view;
        showDialog(DATE_PICKER_CODE);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DATE_PICKER_CODE) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(this, dateListener, year, month, day);
        }
        return null;
    }

    private void putDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.forLanguageTag("pl-PL"));
        String put = df.format(cal.getTime());
        dateChosenFor.setText(put);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dzieci_details_menu, menu);
        adjustImageToRatio();
        if(dziecko != null) setPhoto(dziecko.get(Dziecko.COLUMN_PHOTO)); else setPhoto(null);
        return true;
    }

    private void adjustImageToRatio() {
        ViewGroup.LayoutParams params = photo.getLayoutParams();
        params.height = AppHelper.getHeightForRatio(AppHelper.getScreenSize()[0], 3, 2);
        photo.setLayoutParams(params);

        ViewGroup.LayoutParams appBarParams = appBar.getLayoutParams();
        appBarParams.height = AppHelper.getHeightForRatio(AppHelper.getScreenSize()[0], 3, 2);
        appBar.setLayoutParams(appBarParams);
    }
}

class PhoneCallOnClickListener implements View.OnClickListener {

    DzieciDetailsActivity activity;
    String phoneNumber;

    public PhoneCallOnClickListener(DzieciDetailsActivity activity, String phoneNumber) {
        this.activity = activity;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void onClick(View v) {
        if(activity.checkCallingOrSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            String number = "tel:" + phoneNumber;
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
            activity.startActivity(callIntent);
            Log.d("PhoneCallOnClickListen", "Właśnie dzwonię pod numer: " + number);
        } else {
            AppHelper.showMessage(activity.containerLayout, R.string.app_no_calling_permission);
        }
    }
}