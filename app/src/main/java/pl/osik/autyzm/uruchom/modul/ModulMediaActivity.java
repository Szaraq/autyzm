package pl.osik.autyzm.uruchom.modul;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.FileHelper;
import pl.osik.autyzm.sql.Plik;
import pl.osik.autyzm.uruchom.UruchomController;

public class ModulMediaActivity extends AppCompatActivity implements View.OnClickListener {

    //TODO Filmy Player

    @Bind(R.id.player)
    ImageView player;
    @Bind(R.id.buttonNext)
    Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modul_media);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(UruchomController.getLekcja().getTytul());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        buttonNext.setOnClickListener(this);
        showMedia();
    }

    private void showMedia() {
        String path = Plik.getById(UruchomController.getModul().getPlik(), true).getPath();
        if(FileHelper.getType(path) == FileHelper.FileTypes.PHOTO) {
            FileHelper.FileManager.placePhoto(this, player, path, 200);
            //TODO Przetestować na urządzeniu bez rescale:
            //player.setImageBitmap(BitmapFactory.decodeFile(path));
        } else {

        }
    }

    @Override
    public void onClick(View v) {
        UruchomController.gotoNextActivity(this);
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
