package pl.osik.autismemotion.multimedia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.FileHelper;
import pl.osik.autismemotion.helpers.orm.PlikORM;
import tcking.github.com.giraffeplayer.GiraffePlayer;

public class ShowMediaActivity extends AppCompatActivity {

    public static final String EXTRA_PLIK = "plik_orm";
    public static String PLIK;

    private PlikORM plik;

    @Bind(R.id.videoPlayerContainer)
    FrameLayout videoPlayerContainer;
    @Bind(R.id.photoPlayer)
    ImageView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_media);
        ButterKnife.bind(this);
        plik = (PlikORM) getIntent().getSerializableExtra(EXTRA_PLIK);
        showMedia();
    }

    private void showMedia() {
        String path = plik.getPath();
        PLIK = path;
        if(FileHelper.getType(path, plik.isGotByNative()) == FileHelper.FileTypes.PHOTO) {
            player.setVisibility(View.VISIBLE);
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
                    ShowMediaActivity.this.finish();
                }
            });
        }
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
