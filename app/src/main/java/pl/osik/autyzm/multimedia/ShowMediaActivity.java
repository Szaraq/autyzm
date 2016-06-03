package pl.osik.autyzm.multimedia;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.util.PlayerControl;
import com.google.android.libraries.mediaframework.exoplayerextensions.Video;
import com.google.android.libraries.mediaframework.layeredvideo.SimpleVideoPlayer;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.FileHelper;
import pl.osik.autyzm.helpers.orm.PlikORM;
import pl.osik.autyzm.lekcje.nowy_modul.PlikActivity;

public class ShowMediaActivity extends AppCompatActivity {

    public static final String EXTRA_PLIK = "plik_orm";
    public static String PLIK;

    private PlikORM plik;

    @Bind(R.id.videoPlayerContainer)
    FrameLayout videoPlayerContainer;
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
        String path = plik.getPath();
        PLIK = path;
        if(FileHelper.getType(path) == FileHelper.FileTypes.PHOTO) {
            FileHelper.FileManager.placePhoto(this, player, path, 200);
        } else {
            SimpleVideoPlayer videoPlayer = new SimpleVideoPlayer(this, videoPlayerContainer, new Video(path, Video.VideoType.MP4), plik.getName(), false);
            videoPlayer.play();
            videoPlayer.release();

            /*Intent intent = new Intent(this, PlayerActivity.class);
            startActivity(intent);*/
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
