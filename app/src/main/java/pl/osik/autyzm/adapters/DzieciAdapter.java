package pl.osik.autyzm.adapters;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.DzieciDetailsActivity;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.sql.Dziecko;

/**
 * Created by m.osik2 on 2016-04-21.
 */
public class DzieciAdapter extends RecyclerView.Adapter<DzieciViewHolder> {

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
                Toast.makeText(dzieciContextMenu.getContext(), "Kliknięcie wprost na " + name, Toast.LENGTH_LONG).show();
            }
        });
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
            Intent intent = new Intent(fragment.getActivity(), DzieciDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Dziecko.COLUMN_ID, id);
            intent.putExtras(bundle);
            fragment.startActivity(intent);
        } else {
            Toast.makeText(dzieciContextMenu.getContext(), item.getTitle() + " " + name, Toast.LENGTH_LONG).show();
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