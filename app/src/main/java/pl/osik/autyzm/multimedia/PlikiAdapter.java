package pl.osik.autyzm.multimedia;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.orm.PlikORM;
import pl.osik.autyzm.sql.Folder;
import pl.osik.autyzm.sql.Plik;

/**
 * Created by m.osik2 on 2016-05-05.
 */
public class PlikiAdapter extends RecyclerView.Adapter<PlikiViewHolder> {

    private LayoutInflater layoutInflater;
    private MultimediaFragment fragment;
    private int idFolder;
    private ArrayList<PlikORM> plikiList;

    public PlikiAdapter(LayoutInflater layoutInflater, Fragment fragment, int idFolder) {
        this.layoutInflater = layoutInflater;
        this.fragment = (MultimediaFragment) fragment;
        this.idFolder = idFolder;
        this.plikiList = Folder.getPlikiInFolder(idFolder);
    }

    @Override
    public PlikiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_plikow, parent, false);
        PlikiViewHolder holder = new PlikiViewHolder(view);
        holder.setAdapter(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(PlikiViewHolder holder, int position) {
        holder.setFragment(fragment);
        holder.setPlik(plikiList.get(position));
    }

    @Override
    public int getItemCount() {
        return plikiList.size();
    }

    public void refresh() {
        plikiList = Folder.getPlikiInFolder(idFolder);
        notifyDataSetChanged();
    }
}

class PlikiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private PlikiAdapter adapter;
    private PlikORM plik;
    private MultimediaFragment fragment;

    @Bind(R.id.plik_name)
    TextView plikName;
    @Bind(R.id.plik_image)
    ImageView plikImage;
    @Bind(R.id.lista_plikow)
    LinearLayout listaPlikow;

    public PlikiViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        listaPlikow.setOnClickListener(this);
    }

    public void setAdapter(PlikiAdapter adapter) {
        this.adapter = adapter;
    }

    public void setPlik(PlikORM plik) {
        this.plik = plik;
        plikName.setText(plik.getName());
        //TODO naprawić Thumbnail
        Bitmap thumbnail = Plik.getThumbnail(fragment.getActivity(), plik.getPath());
        if(thumbnail == null) {
            Glide.with(plikImage.getContext())
                    .load("")
                    .placeholder(R.drawable.ic_test_plik)
                    .into(plikImage);
        } else {
            Glide.with(plikImage.getContext())
                    .load(thumbnail)
                    .into(plikImage);
        }
    }

    public void setFragment(MultimediaFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onClick(View v) {
        //TODO onClickListener
    }

}