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
import pl.osik.autyzm.uruchom.UruchomController;

/**
 * Created by m.osik2 on 2016-05-10.
 */
public class StartLastAdapter extends RecyclerView.Adapter<StartLastViewHolder> {

    private final int HOW_MANY = 5;
    private final LayoutInflater layoutInflater;
    private final StartFragment fragment;
    private ArrayList<LekcjaORM> lekcjaList = Lekcja.getOstatnieLekcje(HOW_MANY, true);

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
        LekcjaORM lekcja = lekcjaList.get(position);
        holder.setLekcja(lekcja);
    }

    @Override
    public int getItemCount() {
        return lekcjaList.size();
    }

    public void refresh() {
        lekcjaList = Lekcja.getOstatnieLekcje(HOW_MANY, true);
        notifyDataSetChanged();
    }
}

class StartLastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private StartLastAdapter adapter;
    private StartFragment fragment;
    private LekcjaORM lekcja;

    @Bind(R.id.tytul)
    TextView tytul;
    @Bind(R.id.data)
    TextView data;

    public StartLastViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tytul.setOnClickListener(this);
    }

    public void setAdapter(StartLastAdapter adapter) {
        this.adapter = adapter;
    }

    public void setFragment(StartFragment fragment) {
        this.fragment = fragment;
    }

    public void setData(String dataToSet) {
        data.setText(dataToSet);
    }

    public void setLekcja(LekcjaORM lekcja) {
        this.lekcja = lekcja;
        tytul.setText(lekcja.getTytul());
        setData(lekcja.getLastUsedAsString());
    }

    @Override
    public void onClick(View v) {
        UruchomController.runLekcja(fragment, lekcja);
    }
}