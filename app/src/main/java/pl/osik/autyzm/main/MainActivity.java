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
import android.widget.ImageView;
import android.widget.TextView;

import pl.osik.autyzm.R;
import pl.osik.autyzm.dzieci.DzieciFragment;
import pl.osik.autyzm.login.LoginActivity;
import pl.osik.autyzm.multimedia.MultimediaFragment;
import pl.osik.autyzm.sql.Dziecko;
import pl.osik.autyzm.sql.LoadTestData;
import pl.osik.autyzm.sql.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView user = (TextView) headerView.findViewById(R.id.user);
        user.setText(User.getCurrentUser());
        ImageView userPhoto = (ImageView) headerView.findViewById(R.id.userPhoto);
        //TODO userPhoto -> fotka usera

        instance = this;

        gotoFragment(new StartFragment());
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
            //super.onBackPressed();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_start) {

            gotoFragment(new StartFragment());

        } else if (id == R.id.nav_run_lesson) {

        } else if (id == R.id.nav_lessons) {

        } else if (id == R.id.nav_children) {

            gotoFragment(new DzieciFragment());

        } else if (id == R.id.nav_media) {

            gotoFragment(new MultimediaFragment());

        } else if (id == R.id.nav_config) {

        } else if (id == R.id.nav_logout) {
            User.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void gotoFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerLayout, fragment);
        fragmentTransaction.commit();
    }
}
