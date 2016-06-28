package pl.osik.autyzm.uruchom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;

public class WyborDzieckaActivity extends AppCompatActivity {

    WyborDzieckaAdapter wyborAdapter;

    @Bind(R.id.dzieci_list)
    RecyclerView dzieciList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wybor_dziecka);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(R.string.uruchom_wybor_dziecka_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wyborAdapter = new WyborDzieckaAdapter(getLayoutInflater(), this);
        dzieciList.setLayoutManager(new LinearLayoutManager(this));
        dzieciList.setAdapter(wyborAdapter);
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
        UruchomController.setCurrentIsPytania(false);
    }
}
