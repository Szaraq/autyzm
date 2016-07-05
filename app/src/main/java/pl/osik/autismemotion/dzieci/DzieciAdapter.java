package pl.osik.autismemotion.dzieci;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.AppHelper;
import pl.osik.autismemotion.helpers.MyApp;
import pl.osik.autismemotion.helpers.MyPreDrawListener;
import pl.osik.autismemotion.helpers.OperationsEnum;
import pl.osik.autismemotion.sql.Dziecko;

/**
 * Created by m.osik2 on 2016-04-21.
 */
public class DzieciAdapter extends RecyclerView.Adapter<DzieciViewHolder> {

    public static final String BUNDLE_SWITCH_OPERACJA = "operacja";

    private final Fragment fragment;
    ArrayList<HashMap<String, Object>> dzieciList = Dziecko.getDzieciList();

    @Bind(R.id.lista_dzieci)
    LinearLayout listaDzieci;

    public DzieciAdapter(final LayoutInflater layoutInflater, final Fragment fragment) {
        LayoutInflater layoutInflater1 = layoutInflater;
        this.fragment = fragment;
    }

    @Override
    public DzieciViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_dzieci, parent, false);
        ButterKnife.bind(this, view);
        final DzieciViewHolder holder = new DzieciViewHolder(view);
        holder.setAdapter(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final DzieciViewHolder holder, final int position) {
        holder.setFragment(fragment);
        final HashMap<String, Object> map = dzieciList.get(position);
        final String photo = (String) map.get(Dziecko.COLUMN_PHOTO);
        final String name = map.get(Dziecko.COLUMN_NAZWISKO) + " " + map.get(Dziecko.COLUMN_IMIE);
        final int id = (int) map.get(Dziecko.COLUMN_ID);
        holder.setPhoto(photo);
        holder.setName(name);
        holder.setId(id);
    }

    public void refresh() {
        dzieciList = Dziecko.getDzieciList();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dzieciList.size();
    }
}

class DzieciViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private int id;
    private String name;
    private String photo;
    private Fragment fragment;
    private DzieciAdapter adapter;

    @Bind(R.id.containerLayout)
    LinearLayout containerLayout;
    @Bind(R.id.dzieci_name)
    TextView dzieciName;
    @Bind(R.id.dzieci_photo)
    ImageView dzieciPhoto;
    @Bind(R.id.dzieci_context_menu)
    ImageView dzieciContextMenu;

    public DzieciViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
        dzieciContextMenu.setOnClickListener(this);
        containerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoDetails(OperationsEnum.SHOW);
            }
        });
    }

    protected void gotoDetails(OperationsEnum operacja) {
        final Intent intent = new Intent(fragment.getActivity(), DzieciDetailsActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putInt(Dziecko.COLUMN_ID, id);
        bundle.putSerializable(DzieciAdapter.BUNDLE_SWITCH_OPERACJA, operacja);
        intent.putExtras(bundle);
        fragment.startActivity(intent);
    }

    public void setPhoto(String photo) {
        this.photo = photo;
        if(photo == null || !(new File(photo)).exists()) {
            dzieciPhoto.setImageResource(DzieciDetailsActivity.RESOURCE_NO_PHOTO);
        } else {
            final ViewTreeObserver vto = dzieciPhoto.getViewTreeObserver();
            vto.addOnPreDrawListener(new MyPreDrawListener(dzieciPhoto, photo, fragment.getActivity()));
        }
    }

    public String getPhoto() {
        return photo;
    }

    public void setName(String name) {
        this.name = name;
        dzieciName.setText(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public void onClick(View v) {
        if(v == dzieciContextMenu) {
            final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            AppHelper.setForceIconInPopupMenu(popupMenu);
            popupMenu.inflate(R.menu.dzieci_context_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.dzieci_edit) {
            gotoDetails(OperationsEnum.EDYCJA);
        } else {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(dzieciContextMenu.getContext());
            dialog.setMessage(MyApp.getContext().getString(R.string.message_dziecko_do_usunięcia) + " " + name + "?")
                    .setTitle(R.string.popup_uwaga)
                    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Dziecko d = new Dziecko();
                            d.delete(id);
                            adapter.refresh();
                            AppHelper.showMessage(fragment.getView(), R.string.message_dziecko_usunięte);
                        }
                    })
                    .setNegativeButton(R.string.button_anuluj, null)
                    .setIcon(R.drawable.ic_uwaga);
            dialog.show();
        }
        return true;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    protected void setAdapter(DzieciAdapter adapter) {
        this.adapter = adapter;
    }
}