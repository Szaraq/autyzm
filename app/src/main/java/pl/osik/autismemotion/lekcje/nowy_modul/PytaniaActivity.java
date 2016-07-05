package pl.osik.autismemotion.lekcje.nowy_modul;

import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.AppHelper;
import pl.osik.autismemotion.helpers.MyChainTourGuideConfig;
import pl.osik.autismemotion.lekcje.LekcjeHelper;
import pl.osik.autismemotion.sql.Pytanie;
import tourguide.tourguide.ChainTourGuide;

public class PytaniaActivity extends AppCompatActivity implements View.OnClickListener {
    ChainTourGuide tourGuide;
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
        MyChainTourGuideConfig[] tourGuides = new MyChainTourGuideConfig[] {
                new MyChainTourGuideConfig(R.string.tourGuide_modul_dodaj_pytanie, buttonAdd, Gravity.TOP),
                new MyChainTourGuideConfig(R.string.tourGuide_modul_nie_dodawaj_pytania, buttonNext, Gravity.TOP)
        };
        tourGuide = AppHelper.makeTourGuideSequence(this, tourGuides, null);
    }

    protected void setAddPytanieVisibility(boolean visible) {
        //TODO FINALLY Na razie komentujemy, bo nie wiadomo co z tym będzie. Finalnie wyrzucić i wywalić z layoutu.
        /*
        if(visible) {
            noPlikContainer.setVisibility(View.VISIBLE);
        } else {
            noPlikContainer.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonAdd.getId() || v.getId() == noPlikContainer.getId()) {
            LekcjeHelper.addPytanie("");
            pytaniaAdapter.pytanieAdded = true;
            pytaniaAdapter.refresh();
            setAddPytanieVisibility(false);
            if(tourGuide != null) tourGuide.next();
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
