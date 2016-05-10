package pl.osik.autyzm.main;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.orm.LekcjaORM;
import pl.osik.autyzm.sql.Lekcja;

/**
 * Created by m.osik2 on 2016-05-10.
 */
public class StartLastAdapter extends RecyclerView.Adapter<StartLastViewHolder> {

    private final int HOW_MANY = 5;
    private final LayoutInflater layoutInflater;
    private final StartFragment fragment;
    private ArrayList<LekcjaORM> lekcjaList = Lekcja.getOstatnieLekcje(HOW_MANY);

    public StartLastAdapter(LayoutInflater layoutInflater, Fragment fragment) {
        this.layoutInflater = layoutInflater;
        this.fragment = (StartFragment) fragment;
    }

    @Override
    public StartLastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_lekcje_last, parent, false);
        StartLastViewHolder holder = new StartLastViewHolder(view);
        holder.setAdapter(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(StartLastViewHolder holder, int position) {
        holder.setFragment(fragment);
        String name = lekcjaList.get(position).getTytul();
        holder.setName(name);
    }

    @Override
    public int getItemCount() {
        return lekcjaList.size();
    }

    public void refresh() {
        lekcjaList = Lekcja.getOstatnieLekcje(HOW_MANY);
        notifyDataSetChanged();
    }
}

class StartLastViewHolder extends RecyclerView.ViewHolder {

    private StartLastAdapter adapter;
    private StartFragment fragment;
    private String name;

    @Bind(R.id.tytul)
    TextView tytul;

    public StartLastViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setAdapter(StartLastAdapter adapter) {
        this.adapter = adapter;
    }

    public void setFragment(StartFragment fragment) {
        this.fragment = fragment;
    }

    public void setName(String name) {
        this.name = name;
        tytul.setText(name);
    }
}