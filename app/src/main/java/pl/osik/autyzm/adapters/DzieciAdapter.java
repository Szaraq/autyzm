package pl.osik.autyzm.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import pl.osik.autyzm.sql.Dziecko;

/**
 * Created by m.osik2 on 2016-04-21.
 */
public class DzieciAdapter extends RecyclerView.Adapter<DzieciViewHolder> {

    ArrayList<HashMap<String, String>> dzieciList = Dziecko.getDzieciList();
    private final LayoutInflater layoutInflater;

    public DzieciAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    @Override
    public DzieciViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new DzieciViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DzieciViewHolder holder, int position) {
        HashMap<String, String> map = dzieciList.get(position);
        String photo = map.get(Dziecko.COLUMN_PHOTO);
        String name = map.get(Dziecko.COLUMN_NAZWISKO) + " " + map.get(Dziecko.COLUMN_IMIE);
        holder.setPhoto(photo);
        holder.setName(name);
    }

    @Override
    public int getItemCount() {
        return dzieciList.size();
    }
}

class DzieciViewHolder extends RecyclerView.ViewHolder {

    private String photo;
    private TextView textView;
    private String name;

    public DzieciViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setName(String name) {
        this.name = name;
        textView.setText(name);
    }

    public String getName() {
        return name;
    }
}