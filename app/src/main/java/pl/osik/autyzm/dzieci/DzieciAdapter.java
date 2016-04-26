package pl.osik.autyzm.dzieci;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.OperationsEnum;
import pl.osik.autyzm.sql.Dziecko;

/**
 * Created by m.osik2 on 2016-04-21.
 */
public class DzieciAdapter extends RecyclerView.Adapter<DzieciViewHolder> {

    public static final String BUNDLE_SWITCH_OPERACJA = "operacja";
    private Fragment fragment;
    ArrayList<HashMap<String, Object>> dzieciList = Dziecko.getDzieciList();
    private final LayoutInflater layoutInflater;

    public DzieciAdapter(LayoutInflater layoutInflater, Fragment fragment) {
        this.layoutInflater = layoutInflater;
        this.fragment = fragment;
    }

    @Override
    public DzieciViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_dzieci, parent, false);
        return new DzieciViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DzieciViewHolder holder, int position) {
        holder.setFragment(fragment);
        HashMap<String, Object> map = dzieciList.get(position);
        String photo = (String) map.get(Dziecko.COLUMN_PHOTO);
        String name = map.get(Dziecko.COLUMN_NAZWISKO) + " " + map.get(Dziecko.COLUMN_IMIE);
        int id = (int) map.get(Dziecko.COLUMN_ID);
        holder.setPhoto(photo);
        holder.setName(name);
        holder.setId(id);
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
        dzieciName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoDetails(OperationsEnum.SHOW);
            }
        });
    }

    protected void gotoDetails(OperationsEnum operacja) {
        Intent intent = new Intent(fragment.getActivity(), DzieciDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Dziecko.COLUMN_ID, id);
        bundle.putSerializable(DzieciAdapter.BUNDLE_SWITCH_OPERACJA, operacja);
        intent.putExtras(bundle);
        fragment.startActivity(intent);
    }

    public void setPhoto(String photo) {
        this.photo = photo;
        dzieciPhoto.setImageResource(R.drawable.ic_test_child_photo);
        /*Glide.with(dzieciPhoto.getContext())
                .load(R.drawable.ic_test_child_photo)
                .into(dzieciPhoto);*/
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
            gotoDetails(OperationsEnum.EDYCJA);
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(dzieciContextMenu.getContext());
            dialog.setMessage(R.string.message_dziecko_usunięte + dzieciName.getText().toString() + "?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Dziecko d = new Dziecko();
                            d.delete(id);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert);
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
}