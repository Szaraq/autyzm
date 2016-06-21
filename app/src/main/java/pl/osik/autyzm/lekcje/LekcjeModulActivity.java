package pl.osik.autyzm.lekcje;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.Visibility;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.dzieci.DzieciAdapter;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.MyApp;
import pl.osik.autyzm.helpers.OperationsEnum;
import pl.osik.autyzm.helpers.orm.ModulORM;
import pl.osik.autyzm.lekcje.nowy_modul.PlikActivity;
import pl.osik.autyzm.sql.Folder;
import pl.osik.autyzm.sql.Lekcja;
import pl.osik.autyzm.sql.Pytanie;

public class LekcjeModulActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.containerLayout)
    PercentRelativeLayout containerLayout;
    @Bind(R.id.buttonAddModule)
    Button buttonAdd;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    @Bind(R.id.lista_modulow_container)
    FrameLayout listaModulowContainer;
    @Bind(R.id.brak_modulow)
    TextView brakModulow;
    @Bind(R.id.lista_modulow)
    RecyclerView listaModulow;

    ModulyAdapter modulyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lekcje_modul);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(getText(R.string.lekcje_moduly_title) + LekcjeHelper.getLekcja().getTytul());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        buttonAdd.setOnClickListener(this);
        buttonNext.setOnClickListener(this);

        if(LekcjeHelper.hasModules()) {
            createList();
        } else {
            buttonNext.setClickable(false);
            buttonNext.setTextColor(getResources().getColor(R.color.colorPrimaryDisabled));
            dodajBrakModulowInfo();
        }
    }

    private void createList() {
        modulyAdapter = new ModulyAdapter(getLayoutInflater(), this, LekcjeHelper.getLekcja().getId());
        listaModulow.setLayoutManager(new LinearLayoutManager(this));
        listaModulow.setAdapter(modulyAdapter);
    }

    private void dodajBrakModulowInfo() {
        brakModulow.setVisibility(View.VISIBLE);
        listaModulow.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonAdd.getId())
            dodajModul();
        else if(v.getId() == buttonNext.getId())
            if(checkPytania())
                przejdzDalej();
    }

    private boolean checkPytania() {
        if(Pytanie.getPytaniaForLekcja(LekcjeHelper.getLekcja().getId()).size() > 0) return true;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(R.string.lekcje_modul_brak_pytan)
                .setTitle(R.string.popup_uwaga)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        przejdzDalej();
                    }
                })
                .setNegativeButton(R.string.button_anuluj, null)
                .setIcon(R.drawable.ic_uwaga);
        dialog.show();
        return false;
    }

    private void przejdzDalej() {
        LekcjeHelper.finishLekcjeTytulActivity();
        finish();
    }

    private void dodajModul() {
        ModulORM modul = new ModulORM();
        LekcjeHelper.setNewModul(modul);
        Intent intent = new Intent(this, PlikActivity.class);
        startActivity(intent);
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
    public void onResume() {
        super.onResume();
        if(LekcjeHelper.hasModules()) {
            if(modulyAdapter == null) createList();
                else modulyAdapter.refresh();
            brakModulow.setVisibility(View.INVISIBLE);
            listaModulow.setVisibility(View.VISIBLE);
            buttonNext.setClickable(true);
            buttonNext.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }
}
