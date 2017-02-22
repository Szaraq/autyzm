package pl.osik.autismemotion.dzieci;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.AppHelper;
import pl.osik.autismemotion.helpers.FileHelper;
import pl.osik.autismemotion.helpers.FilePickerActivity;
import pl.osik.autismemotion.helpers.MyApp;
import pl.osik.autismemotion.helpers.OperationsEnum;
import pl.osik.autismemotion.helpers.listeners.MyOnKeyEnterListener;
import pl.osik.autismemotion.sql.Dziecko;
import pl.osik.autismemotion.sql.User;
import pl.osik.autismemotion.validate.ValidateCommand;
import pl.osik.autismemotion.validate.ValidateNotNull;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.TourGuide;

public class DzieciDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final Class classToReplaceShow = DzieciStatisticsActivity.class;
    public static final int DELAY_MILLIS = 500;
    TourGuide tourGuide;
    private static final int DATE_PICKER_CODE = 1178;
    public static final int RESOURCE_NO_PHOTO = R.drawable.ic_no_photo;

    HashMap<String, EditText> all;
    int id;
    public OperationsEnum operacja;
    final ValidateCommand validate = new ValidateCommand();

    private String photoPath;
    final private Calendar calendar = Calendar.getInstance();
    private EditText dateChosenFor;
    final private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            putDate(year, monthOfYear, dayOfMonth);
        }
    };

    @Bind(R.id.containerLayout)
    CoordinatorLayout containerLayout;
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
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
        final Bundle bundle = getIntent().getExtras();

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
                askForPermissions();
            }
            button.setVisibility(View.GONE);
            tourGuide = AppHelper.makeTourGuide(this, R.string.tourGuide_dzieci_details_statistics, Gravity.LEFT, null);
            if(tourGuide != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tourGuide.playOn(fab);
                    }
                }, DELAY_MILLIS);
            }
        } else if(operacja == OperationsEnum.DODAWANIE) {
            getSupportActionBar().setTitle(R.string.dziecko_dodaj_title);
            imie.requestFocus();
            telefonOjca.setOnKeyListener(new MyOnKeyEnterListener(imieMatki));
            final Calendar cal = Calendar.getInstance();
            final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.forLanguageTag("pl-PL"));
            rozpoczecie.setText(df.format(cal.getTime()));
            fab.setVisibility(View.GONE);
            addTourGuideForDodawanie();
        }
        /** END **/
        button.setOnClickListener(this);
        fab.setOnClickListener(this);
        dataUrodzenia.setOnClickListener(this);
        rozpoczecie.setOnClickListener(this);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhoto();
            }
        });
    }

    private void addTourGuideForDodawanie() {
        final Overlay overlay = new Overlay();
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tourGuide != null) tourGuide.cleanUp();
            }
        });
        overlay.setStyle(Overlay.Style.Rectangle);
        tourGuide = AppHelper.makeTourGuide(this, R.string.tourGuide_dzieci_details, Gravity.TOP, null);
        if(tourGuide == null) return;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tourGuide.setOverlay(overlay)
                        .playOn(imieLayout);
            }
        }, DELAY_MILLIS);
        imie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tourGuide != null) tourGuide.cleanUp();
            }
        });
    }

    private void askForPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MyApp.MY_PERMISSIONS_REQUEST_CALL);
        } else {
            enableCalling();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case MyApp.MY_PERMISSIONS_REQUEST_CALL:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    enableCalling();
                else
                    disableCalling();
        }
    }

    private void disableCalling() {
        changeIconToDisableCalling(telefonMatkiIcon);
        changeIconToDisableCalling(telefonOjcaIcon);
    }

    private void changeIconToDisableCalling(final ImageView icon) {
        icon.setColorFilter(getResources().getColor(R.color.colorError));
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppHelper.makeToolTip(DzieciDetailsActivity.this, v, Gravity.END, R.string.dzieci_details_no_permissions_to_call);
            }
        });
    }

    private void enableCalling() {
        enableCalling(telefonMatkiIcon, telefonMatkiView, telefonMatkiLabel, telefonMatkiViewContainer);
        enableCalling(telefonOjcaIcon, telefonOjcaView, telefonOjcaLabel, telefonOjcaViewContainer);
    }

    private void enableCalling(final ImageView icon, final TextView text, final TextView label, final LinearLayout container) {
        if(text.length() > 0) {
            final PhoneCallOnClickListener phoneCallOnClickListenerMatki = new PhoneCallOnClickListener(this, text.getText().toString().trim());
            text.setOnClickListener(phoneCallOnClickListenerMatki);
            icon.setOnClickListener(phoneCallOnClickListenerMatki);
        } else {
            text.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
            container.setVisibility(View.GONE);
        }
    }

    private void setPhoto(@Nullable final String path) {
        try {
            if(path == null) {
                setNoPhoto();
                return;
            }
            MyApp.getContext().getContentResolver().openFileDescriptor(Uri.parse(path), "r");
        } catch (FileNotFoundException e) {
            setNoPhoto();
            return;
        }

        Glide.with(this)
                .load(path)
                .centerCrop()
                .into(photo);
        menu.findItem(R.id.deletePhoto).setVisible(true);
        menu.findItem(R.id.changePhoto).setTitle(R.string.dzieci_details_zmien_zdjecie);
    }

    private void setNoPhoto() {
        photo.setImageResource(RESOURCE_NO_PHOTO);
        menu.findItem(R.id.deletePhoto).setVisible(false);
        menu.findItem(R.id.changePhoto).setTitle(R.string.dzieci_details_dodaj_zdjecie);
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
                changePhoto();
                return true;
            case R.id.deletePhoto:
                final AlertDialog.Builder dialog = new AlertDialog.Builder(DzieciDetailsActivity.this);
                dialog.setMessage(MyApp.getContext().getString(R.string.message_photo_do_usunięcia))
                        .setTitle(R.string.popup_uwaga)
                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Dziecko d = new Dziecko();
                                Dziecko.changePhoto(id, null);
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

    private void changePhoto() {
        FileHelper.FileManager.pickPhoto(DzieciDetailsActivity.this, FileHelper.FileManager.EXTENSION_ARRAY_PHOTO);
    }

    /* Zapisz lub Statystyki oraz przekierowanie do setDate dla daty urodzenia i rozpoczecia */
    @Override
    public void onClick(View v) {
        if(v.getId() == dataUrodzenia.getId() || v.getId() == rozpoczecie.getId()) {
            setDate((EditText) v);
            return;
        }
        final Dziecko d = new Dziecko();
        if(operacja == OperationsEnum.SHOW || (operacja == OperationsEnum.EDYCJA && v.getId() == fab.getId())) {            //jeżeli jest show (wtedy button nie ma) LUB jeżeli jest edycja i przyciśnięty fab
            //Idź do statystyk
            final Intent intent = new Intent(this, DzieciStatisticsActivity.class);
            final Bundle bundle = new Bundle();
            bundle.putSerializable(Dziecko.TABLE_NAME, dziecko);
            intent.putExtras(bundle);
            startActivity(intent);
            if(tourGuide != null) tourGuide.cleanUp();
        } else if(operacja == OperationsEnum.EDYCJA) {                                                          //jeżeli jest edycja i przyciśnięty button (fab obsłużony już w poprzednim if)
            //Edytuj wpis
            if(validate.doValidateAll()) {
                final ContentValues data = new ContentValues();
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
                final ContentValues data = new ContentValues();
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
            if (requestCode == FileHelper.FileManager.PICK_IMAGE_DEFAULT) {
                final String path = data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH);
                photoPath = path;
                Dziecko.changePhoto(id, path);
                setPhoto(path);
            } else if(requestCode == FileHelper.FileManager.PICK_IMAGE) {
                final String path = FileHelper.getFilePath(data);
                photoPath = path;
                Dziecko.changePhoto(id, path);
                setPhoto(path);
            }
        }
    }

    public void setDate(EditText view) {
        dateChosenFor = view;
        showDialog(DATE_PICKER_CODE);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DATE_PICKER_CODE) {
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH) + 1;
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(this, dateListener, year, month, day);
        }
        return null;
    }

    private void putDate(final int year, final int monthOfYear, final int dayOfMonth) {
        final Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);
        final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.forLanguageTag("pl-PL"));
        final String put = df.format(cal.getTime());
        dateChosenFor.setText(put);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dzieci_details_menu, menu);
        adjustImageToRatio();
        if(dziecko != null) setPhoto(dziecko.get(Dziecko.COLUMN_PHOTO)); else setPhoto(null);
        return true;
    }

    private void adjustImageToRatio() {
        final ViewGroup.LayoutParams params = photo.getLayoutParams();
        params.height = AppHelper.getHeightForRatio(AppHelper.getScreenSize()[0], 3, 2);
        photo.setLayoutParams(params);

        final ViewGroup.LayoutParams appBarParams = appBar.getLayoutParams();
        appBarParams.height = AppHelper.getHeightForRatio(AppHelper.getScreenSize()[0], 3, 2);
        appBar.setLayoutParams(appBarParams);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppHelper.hideKeyboard(containerLayout);
    }
}

class PhoneCallOnClickListener implements View.OnClickListener {

    final DzieciDetailsActivity activity;
    final String phoneNumber;

    public PhoneCallOnClickListener(final DzieciDetailsActivity activity, final String phoneNumber) {
        this.activity = activity;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void onClick(View v) {
        if(activity.checkCallingOrSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            final String number = "tel:" + phoneNumber;
            final Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
            activity.startActivity(callIntent);
            Log.d("PhoneCallOnClickListen", "Właśnie dzwonię pod numer: " + number);
        } else {
            AppHelper.showMessage(activity.containerLayout, R.string.app_no_calling_permission);
        }
    }
}