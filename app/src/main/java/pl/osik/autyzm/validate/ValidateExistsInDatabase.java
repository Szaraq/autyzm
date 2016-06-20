package pl.osik.autyzm.validate;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;

import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.MyApp;
import pl.osik.autyzm.sql.AbstractDBTable;
import pl.osik.autyzm.sql.DBHelper;

/**
 * Created by m.osik2 on 2016-05-09.
 */
public class ValidateExistsInDatabase implements Validate {

    private AbstractDBTable table;
    private String column, exceptMe;

    public ValidateExistsInDatabase(AbstractDBTable table, String column) {
        this(table, column, null);
    }

    public ValidateExistsInDatabase(AbstractDBTable table, String column, String exceptMe) {
        if(!Arrays.asList(table.getColumns()).contains(column)) {
            try {
                throw new Exception("Nie ma kolumny " + column + " w tabeli " + table.getTableName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.table = table;
        this.column = column;
        this.exceptMe = exceptMe;
    }

    @Override
    public boolean validate(View view) {
        String textFromView = getStringFromView(view);
        return validate(textFromView);
    }

    private String getStringFromView(View view) {
        EditText editText = null;
        if(view instanceof TextInputLayout) {
            editText = ((TextInputLayout) view).getEditText();
        } else if(view instanceof EditText) {
            editText = (EditText) view;
        } else {
            try {
                throw new Exception(MyApp.getContext().getString(R.string.exception_wrong_view));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return editText.getText().toString();
    }

    public boolean validate(String text) {
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDBRead();
        if(exceptMe != null && text.equals(exceptMe)) return true;
        String query = "SELECT * FROM " + table.getTableName() + " WHERE " + column + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{text});
        int countRows = cursor.getCount();
        cursor.close();
        helper.close();
        if(countRows == 0) return true;
        return false;
    }

    @Override
    public String getErrorMsg() {
        return context.getString(R.string.validate_error_existsInDB);
    }
}
