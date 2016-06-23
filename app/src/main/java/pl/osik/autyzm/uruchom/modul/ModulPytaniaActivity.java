package pl.osik.autyzm.uruchom.modul;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.orm.PytanieORM;
import pl.osik.autyzm.uruchom.UruchomController;
import pl.osik.autyzm.validate.ValidateAllOdpowiedziSelected;
import pl.osik.autyzm.validate.ValidateCommand;

public class ModulPytaniaActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int NO_ANSWER = -1;
    private final LinkedHashMap<PytanieORM, Integer> pytaniaOdpowiedzi = new LinkedHashMap<>();
    private ValidateCommand validate;

    @Bind(R.id.text_error)
    TextView textError;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    @Bind(R.id.containerLayout)
    LinearLayout containerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modul_pytania);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(UruchomController.getLekcja().getTytul());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinkedList<PytanieORM> pytania = UruchomController.getPytania();

        int liczbaPytan = pytania.size();
        for(int j = 0; j < liczbaPytan; j++) {
            PytanieORM currPytanie = pytania.get(j);
            pytaniaOdpowiedzi.put(currPytanie, NO_ANSWER);
            createPytanie(currPytanie);
        }
        buttonNext.setOnClickListener(this);
        addValidations();
        if(liczbaPytan == 0) buttonNext.callOnClick();
    }

    private void addValidations() {
        validate = new ValidateCommand();
        validate.addValidate(buttonNext, new ValidateAllOdpowiedziSelected(pytaniaOdpowiedzi));
    }

    private void createPytanie(PytanieORM currPytanie) {
        View v = getLayoutInflater().inflate(R.layout.pytanie, null);
        containerLayout.addView(v);
        TextView pytanieText = (TextView) v.findViewById(R.id.pytanie_text);
        pytanieText.setText(currPytanie.getTresc());

        LinearLayout buttonContainer = (LinearLayout) v.findViewById(R.id.buttonContainer);
        ArrayList<Button> buttonList = new ArrayList<Button>(buttonContainer.getChildCount());
        for (int i = 0; i < buttonContainer.getChildCount(); i++) {
            Button button = (Button) buttonContainer.getChildAt(i);
            button.setOnClickListener(new OdpowiedzOnClickListener(this, i, buttonList, currPytanie));
            buttonList.add(button);
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

    /* Button Dalej */
    @Override
    public void onClick(View v) {
        if(validate.doValidateAll()) {
            UruchomController.addOdpowiedzi(pytaniaOdpowiedzi);
            UruchomController.gotoNextActivity(this);
        } else {
            CharSequence errorTxt = buttonNext.getError().toString();
            buttonNext.setError(null);
            textError.setText(errorTxt);
            textError.setVisibility(View.VISIBLE);
        }
    }

    private static class OdpowiedzOnClickListener implements View.OnClickListener {
        private final ModulPytaniaActivity activity;
        private final int punkty;
        private final List<Button> listaOpcji;
        private final PytanieORM currPytanie;

        public OdpowiedzOnClickListener(ModulPytaniaActivity activity, int punkty, List<Button> listaOpcji, PytanieORM currPytanie) {
            this.activity = activity;
            this.punkty = punkty;
            this.listaOpcji = listaOpcji;
            this.currPytanie = currPytanie;
        }

        private void setPressed(Button b) {
            b.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryRipple));
            activity.pytaniaOdpowiedzi.put(currPytanie, punkty);
        }

        private void setUnpressed(Button b) {
            b.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
        }

        @Override
        public void onClick(View v) {
            Button b = (Button) v;
            for (Button other : listaOpcji) {
                setUnpressed(other);
            }
            setPressed(b);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UruchomController.setCurrentIsPytania(true);
    }
}