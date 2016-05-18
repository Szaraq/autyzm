package pl.osik.autyzm.lekcje.nowy_modul;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.Serializable;
import java.security.PublicKey;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.OperationsEnum;
import pl.osik.autyzm.lekcje.LekcjeHelper;

public class PlikActivity extends AppCompatActivity implements View.OnClickListener, Serializable {

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
        if(LekcjeHelper.getOperacja() == OperationsEnum.DODAWANIE) changeViewToAdd();
        buttonAdd.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
    }

    private void changeViewToAdd() {
        buttonNext.setClickable(false);
        buttonNext.setTextColor(getResources().getColor(R.color.colorPrimaryRipple));
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
            //TODO Add file onclicklistener
            plikView.setImageResource(R.drawable.ic_play);
            //TODO co z nazwą modułu?
            LekcjeHelper.getModul().setName("Test");
            LekcjeHelper.getModul().setPlik(1);
            changeViewToEdit();
        } else if(v.getId() == buttonNext.getId()) {
            Intent intent = new Intent(this, PytaniaActivity.class);
            LekcjeHelper.setPlikActivity(this);
            startActivity(intent);
        }
    }
}
