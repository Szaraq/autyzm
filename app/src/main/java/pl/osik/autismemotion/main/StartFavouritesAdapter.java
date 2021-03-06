package pl.osik.autismemotion.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.orm.LekcjaORM;
import pl.osik.autismemotion.sql.Lekcja;
import pl.osik.autismemotion.uruchom.UruchomController;

/**
 * Created by m.osik2 on 2016-05-11.
 */
public class StartFavouritesAdapter extends RecyclerView.Adapter<StartFavouritesViewHolder> {

    private final StartFragment fragment;
    public ArrayList<LekcjaORM> lekcjaList = Lekcja.getFavourites(true);

    public StartFavouritesAdapter(LayoutInflater layoutInflater, StartFragment fragment) {
        LayoutInflater layoutInflater1 = layoutInflater;
        this.fragment = fragment;
    }

    @Override
    public StartFavouritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_lekcje_favourites, parent, false);
        StartFavouritesViewHolder holder = new StartFavouritesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(StartFavouritesViewHolder holder, int position) {
        holder.setFragment(fragment);
        holder.setLekcja(lekcjaList.get(position));
    }

    @Override
    public int getItemCount() {
        return lekcjaList.size();
    }

    public void refresh() {
        lekcjaList = Lekcja.getFavourites(true);
        notifyDataSetChanged();
    }
}

class StartFavouritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private StartFragment fragment;
    private LekcjaORM lekcja;

    @Bind(R.id.containerLayout)
    LinearLayout containerLayout;
    @Bind(R.id.tytul)
    TextView tytul;

    public StartFavouritesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        containerLayout.setOnClickListener(this);
    }

    public void setFragment(StartFragment fragment) {
        this.fragment = fragment;
    }

    public void setLekcja(LekcjaORM lekcja) {
        this.lekcja = lekcja;
        tytul.setText(lekcja.getTytul());
    }

    @Override
    public void onClick(View v) {
        UruchomController.runLekcja(fragment, lekcja);
    }
}