package pl.osik.autyzm.lekcje.nowy_modul;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.orm.PytanieORM;
import pl.osik.autyzm.lekcje.LekcjeAdapter;
import pl.osik.autyzm.lekcje.LekcjeHelper;

public class PytaniaActivity extends AppCompatActivity implements View.OnClickListener {

    PytaniaAdapter pytaniaAdapter;

    @Bind(R.id.lista_pytan)
    RecyclerView listaPytan;
    @Bind(R.id.buttonAdd)
    Button buttonAdd;
    @Bind(R.id.buttonNext)
    Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pytania);
        ButterKnife.bind(this);
        buttonAdd.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        pytaniaAdapter = new PytaniaAdapter(getLayoutInflater(), this);
        listaPytan.setLayoutManager(new LinearLayoutManager(this));
        listaPytan.setAdapter(pytaniaAdapter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonAdd.getId()) {
            LekcjeHelper.addPytanie("");
            pytaniaAdapter.refresh();
        } else if(v.getId() == buttonNext.getId()) {
            LekcjeHelper.commitAll();
            LekcjeHelper.finishPlikActivity();
            finish();
        }
    }

    private void savePytania() {
        String out = "";
        for (PytanieORM pyt :
                LekcjeHelper.getPytaniaList()){
            out += pyt.getTresc() + " ";
        }
        Log.d("save", out);
    }
}
