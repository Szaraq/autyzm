package pl.osik.autyzm.lekcje;

import android.content.Intent;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.dzieci.DzieciAdapter;
import pl.osik.autyzm.helpers.OperationsEnum;

public class LekcjeModulActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.buttonAddModule)
    Button buttonAdd;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    @Bind(R.id.lista_modulow_container)
    FrameLayout listaModulowContainer;
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
            buttonNext.setClickable(false);
            buttonNext.setTextColor(getResources().getColor(R.color.colorPrimaryRipple));
            listaModulow.setVisibility(View.GONE);
            dodajBrakModulowInfo();
        } else {
            modulyAdapter = new ModulyAdapter(getLayoutInflater(), this, LekcjeHelper.getLekcja().getId());
            listaModulow.setLayoutManager(new LinearLayoutManager(this));
            listaModulow.setAdapter(modulyAdapter);
        }
    }

    private void dodajBrakModulowInfo() {
        final TextView text = new TextView(this);
        text.setText(R.string.lekcje_modul_noModules);
        text.setTextColor(getResources().getColor(R.color.colorError));
        text.setGravity(Gravity.CENTER);
        listaModulowContainer.addView(text);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonAdd.getId())
            dodajModul();
        else if(v.getId() == buttonNext.getId())
            przejdzDalej();
    }

    private void przejdzDalej() {
        //TODO przejdzDalej
        Toast.makeText(this, "przejdzDalej", Toast.LENGTH_SHORT).show();
    }

    private void dodajModul() {
        //TODO dodajModul
        Toast.makeText(this, "dodajModul", Toast.LENGTH_SHORT).show();
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
