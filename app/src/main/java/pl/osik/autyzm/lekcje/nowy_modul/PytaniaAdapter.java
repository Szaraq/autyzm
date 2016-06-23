package pl.osik.autyzm.lekcje.nowy_modul;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.MyApp;
import pl.osik.autyzm.helpers.orm.PytanieORM;
import pl.osik.autyzm.lekcje.LekcjeHelper;

/**
 * Created by m.osik2 on 2016-05-18.
 */
public class PytaniaAdapter extends RecyclerView.Adapter<PytaniaAdapter.PytaniaViewHolder> {

    public ArrayList<PytanieORM> pytania = LekcjeHelper.getPytaniaList();
    private final PytaniaActivity activity;
    private final LayoutInflater layoutInflater;
    protected boolean pytanieAdded = false;

    public PytaniaAdapter(LayoutInflater layoutInflater, PytaniaActivity activity) {
        this.layoutInflater = layoutInflater;
        this.activity = activity;
    }

    @Override
    public PytaniaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_pytan, parent, false);
        //Butterknife do konstruktora (+test), bo chyba wykonuje się tyle razy, ile jest wierszy. W każdym adapterze!!
        ButterKnife.bind(this, view);
        PytaniaViewHolder holder = new PytaniaViewHolder(view, new MyCustomEditTextListener());
        holder.setAdapter(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(PytaniaViewHolder holder, int position) {
        holder.myCustomEditTextListener.updatePosition(position);
        holder.setActivity(activity);
        holder.setPosition(position);
        holder.setPytanie(pytania.get(position));
    }

    @Override
    public int getItemCount() {
        return pytania.size();
    }

    public void refresh() {
        pytania = LekcjeHelper.getPytaniaList();
        notifyDataSetChanged();
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            pytania.get(position).setTresc(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

    public static class PytaniaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        PytaniaAdapter adapter;
        PytaniaActivity activity;
        int position;
        public final MyCustomEditTextListener myCustomEditTextListener;

        @Bind(R.id.row_pytanie)
        EditText pytanieText;
        @Bind(R.id.delete)
        ImageView delete;

        public PytaniaViewHolder(View view, MyCustomEditTextListener listener) {
            super(view);
            ButterKnife.bind(this, view);
            myCustomEditTextListener = listener;
            pytanieText.addTextChangedListener(listener);
            delete.setOnClickListener(this);
        }

        public void setAdapter(PytaniaAdapter adapter) {
            this.adapter = adapter;
        }

        public void setActivity(PytaniaActivity activity) {
            this.activity = activity;
        }

        public void setPosition(int position) {
            this.position = position;
            pytanieText.setHint(activity.getString(R.string.lekcje_modul_pytania_row_header) + " " + (position + 1));
            if(adapter.pytanieAdded && adapter.pytania.size() == (position + 1)) {
                adapter.pytanieAdded = false;
                pytanieText.requestFocus();
            }
        }

        public void setPytanie(PytanieORM pytanie) {
            pytanieText.setText(pytanie.getTresc());
        }

        public String getHint() {
            return pytanieText.getHint().toString();
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setMessage(MyApp.getContext().getString(R.string.message_pytanie_do_usunięcia) + " " + getHint() + "?")
                    .setTitle(R.string.popup_uwaga)
                    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LekcjeHelper.removePytanie(position);
                            adapter.refresh();
                            activity.setAddPytanieVisibility(LekcjeHelper.getPytaniaList().size() == 0);
                        }
                    })
                    .setNegativeButton(R.string.button_anuluj, null)
                    .setIcon(R.drawable.ic_uwaga);
            dialog.show();
        }
    }
}