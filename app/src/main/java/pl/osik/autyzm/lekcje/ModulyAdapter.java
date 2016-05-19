package pl.osik.autyzm.lekcje;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.MyApp;
import pl.osik.autyzm.helpers.OperationsEnum;
import pl.osik.autyzm.helpers.orm.LekcjaORM;
import pl.osik.autyzm.helpers.orm.ModulORM;
import pl.osik.autyzm.lekcje.nowy_modul.PlikActivity;
import pl.osik.autyzm.sql.Lekcja;
import pl.osik.autyzm.sql.Modul;

/**
 * Created by m.osik2 on 2016-05-17.
 */
public class ModulyAdapter extends RecyclerView.Adapter<ModulyViewHolder> {

    public static final String BUNDLE_SWITCH_OPERACJA = "operacja";

    private int lekcjaId;
    private ArrayList<ModulORM> moduly;
    private LekcjeModulActivity activity;
    private final LayoutInflater layoutInflater;

    public ModulyAdapter(LayoutInflater layoutInflater, LekcjeModulActivity activity, int lekcjaId) {
        this.layoutInflater = layoutInflater;
        this.activity = activity;
        this.lekcjaId = lekcjaId;
        moduly = Modul.getModulyForLekcja(lekcjaId);
    }

    @Override
    public ModulyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_modulow, parent, false);
        ButterKnife.bind(this, view);
        ModulyViewHolder holder = new ModulyViewHolder(view);
        holder.setAdapter(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ModulyViewHolder holder, int position) {
        holder.setActivity(activity);
        holder.setModul(moduly.get(position));
    }

    @Override
    public int getItemCount() {
        return moduly.size();
    }

    public void refresh() {
        moduly = Modul.getModulyForLekcja(lekcjaId);
        notifyDataSetChanged();
    }
}

class ModulyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private LekcjeModulActivity activity;
    private ModulyAdapter adapter;
    private ModulORM modul;

    @Bind(R.id.modul_name)
    TextView modulName;
    @Bind(R.id.modul_context_menu)
    ImageView modulContextMenu;

    public ModulyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        modulContextMenu.setOnClickListener(this);
    }

    public void setActivity(LekcjeModulActivity activity) {
        this.activity = activity;
    }

    public void setAdapter(ModulyAdapter adapter) {
        this.adapter = adapter;
    }

    public void setModul(ModulORM modul) {
        this.modul = modul;
        modulName.setText(modul.getName());
    }

    @Override
    public void onClick(View v) {
        if(v == modulContextMenu) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            AppHelper.setForceIconInPopupMenu(popupMenu);
            popupMenu.inflate(R.menu.dzieci_context_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.dzieci_edit) {
            LekcjeHelper.setModul(modul);
            Intent intent = new Intent(activity, PlikActivity.class);
            activity.startActivity(intent);
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(modulContextMenu.getContext());
            dialog.setMessage(MyApp.getContext().getString(R.string.message_dziecko_do_usunięcia) + " " + modul.getName() + "?")
                    .setTitle(R.string.popup_uwaga)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Modul m = new Modul();
                            m.delete(modul.getId());
                            adapter.refresh();
                            Toast.makeText(modulContextMenu.getContext(), R.string.message_modul_usunięty, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.ic_uwaga);
            dialog.show();
        }
        return true;
    }
}