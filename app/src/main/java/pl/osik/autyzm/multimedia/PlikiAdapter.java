package pl.osik.autyzm.multimedia;

import android.content.Intent;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
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
import pl.osik.autyzm.helpers.FileHelper;
import pl.osik.autyzm.helpers.FilePickerActivity;
import pl.osik.autyzm.helpers.orm.PlikORM;
import pl.osik.autyzm.sql.Plik;

/**
 * Created by m.osik2 on 2016-05-05.
 */
public class PlikiAdapter extends RecyclerView.Adapter<PlikiViewHolder> {

    private LayoutInflater layoutInflater;
    private MultimediaFragment fragment;
    private int idFolder;
    private ArrayList<PlikORM> plikiList;
    private boolean chooser;

    public PlikiAdapter(LayoutInflater layoutInflater, Fragment fragment, int idFolder, boolean chooser) {
        this.layoutInflater = layoutInflater;
        this.chooser = chooser;
        this.fragment = (MultimediaFragment) fragment;
        this.idFolder = idFolder;
        this.plikiList = Plik.getPlikiInFolder(idFolder, true);
    }

    @Override
    public PlikiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_plikow, parent, false);
        PlikiViewHolder holder = new PlikiViewHolder(view, chooser);
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
        plikiList = Plik.getPlikiInFolder(idFolder, true);
        notifyDataSetChanged();
    }
}

class PlikiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private PlikiAdapter adapter;
    private PlikORM plik;
    private MultimediaFragment fragment;
    private boolean chooser;

    @Bind(R.id.multimedia_icon)
    ImageView multimediaIcon;
    @Bind(R.id.plik_name)
    TextView plikName;
    @Bind(R.id.plik_image)
    ImageView plikImage;
    @Bind(R.id.lista_plikow)
    LinearLayout listaPlikow;

    public PlikiViewHolder(View itemView, boolean chooser) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.chooser = chooser;
        listaPlikow.setOnClickListener(this);
    }

    public void setAdapter(PlikiAdapter adapter) {
        this.adapter = adapter;
    }

    public void setPlik(PlikORM plik) {
        this.plik = plik;
        plikName.setText(plik.getShortName(true));
        String thumbnail = Plik.getThumbAbsolutePath(plik.getId());
        if(thumbnail == null) {
            Glide.with(plikImage.getContext())
                    .load("")
                    .placeholder(R.drawable.ic_test_plik)
                    .into(plikImage);
        } else {
            Glide.with(plikImage.getContext())
                    .load(thumbnail)
                    .centerCrop()
                    .into(plikImage);
        }
        setIcon(plik.getPath());
    }

    private void setIcon(String path) {
        @DrawableRes int icon;
        @ColorRes int iconColor;
        if(FileHelper.getType(path) == FileHelper.FileTypes.PHOTO) {
            icon = R.drawable.ic_filetype_image;
            iconColor = R.color.colorMultimediaIconImage;
        } else {
            icon = R.drawable.ic_filetype_movie;
            iconColor = R.color.colorMultimediaIconVideo;
        }
        multimediaIcon.setImageResource(icon);
        multimediaIcon.setColorFilter(fragment.getResources().getColor(iconColor));
    }

    public void setFragment(MultimediaFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onClick(View v) {
        if(chooser) {
            Intent extra = new Intent();
            extra.putExtra(FilePickerActivity.EXTRA_FILE_PATH, plik.getPath());
            extra.putExtra(PlikORM.EXTRA_PLIK_ID, plik.getId());
            fragment.getActivity().setResult(FilePickerActivity.RESULT_OK, extra);
            fragment.getActivity().finish();
        } else {
            Intent intent = new Intent(fragment.getActivity(), ShowMediaActivity.class);
            intent.putExtra(ShowMediaActivity.EXTRA_PLIK, plik);
            fragment.getActivity().startActivity(intent);
        }
    }

}