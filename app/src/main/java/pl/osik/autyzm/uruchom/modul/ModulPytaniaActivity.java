package pl.osik.autyzm.uruchom.modul;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.util.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.orm.PytanieORM;
import pl.osik.autyzm.sql.Odpowiedz;
import pl.osik.autyzm.uruchom.UruchomController;
import pl.osik.autyzm.validate.ValidateAllOdpowiedziSelected;
import pl.osik.autyzm.validate.ValidateCommand;

public class ModulPytaniaActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int NO_ANSWER = -1;
    private final int SPACING = 30;
    private final int SPACING_NEXT_BUTTON = 100;
    private LinkedHashMap<PytanieORM, Integer> pytaniaOdpowiedzi = new LinkedHashMap<>();
    private int currViewId;
    private ValidateCommand validate;

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
            TextView errorMsg = new TextView(this);
            errorMsg.setText(errorTxt);
            errorMsg.setTextColor(getResources().getColor(R.color.colorError));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, buttonNext.getId());
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
            errorMsg.setLayoutParams(params);
            errorMsg.setPadding(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, SPACING_NEXT_BUTTON, getResources().getDisplayMetrics()), 0, 0);
            containerLayout.addView(errorMsg);
        }
    }

    private static class OdpowiedzOnClickListener implements View.OnClickListener {
        private ModulPytaniaActivity activity;
        private int punkty;
        private List<Button> listaOpcji;
        private PytanieORM currPytanie;

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