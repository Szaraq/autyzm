package pl.osik.autyzm.lekcje.nowy_modul;

import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.orm.PytanieORM;
import pl.osik.autyzm.lekcje.LekcjeAdapter;
import pl.osik.autyzm.lekcje.LekcjeHelper;
import pl.osik.autyzm.sql.Pytanie;

public class PytaniaActivity extends AppCompatActivity implements View.OnClickListener {

    PytaniaAdapter pytaniaAdapter;

    @Bind(R.id.lista_pytan)
    RecyclerView listaPytan;
    @Bind(R.id.no_plik_container)
    PercentRelativeLayout noPlikContainer;
    @Bind(R.id.buttonAdd)
    Button buttonAdd;
    @Bind(R.id.buttonNext)
    Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pytania);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(R.string.lekcje_nowy_modul_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        buttonAdd.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        noPlikContainer.setOnClickListener(this);
        pytaniaAdapter = new PytaniaAdapter(getLayoutInflater(), this);
        listaPytan.setLayoutManager(new LinearLayoutManager(this));
        listaPytan.setAdapter(pytaniaAdapter);
        LekcjeHelper.setPytaniaList(Pytanie.getPytaniaForModul(LekcjeHelper.getModul().getId()));
        setAddPytanieVisibility(LekcjeHelper.getPytaniaList().size() == 0);
    }

    protected void setAddPytanieVisibility(boolean visible) {
        if(visible) {
            noPlikContainer.setVisibility(View.VISIBLE);
        } else {
            noPlikContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonAdd.getId() || v.getId() == noPlikContainer.getId()) {
            LekcjeHelper.addPytanie("");
            pytaniaAdapter.pytanieAdded = true;
            pytaniaAdapter.refresh();
            setAddPytanieVisibility(false);
        } else if(v.getId() == buttonNext.getId()) {
            LekcjeHelper.commitAll();
            LekcjeHelper.finishPlikActivity();
            finish();
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
}
