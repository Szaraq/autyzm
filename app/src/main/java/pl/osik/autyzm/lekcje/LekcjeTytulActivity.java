package pl.osik.autyzm.lekcje;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.OperationsEnum;
import pl.osik.autyzm.helpers.orm.LekcjaORM;
import pl.osik.autyzm.sql.Lekcja;

public class LekcjeTytulActivity extends AppCompatActivity implements View.OnClickListener {

    OperationsEnum operacja;

    @Bind(R.id.tytul)
    EditText tytul;
    @Bind(R.id.button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lekcje_tytul);
        ButterKnife.bind(this);
        button.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        operacja = (OperationsEnum) bundle.getSerializable(LekcjeAdapter.BUNDLE_SWITCH_OPERACJA);
        if(operacja == OperationsEnum.EDYCJA)
            populate((LekcjaORM) bundle.getSerializable(Lekcja.TABLE_NAME));
    }

    private void populate(LekcjaORM lekcja) {
        tytul.setText(lekcja.getTytul());
    }

    @Override
    public void onClick(View v) {
        //TODO Next
    }
}
