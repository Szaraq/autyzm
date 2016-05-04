package pl.osik.autyzm.multimedia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.MySortedMap;
import pl.osik.autyzm.main.StartFragment;
import pl.osik.autyzm.sql.Dziecko;
import pl.osik.autyzm.sql.Folder;

/**
 * Created by m.osik2 on 2016-05-04.
 */
public class FolderyAdapter extends RecyclerView.Adapter<FolderyViewHolder> {

    private MultimediaFragment fragment;
    private final LayoutInflater layoutInflater;
    private final int idFolder;
    private MySortedMap foldery;

    public FolderyAdapter(LayoutInflater layoutInflater, Fragment fragment, int idFolder) {
        this.layoutInflater = layoutInflater;
        this.fragment = (MultimediaFragment) fragment;
        this.idFolder = idFolder;
        this.foldery = Folder.getFolderyInFolder(idFolder);
    }

    @Override
    public FolderyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_folderow, parent, false);
        FolderyViewHolder holder = new FolderyViewHolder(view);
        holder.setAdapter(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(FolderyViewHolder holder, int position) {
        holder.setFragment(fragment);
        String name = foldery.get(position);
        int id = foldery.get(name);
        holder.setName(name);
        holder.setId(id);
    }

    @Override
    public int getItemCount() {
        return foldery.size();
    }

    public void refresh() {
        foldery = Folder.getFolderyInFolder(idFolder);
        notifyDataSetChanged();
    }
}

class FolderyViewHolder extends RecyclerView.ViewHolder {

    private FolderyAdapter folderyAdapter;
    private MultimediaFragment fragment;
    private String name;
    private int id;

    @Bind(R.id.lista_folderow)
    LinearLayout listaFolderow;
    @Bind(R.id.folder_name)
    TextView folderName;

    public FolderyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        AppHelper.changeListItemHeight(listaFolderow);
        listaFolderow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.gotoNextFolder(id, name);
            }
        });
    }

    public void setAdapter(FolderyAdapter folderyAdapter) {
        this.folderyAdapter = folderyAdapter;
    }

    public void setFragment(MultimediaFragment fragment) {
        this.fragment = fragment;
    }

    public void setName(String name) {
        this.name = name;
        folderName.setText(name);
    }

    public void setId(int id) {
        this.id = id;
    }
}