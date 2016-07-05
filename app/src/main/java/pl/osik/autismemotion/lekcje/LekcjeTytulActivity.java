package pl.osik.autismemotion.lekcje;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.AppHelper;
import pl.osik.autismemotion.helpers.OperationsEnum;
import pl.osik.autismemotion.helpers.orm.LekcjaORM;
import pl.osik.autismemotion.validate.ValidateCommand;
import pl.osik.autismemotion.validate.ValidateNotNull;

public class LekcjeTytulActivity extends AppCompatActivity implements View.OnClickListener {

    LekcjaORM lekcja;
    private final ValidateCommand validate = new ValidateCommand();

    @Bind(R.id.tytul)
    EditText tytul;
    @Bind(R.id.button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lekcje_tytul);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(R.string.lekcje_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        validate.addValidate(tytul, new ValidateNotNull());
        button.setOnClickListener(this);

        if(LekcjeHelper.getOperacja() == OperationsEnum.EDYCJA) {
            lekcja = LekcjeHelper.getLekcja();
            populate(lekcja);
        } else {
            lekcja = new LekcjaORM();
        }
    }

    private void populate(LekcjaORM lekcja) {
        tytul.setText(lekcja.getTytul());
    }

    @Override
    public void onClick(View v) {
        if(validate.doValidateAll()) {
            Intent intent = new Intent(v.getContext(), LekcjeModulActivity.class);
            lekcja.setTytul(tytul.getText().toString());
            LekcjeHelper.setLekcja(lekcja);
            LekcjeHelper.setLekcjeTytulActivity(this);
            startActivity(intent);
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

    @Override
    public void onPause() {
        super.onPause();
        AppHelper.hideKeyboard();
    }
}
