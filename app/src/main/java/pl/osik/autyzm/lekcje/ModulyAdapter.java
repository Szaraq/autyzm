package pl.osik.autyzm.lekcje;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.MyApp;
import pl.osik.autyzm.helpers.orm.ModulORM;
import pl.osik.autyzm.lekcje.nowy_modul.PlikActivity;
import pl.osik.autyzm.sql.Modul;

/**
 * Created by m.osik2 on 2016-05-17.
 */
public class ModulyAdapter extends RecyclerView.Adapter<ModulyViewHolder> {

    public static final String BUNDLE_SWITCH_OPERACJA = "operacja";

    private ArrayList<ModulORM> moduly;
    private final LekcjeModulActivity activity;

    public ModulyAdapter(LayoutInflater layoutInflater, LekcjeModulActivity activity, int lekcjaId) {
        LayoutInflater layoutInflater1 = layoutInflater;
        this.activity = activity;
        moduly = LekcjeHelper.getModulyList();
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
        moduly = LekcjeHelper.getModulyList();
        notifyDataSetChanged();
    }
}

class ModulyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private LekcjeModulActivity activity;
    private ModulyAdapter adapter;
    private ModulORM modul;

    @Bind(R.id.modul_name)
    TextView modulName;
    @Bind(R.id.arrow_up)
    ImageView arrowUp;
    @Bind(R.id.arrow_down)
    ImageView arrowDown;
    @Bind(R.id.modul_context_menu)
    ImageView modulContextMenu;

    public ModulyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        modulContextMenu.setOnClickListener(this);
        arrowUp.setOnClickListener(this);
        arrowDown.setOnClickListener(this);
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
        cleanArrows();
    }

    private void cleanArrows() {
        arrowUp.setVisibility(View.VISIBLE);
        arrowDown.setVisibility(View.VISIBLE);
        if(modul.getNumer() == 1) arrowUp.setVisibility(View.GONE);
        if(modul.getNumer() == LekcjeHelper.getModulyList().size()) arrowDown.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == modulContextMenu.getId()) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            AppHelper.setForceIconInPopupMenu(popupMenu);
            popupMenu.inflate(R.menu.dzieci_context_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
            return;
        } else if(v.getId() == arrowUp.getId()) {
            LekcjeHelper.swapModul(true, modul.getNumer());
        } else if(v.getId() == arrowDown.getId()) {
            LekcjeHelper.swapModul(false, modul.getNumer());
        }
        cleanArrows();
        adapter.refresh();
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
                    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Modul m = new Modul();
                            m.delete(modul.getId());
                            LekcjeHelper.removeModul(modul.getNumer() - 1);
                            adapter.refresh();
                            AppHelper.showMessage(activity.containerLayout, R.string.message_modul_usunięty);
                        }
                    })
                    .setNegativeButton(R.string.button_anuluj, null)
                    .setIcon(R.drawable.ic_uwaga);
            dialog.show();
        }
        return true;
    }
}