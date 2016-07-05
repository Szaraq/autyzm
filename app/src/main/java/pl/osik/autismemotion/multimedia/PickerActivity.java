package pl.osik.autismemotion.multimedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.FileHelper;
import pl.osik.autismemotion.helpers.FilePickerActivity;

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
            if (requestCode == FileHelper.FileManager.PICK_IMAGE) {
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
