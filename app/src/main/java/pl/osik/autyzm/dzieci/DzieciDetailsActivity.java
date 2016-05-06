package pl.osik.autyzm.dzieci;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.OperationsEnum;
import pl.osik.autyzm.sql.Dziecko;
import pl.osik.autyzm.sql.User;

public class DzieciDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    //TODO DatePicker: http://www.tutorialspoint.com/android/android_datepicker_control.htm

    HashMap<String, EditText> all;
    int id;
    OperationsEnum operacja;

    @Bind(R.id.imie)
    EditText imie;
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
    @Bind(R.id.telefon_ojca)
    EditText telefonOjca;
    @Bind(R.id.imie_matki)
    EditText imieMatki;
    @Bind(R.id.nazwisko_matki)
    EditText nazwiskoMatki;
    @Bind(R.id.telefon_matki)
    EditText telefonMatki;
    @Bind(R.id.button)
    Button button;
    @Bind(R.id.photo)
    ImageView photo;
    @Bind(R.id.scrollView)
    ScrollView scrollView;

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
            put(Dziecko.COLUMN_NOTATKI,notatki);
            put(Dziecko.COLUMN_OJCIECIMIE, imieOjca);
            put(Dziecko.COLUMN_OJCIECNAZWISKO, nazwiskoOjca);
            put(Dziecko.COLUMN_OJCIECTELEFON, telefonOjca);
            put(Dziecko.COLUMN_MATKAIMIE, imieMatki);
            put(Dziecko.COLUMN_MATKANAZWISKO, nazwiskoMatki);
            put(Dziecko.COLUMN_MATKATELEFON, telefonMatki);
        }};

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();

        operacja = (OperationsEnum) bundle.getSerializable(DzieciAdapter.BUNDLE_SWITCH_OPERACJA);

        if(operacja != OperationsEnum.DODAWANIE) {
            //Chowanie klawiatury
            scrollView.requestFocus();
            try {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch(NullPointerException exc) { Log.d("keyboardHide", "Klawiatura się nie pojawiła"); }       //Jeżeli klawiatura się nie pojawi to idziemy dalej

            id = bundle.getInt(Dziecko.COLUMN_ID);
            dziecko = Dziecko.getDzieckoById(id);
            getSupportActionBar().setTitle(dziecko.get(Dziecko.COLUMN_IMIE) + " " + dziecko.get(Dziecko.COLUMN_NAZWISKO));
            populate();
            if(dziecko.get(Dziecko.COLUMN_PHOTO) != null) AppHelper.placePhoto(this, photo, dziecko.get(Dziecko.COLUMN_PHOTO));
        }

        if(operacja != OperationsEnum.SHOW) {
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = AppHelper.pickPhoto(DzieciDetailsActivity.this);
                    Log.d("DzieciDetails", path);
                    Dziecko.changePhoto(id, path);
                }
            });
        }

        if(operacja == OperationsEnum.EDYCJA) {
            //nic nowego?
        } else if(operacja == OperationsEnum.SHOW) {
            button.setText(R.string.dzieci_details_button_statystyki);
            blockEditTexts();
        } else if(operacja == OperationsEnum.DODAWANIE) {
            getSupportActionBar().setTitle(R.string.dziecko_dodaj_title);
        }
        button.setOnClickListener(this);

    }

    private void populate() {
        for(Map.Entry<String, EditText> entry : all.entrySet()) {
            entry.getValue().setText(dziecko.get(entry.getKey()));
        }
    }

    private void blockEditTexts() {
        for(Map.Entry<String, EditText> entry : all.entrySet()) {
            entry.getValue().setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        Dziecko d = new Dziecko();
        if(operacja == OperationsEnum.SHOW) {
            Intent intent = new Intent(this, DzieciStatisticsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Dziecko.COLUMN_ID, id);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if(operacja == OperationsEnum.EDYCJA) {
            ContentValues data = new ContentValues();
            for(Map.Entry<String, EditText> entry : all.entrySet()) {
                data.put(entry.getKey(), entry.getValue().getText().toString());
            }
            if(d.edit(id, data)) {
                Toast.makeText(this, R.string.message_dziecko_edytowane, Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        } else if(operacja == OperationsEnum.DODAWANIE) {
            ContentValues data = new ContentValues();
            for(Map.Entry<String, EditText> entry : all.entrySet()) {
                data.put(entry.getKey(), entry.getValue().getText().toString());
            }
            data.put(Dziecko.COLUMN_USER, User.getCurrentId());
            d.insert(data);
            Toast.makeText(this, R.string.message_dziecko_dodane, Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    String imgDecodableString;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppHelper.PICK_IMAGE) {
                AppHelper.placePhoto(this, photo, data.getData().toString());
            }
        }
    }
}