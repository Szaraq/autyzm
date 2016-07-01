package pl.osik.autyzm.lekcje.nowy_modul;

import android.content.Intent;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.Serializable;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.FileHelper;
import pl.osik.autyzm.helpers.FilePickerActivity;
import pl.osik.autyzm.helpers.orm.PlikORM;
import pl.osik.autyzm.lekcje.LekcjeHelper;
import pl.osik.autyzm.multimedia.PickerActivity;
import pl.osik.autyzm.sql.Plik;
import tourguide.tourguide.TourGuide;

public class PlikActivity extends AppCompatActivity implements View.OnClickListener, Serializable {

    public final static int NO_FILE = -1;
    TourGuide tourGuide;

    @Bind(R.id.plik_view)
    ImageView plikView;
    @Bind(R.id.no_plik_container)
    PercentRelativeLayout noPlikContainer;
    @Bind(R.id.no_plik_view)
    ImageView noPlikView;
    @Bind(R.id.no_plik_text)
    TextView noPlikText;
    @Bind(R.id.buttonAdd)
    Button buttonAdd;
    @Bind(R.id.buttonNext)
    Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plik);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(R.string.lekcje_nowy_modul_title);
        if(LekcjeHelper.getModul().getPlik() == 0) {
            changeViewToAdd();
        } else {
            PlikORM plik = Plik.getById(LekcjeHelper.getModul().getPlik(), true);
            plikView.setImageBitmap(FileHelper.getThumbnail(plik.getPath()));
        }
        buttonAdd.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        tourGuide = AppHelper.makeTourGuide(this, R.string.tourGuide_modul_dodaj_plik, Gravity.TOP, null).playOn(buttonAdd);
    }

    private void changeViewToAdd() {
        buttonNext.setClickable(false);
        buttonNext.setTextColor(getResources().getColor(R.color.colorPrimaryDisabled));
        buttonAdd.setText(getString(R.string.button_add));

        //TODO FINALLY Na razie komentujemy, bo nie wiadomo co z tym będzie. Finalnie wyrzucić i wywalić z layoutu.
        //buttonAdd.setVisibility(View.GONE);
        //noPlikContainer.setVisibility(View.VISIBLE);
        //noPlikContainer.setOnClickListener(this);
        plikView.setVisibility(View.GONE);
    }

    private void changeViewToEdit() {
        //TODO FINALLY j.w.
        //buttonAdd.setVisibility(View.VISIBLE);
        //noPlikContainer.setVisibility(View.GONE);
        buttonNext.setClickable(true);
        buttonNext.setTextColor(getResources().getColor(R.color.colorPrimary));
        buttonAdd.setText(getString(R.string.button_edit));
        plikView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonAdd.getId() || v.getId() == noPlikContainer.getId()) {
            Intent intent = new Intent(this, PickerActivity.class);
            startActivityForResult(intent, FileHelper.FileManager.PICK_IMAGE);
        } else if(v.getId() == buttonNext.getId()) {
            Intent intent = new Intent(this, PytaniaActivity.class);
            LekcjeHelper.setPlikActivity(this);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FileHelper.FileManager.PICK_IMAGE) {
                File file = new File(data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));
                plikView.setImageBitmap(FileHelper.getThumbnail(file.getPath()));
                LekcjeHelper.getModul().setName(FileHelper.removeExtension(file.getName()));
                LekcjeHelper.getModul().setPlik(data.getIntExtra(PlikORM.EXTRA_PLIK_ID, NO_FILE));
                changeViewToEdit();
                if(tourGuide != null) tourGuide.cleanUp();
            }
        }
    }
}
