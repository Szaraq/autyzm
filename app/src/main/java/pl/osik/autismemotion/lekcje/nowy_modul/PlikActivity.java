package pl.osik.autismemotion.lekcje.nowy_modul;

import android.content.Intent;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.AppHelper;
import pl.osik.autismemotion.helpers.FileHelper;
import pl.osik.autismemotion.helpers.FilePickerActivity;
import pl.osik.autismemotion.helpers.orm.PlikORM;
import pl.osik.autismemotion.lekcje.LekcjeHelper;
import pl.osik.autismemotion.multimedia.PickerActivity;
import pl.osik.autismemotion.sql.Plik;
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
            plikView.setImageBitmap(FileHelper.getThumbnailFromStorage(plik.getThumb()));
        }
        buttonAdd.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        tourGuide = AppHelper.makeTourGuide(this, R.string.tourGuide_modul_dodaj_plik, Gravity.TOP, null);
        if(tourGuide != null) tourGuide.playOn(buttonAdd);
    }

    private void changeViewToAdd() {
        buttonNext.setTextColor(getResources().getColor(R.color.colorPrimaryDisabled));
        buttonAdd.setText(getString(R.string.button_add));

        plikView.setVisibility(View.GONE);
    }

    private void changeViewToEdit() {
        buttonNext.setTextColor(getResources().getColor(R.color.colorPrimary));
        buttonAdd.setText(getString(R.string.button_edit));
        plikView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonAdd.getId() || v.getId() == noPlikContainer.getId()) {
            Intent intent = new Intent(this, PickerActivity.class);
            startActivityForResult(intent, FileHelper.FileManager.PICK_IMAGE_DEFAULT);
        } else if(v.getId() == buttonNext.getId()) {
            if(LekcjeHelper.getModul().getPlik() == 0) {
                AppHelper.makeToolTip(PlikActivity.this, buttonNext, Gravity.TOP, R.string.lekcje_modul_plik_noPlik_tooltip);
            } else {
                Intent intent = new Intent(this, PytaniaActivity.class);
                LekcjeHelper.setPlikActivity(this);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FileHelper.FileManager.PICK_IMAGE_DEFAULT) {
                String path = data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH);
                plikView.setImageBitmap(FileHelper.getThumbnailFromStorage(data.getIntExtra(PlikORM.EXTRA_PLIK_ID, -1)));
                LekcjeHelper.getModul().setName(Plik.getName(path, data.getBooleanExtra(PlikORM.EXTRA_NATIVE, true)));
                LekcjeHelper.getModul().setPlik(data.getIntExtra(PlikORM.EXTRA_PLIK_ID, NO_FILE));
                changeViewToEdit();
                if(tourGuide != null) tourGuide.cleanUp();
            }
        }
    }
}
