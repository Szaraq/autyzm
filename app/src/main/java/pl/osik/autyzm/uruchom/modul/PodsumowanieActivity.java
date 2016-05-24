package pl.osik.autyzm.uruchom.modul;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.uruchom.UruchomController;

public class PodsumowanieActivity extends AppCompatActivity implements View.OnClickListener {

    //TODO Wzmocnienie

    @Bind(R.id.buttonNext)
    Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podsumowanie);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(UruchomController.getLekcja().getTytul());
        buttonNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        UruchomController.setCurrentIsPytania(false);
    }
}
