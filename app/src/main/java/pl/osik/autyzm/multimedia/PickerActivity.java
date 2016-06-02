package pl.osik.autyzm.multimedia;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.FilePickerActivity;
import pl.osik.autyzm.helpers.FilePlacingInterface;

public class PickerActivity extends AppCompatActivity {

    MultimediaFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        fragment = new MultimediaFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(MultimediaFragment.CHOOSER, true);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppHelper.FileManager.PICK_IMAGE) {
                String path = data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH);
                fragment.placeFile(path);
            }
        }
    }

    @Override
    public void onBackPressed() {
        fragment.onBackPressed();
    }
}
