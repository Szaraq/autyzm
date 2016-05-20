package pl.osik.autyzm.uruchom.modul;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.util.Util;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.sql.Odpowiedz;
import pl.osik.autyzm.uruchom.UruchomController;

public class ModulPytaniaActivity extends AppCompatActivity {
    RadioButton[] radioButtons = new RadioButton[Odpowiedz.MAX_VAL - Odpowiedz.MIN_VAL + 1];

    @Bind(R.id.pytanie)
    TextView pytanie;
    @Bind(R.id.odpowiedzi)
    RadioGroup odpowiedziGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modul_pytania);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(UruchomController.getLekcja().getTytul());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pytanie.setText(UruchomController.getPytanie().getTresc());

        int count = 0;
        final int vPadd = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 50, getResources().getDisplayMetrics());
        for (int i = Odpowiedz.MIN_VAL; i <= Odpowiedz.MAX_VAL; i++) {
            RadioButton radio = new RadioButton(this);
            radio.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            radio.setText(String.valueOf(i));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            radio.setLayoutParams(params);
            radio.setPadding(0, vPadd, 0, vPadd);

            odpowiedziGroup.addView(radio);
            radio.setOnClickListener(new OdpowiedzOnClickListener(this, i));
            radioButtons[count++] = radio;
        }
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

    private static class OdpowiedzOnClickListener  implements View.OnClickListener {
        private Activity activity;
        private int punkty;

        public OdpowiedzOnClickListener(Activity activity, int punkty) {
            this.activity = activity;
            this.punkty = punkty;
        }

        @Override
        public void onClick(View v) {
            UruchomController.addOdpowiedz(punkty);
            UruchomController.gotoNextActivity(activity);
        }
    }

}