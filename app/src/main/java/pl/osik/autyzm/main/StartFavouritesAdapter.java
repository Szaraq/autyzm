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
 * Created by m.osik2 on 2016-05-11.
 */
public class StartFavouritesAdapter extends RecyclerView.Adapter<StartFavouritesViewHolder> {

    private final LayoutInflater layoutInflater;
    private final StartFragment fragment;
    private ArrayList<LekcjaORM> lekcjaList = Lekcja.getFavourites();

    public StartFavouritesAdapter(LayoutInflater layoutInflater, Fragment fragment) {
        this.layoutInflater = layoutInflater;
        this.fragment = (StartFragment) fragment;
    }

    @Override
    public StartFavouritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_lekcje_favourites, parent, false);
        StartFavouritesViewHolder holder = new StartFavouritesViewHolder(view);
        holder.setAdapter(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(StartFavouritesViewHolder holder, int position) {
        holder.setFragment(fragment);
        String name = lekcjaList.get(position).getTytul();
        holder.setName(name);
    }

    @Override
    public int getItemCount() {
        return lekcjaList.size();
    }

    public void refresh() {
        lekcjaList = Lekcja.getFavourites();
        notifyDataSetChanged();
    }
}

class StartFavouritesViewHolder extends RecyclerView.ViewHolder {

    private StartFavouritesAdapter adapter;
    private StartFragment fragment;
    private String name;

    @Bind(R.id.tytul)
    TextView tytul;

    public StartFavouritesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setAdapter(StartFavouritesAdapter adapter) {
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