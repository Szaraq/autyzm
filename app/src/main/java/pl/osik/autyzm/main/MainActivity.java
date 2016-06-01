package pl.osik.autyzm.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.dzieci.DzieciFragment;
import pl.osik.autyzm.help.HelpFragment;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.FilePickerActivity;
import pl.osik.autyzm.helpers.FilePlacingInterface;
import pl.osik.autyzm.helpers.MyPreDrawListener;
import pl.osik.autyzm.lekcje.LekcjeFragment;
import pl.osik.autyzm.login.LoginActivity;
import pl.osik.autyzm.login.UserDetailsFragment;
import pl.osik.autyzm.multimedia.MultimediaFragment;
import pl.osik.autyzm.sql.Dziecko;
import pl.osik.autyzm.sql.LoadTestData;
import pl.osik.autyzm.sql.Plik;
import pl.osik.autyzm.sql.User;
import pl.osik.autyzm.uruchom.UruchomFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //TODO walidacja, przede wszystkim NotNull i ExistsInDatabase
    //TODO FINALLY dodać final tam gdzie można
    //TODO FINALLY wyrzucić debuggable z manifest i build.gradle

    public static MainActivity instance;
    private TextView user;
    private ImageView userPhoto;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Fragment currFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUserInDrawerMenu();

        instance = this;

        gotoFragment(new StartFragment());

        //TODO FINALLY Uncomment
        //Plik.cleanDeletedFiles();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.containerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(fragment instanceof MultimediaFragment) {
            ((MultimediaFragment) fragment).onBackPressed();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_start) {

            gotoFragment(new StartFragment());

        } else if (id == R.id.nav_run_lesson) {

            gotoFragment(new UruchomFragment());

        } else if (id == R.id.nav_lessons) {

            gotoFragment(new LekcjeFragment());

        } else if (id == R.id.nav_children) {

            gotoFragment(new DzieciFragment());

        } else if (id == R.id.nav_media) {

            gotoFragment(new MultimediaFragment());

        } else if (id == R.id.nav_user) {

            Fragment fragment = new UserDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(UserDetailsFragment.NEW_ACCOUNT, false);
            fragment.setArguments(bundle);
            gotoFragment(fragment);

        } else if (id == R.id.nav_help) {

            gotoFragment(new HelpFragment());

        } else if (id == R.id.nav_logout) {
            drawer.closeDrawers();
            User.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.zoom_enter, R.anim.hold);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void gotoFragment(Fragment fragment) {
        currFragment = fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerLayout, fragment);
        fragmentTransaction.commit();
    }

    public void setUserInDrawerMenu() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        user = (TextView) headerView.findViewById(R.id.user);
        userPhoto = (ImageView) headerView.findViewById(R.id.userPhoto);

        user.setText(User.getCurrentName());
        String photoPath = User.getCurrentPhotoPath();

        ViewTreeObserver vto = userPhoto.getViewTreeObserver();
        //vto.addOnPreDrawListener(new MyPreDrawListener(userPhoto, photoPath, this));
        if(photoPath != null) AppHelper.FileManager.placePhoto(this, userPhoto, photoPath, 400);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppHelper.FileManager.PICK_IMAGE) {
                String path = data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH);
                ((FilePlacingInterface) currFragment).placeFile(path);
            }
        }
    }
}
