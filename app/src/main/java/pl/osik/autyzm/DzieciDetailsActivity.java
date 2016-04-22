package pl.osik.autyzm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.sql.Dziecko;

public class DzieciDetailsActivity extends AppCompatActivity {

    @Bind(R.id.dzieci_details_text)
    TextView dzieciDetailsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dzieci_details);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        HashMap<String, String> dziecko = Dziecko.getDzieckoById(bundle.getInt(Dziecko.COLUMN_ID));
        getSupportActionBar().setTitle(dziecko.get(Dziecko.COLUMN_IMIE) + " " + dziecko.get(Dziecko.COLUMN_NAZWISKO));

        StringBuilder show = new StringBuilder();
        for(Map.Entry<String, String> entry : dziecko.entrySet()) {
            show.append(entry.getKey());
            show.append(": ");
            show.append(entry.getValue());
            show.append("\n");
        }

        dzieciDetailsText.setText(show.toString());
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
