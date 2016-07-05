package pl.osik.autismemotion.validate;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

import java.util.Arrays;

import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.MyApp;
import pl.osik.autismemotion.sql.AbstractDBTable;
import pl.osik.autismemotion.sql.DBHelper;

/**
 * Created by m.osik2 on 2016-05-09.
 */
public class ValidateExistsInDatabase implements Validate {

    private final AbstractDBTable table;
    private final String column;
    private final String exceptMe;

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
        return countRows == 0;
    }

    @Override
    public String getErrorMsg() {
        return context.getString(R.string.validate_error_existsInDB);
    }
}
