package pl.osik.autyzm.validate;

import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;

import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.orm.PytanieORM;
import pl.osik.autyzm.uruchom.modul.ModulPytaniaActivity;

/**
 * Created by m.osik2 on 2016-05-24.
 */
public class ValidateAllOdpowiedziSelected implements Validate {
    LinkedHashMap<PytanieORM, Integer> pytaniaOdpowiedzi;

    public ValidateAllOdpowiedziSelected(LinkedHashMap<PytanieORM, Integer> pytaniaOdpowiedzi) {
        this.pytaniaOdpowiedzi = pytaniaOdpowiedzi;
    }

    @Override
    public boolean validate(View view) {
        for (Map.Entry<PytanieORM, Integer> entry : pytaniaOdpowiedzi.entrySet()) {
            if(entry.getValue() == ModulPytaniaActivity.NO_ANSWER) return false;
        }
        return true;
    }

    @Override
    public String getErrorMsg() {
        return context.getString(R.string.validate_error_allOdpowiedziSelected);
    }
}
