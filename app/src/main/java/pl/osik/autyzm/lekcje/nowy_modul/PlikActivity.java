package pl.osik.autyzm.lekcje.nowy_modul;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.FilePickerActivity;
import pl.osik.autyzm.helpers.OperationsEnum;
import pl.osik.autyzm.helpers.orm.PlikORM;
import pl.osik.autyzm.lekcje.LekcjeHelper;
import pl.osik.autyzm.multimedia.PickerActivity;
import pl.osik.autyzm.multimedia.PlikiAdapter;
import pl.osik.autyzm.sql.Dziecko;
import pl.osik.autyzm.sql.Plik;

public class PlikActivity extends AppCompatActivity implements View.OnClickListener, Serializable {

    public final static int NO_FILE = -1;

    @Bind(R.id.plik_view)
    ImageView plikView;
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
            PlikORM plik = Plik.getById(LekcjeHelper.getModul().getPlik());
            plikView.setImageBitmap(Plik.getThumbnail(plik.getPath()));
        }
        buttonAdd.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
    }

    private void changeViewToAdd() {
        buttonNext.setClickable(false);
        buttonNext.setTextColor(getResources().getColor(R.color.colorPrimaryDisabled));
        buttonAdd.setText(getString(R.string.button_add));
    }

    private void changeViewToEdit() {
        buttonNext.setClickable(true);
        buttonNext.setTextColor(getResources().getColor(R.color.colorPrimary));
        buttonAdd.setText(getString(R.string.button_edit));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonAdd.getId()) {
            Intent intent = new Intent(this, PickerActivity.class);
            startActivityForResult(intent, AppHelper.FileManager.PICK_IMAGE);
        } else if(v.getId() == buttonNext.getId()) {
            Intent intent = new Intent(this, PytaniaActivity.class);
            LekcjeHelper.setPlikActivity(this);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppHelper.FileManager.PICK_IMAGE) {
                File file = new File(data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));
                plikView.setImageBitmap(Plik.getThumbnail(file.getPath()));
                LekcjeHelper.getModul().setName(file.getName());
                LekcjeHelper.getModul().setPlik(data.getIntExtra(PlikORM.EXTRA_PLIK_ID, NO_FILE));
                changeViewToEdit();
            }
        }
    }
}
