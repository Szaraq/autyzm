package pl.osik.autyzm.multimedia;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pl.osik.autyzm.R;

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
}
