package pl.osik.autyzm.validate;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by m.osik2 on 2016-05-09.
 */
public class ValidateCommand {
    private HashMap<View, ArrayList<Validate>> listaWalidacji = new HashMap<View, ArrayList<Validate>>();
    private HashMap<View, String> errorMsg = new HashMap<>();

    private boolean doValidate(View view) {
        ArrayList<Validate> lista = listaWalidacji.get(view);
        for (Validate valid : lista) {
            if(!valid.validate(view)) {
                setErrorMsg(view, valid);
                return false;
            }
        }
        return true;
    }

    public ValidateCommand addValidate(View view, Validate validate) {
        if(!listaWalidacji.containsKey(view)) {
            ArrayList<Validate> temp = new ArrayList<>();
            temp.add(validate);
            listaWalidacji.put(view, temp);
        } else {
            listaWalidacji.get(view).add(validate);
        }
        return this;
    }

    public ValidateCommand addValidate(View[] view, Validate validate) {
        for (View v : view) {
            addValidate(v, validate);
        }
        return this;
    }

    public HashMap<View, String> getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(View view, Validate validate) {
        if(errorMsg.containsKey(view)) {
            errorMsg.put(view, errorMsg.get(view) + ". " + validate.getErrorMsg());
        } else {
            errorMsg.put(view, validate.getErrorMsg());
        }
    }

    public void clearList() {
        listaWalidacji.clear();
        errorMsg.clear();
    }

    public void clearErrorMsgs() {
        errorMsg.clear();
    }

    public boolean doValidateAll() {
        boolean out = true;
        for (Map.Entry<View, ArrayList<Validate>> entry : listaWalidacji.entrySet()){
            if(!doValidate(entry.getKey())) out = false;
        }
        if(!out) {
            putErrors();
            clearErrorMsgs();
        }
        return out;
    }

    public void putErrors() {
        for (Map.Entry<View, String> entry : errorMsg.entrySet()){
            View view = entry.getKey();
            if(view instanceof TextView)
                ((TextView) entry.getKey()).setError(entry.getValue());
            else if(view instanceof TextInputLayout)
                ((TextInputLayout) entry.getKey()).setError(entry.getValue());
        }
    }
}
