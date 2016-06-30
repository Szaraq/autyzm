package pl.osik.autyzm.uruchom;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.dzieci.DzieciDetailsActivity;
import pl.osik.autyzm.helpers.MyPreDrawListener;
import pl.osik.autyzm.sql.Dziecko;

/**
 * Created by m.osik2 on 2016-05-20.
 */
public class WyborDzieckaAdapter extends RecyclerView.Adapter<WyborDzieckaViewHolder> {

    private final Activity activity;
    ArrayList<HashMap<String, Object>> dzieciList = Dziecko.getDzieciList();

    public WyborDzieckaAdapter(LayoutInflater layoutInflater, Activity activity) {
        this.activity = activity;
    }

    @Override
    public WyborDzieckaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_wybor_dzieci, parent, false);
        ButterKnife.bind(this, view);
        WyborDzieckaViewHolder holder = new WyborDzieckaViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(WyborDzieckaViewHolder holder, int position) {
        holder.setActivity(activity);
        HashMap<String, Object> map = dzieciList.get(position);
        String photo = (String) map.get(Dziecko.COLUMN_PHOTO);
        String name = map.get(Dziecko.COLUMN_NAZWISKO) + " " + map.get(Dziecko.COLUMN_IMIE);
        int id = (int) map.get(Dziecko.COLUMN_ID);
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

class WyborDzieckaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private int id;
    private String name;
    private String photo;
    private Activity activity;

    @Bind(R.id.containerLayout)
    LinearLayout containerLayout;
    @Bind(R.id.dzieci_name)
    TextView dzieciName;
    @Bind(R.id.dzieci_photo)
    ImageView dzieciPhoto;

    public WyborDzieckaViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        containerLayout.setOnClickListener(this);
    }

    public void setPhoto(String photo) {
        this.photo = photo;
        if(photo == null || !(new File(photo)).exists()) {
            dzieciPhoto.setImageResource(DzieciDetailsActivity.RESOURCE_NO_PHOTO);
        } else {
            ViewTreeObserver vto = dzieciPhoto.getViewTreeObserver();
            vto.addOnPreDrawListener(new MyPreDrawListener(dzieciPhoto, photo, activity));
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
        UruchomController.setIdDziecka(id);
        UruchomController.gotoNextActivity(activity);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}