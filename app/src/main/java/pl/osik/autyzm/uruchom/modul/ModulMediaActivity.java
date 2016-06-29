package pl.osik.autyzm.uruchom.modul;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.FileHelper;
import pl.osik.autyzm.sql.Plik;
import pl.osik.autyzm.uruchom.UruchomController;
import tcking.github.com.giraffeplayer.GiraffePlayer;

public class ModulMediaActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.videoPlayerContainer)
    FrameLayout videoPlayerContainer;
    @Bind(R.id.photoPlayer)
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
            player.setVisibility(View.VISIBLE);
            buttonNext.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(path)
                    .fitCenter()
                    .dontAnimate()
                    .into(player);
        } else {
            videoPlayerContainer.setVisibility(View.VISIBLE);
            GiraffePlayer player = new GiraffePlayer(this);
            player.play(path);
            player.onComplete(new Runnable() {
                @Override
                public void run() {
                    UruchomController.gotoNextActivity(ModulMediaActivity.this);
                }
            });
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
