package pl.osik.autyzm.multimedia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.orm.PlikORM;
import pl.osik.autyzm.sql.Plik;
import pl.osik.autyzm.uruchom.UruchomController;

public class ShowMediaActivity extends AppCompatActivity {

    public static final String EXTRA_PLIK = "plik_orm";

    private PlikORM plik;

    @Bind(R.id.player)
    ImageView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_media);
        ButterKnife.bind(this);
        plik = (PlikORM) getIntent().getSerializableExtra(EXTRA_PLIK);
        getSupportActionBar().setTitle(plik.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        showMedia();
    }

    private void showMedia() {
        AppHelper.FileManager.placePhoto(this, player, plik.getPath(), 200);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
