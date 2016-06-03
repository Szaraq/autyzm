package pl.osik.autyzm.uruchom;

import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.FileHelper;
import pl.osik.autyzm.helpers.orm.LekcjaORM;
import pl.osik.autyzm.helpers.orm.ModulORM;
import pl.osik.autyzm.helpers.orm.PlikORM;
import pl.osik.autyzm.sql.Lekcja;
import pl.osik.autyzm.sql.Modul;
import pl.osik.autyzm.sql.Plik;

/**
 * Created by m.osik2 on 2016-05-16.
 */
public class UruchomAdapter extends RecyclerView.Adapter<UruchomViewHolder> {

    public static final String BUNDLE_SWITCH_OPERACJA = "operacja";

    private ArrayList<LekcjaORM> lekcje = Lekcja.getLekcjaList();
    private UruchomFragment fragment;
    private final LayoutInflater layoutInflater;

    public UruchomAdapter(LayoutInflater layoutInflater, UruchomFragment fragment) {
        this.layoutInflater = layoutInflater;
        this.fragment = fragment;
    }

    @Override
    public UruchomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_lekcji_uruchom, parent, false);
        ButterKnife.bind(this, view);
        UruchomViewHolder holder = new UruchomViewHolder(view);
        holder.setAdapter(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(UruchomViewHolder holder, int position) {
        holder.setFragment(fragment);
        holder.setLekcja(lekcje.get(position));
        holder.createThumbnails();
    }

    @Override
    public int getItemCount() {
        return lekcje.size();
    }

    public void refresh() {
        lekcje = Lekcja.getLekcjaList();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return lekcje.size() == 0;
    }
}

class UruchomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private UruchomFragment fragment;
    private UruchomAdapter adapter;
    private LekcjaORM lekcja;
    private ArrayList<ModulORM> moduly;

    @Bind(R.id.containerLayout)
    CardView containerLayout;
    @Bind(R.id.thumbnailsContainer)
    LinearLayout thumbnailsContainer;
    @Bind(R.id.lekcja_name)
    TextView lekcjaName;
    @Bind(R.id.moduly_lewy)
    TextView modulyLewy;
    @Bind(R.id.moduly_prawy)
    TextView modulyPrawy;
    @Bind(R.id.favourite)
    ImageView favourite;

    public UruchomViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        containerLayout.setOnClickListener(this);
    }

    public void setFragment(UruchomFragment fragment) {
        this.fragment = fragment;
    }

    public void setAdapter(UruchomAdapter adapter) {
        this.adapter = adapter;
    }

    public void setLekcja(final LekcjaORM lekcja) {
        this.lekcja = lekcja;
        lekcjaName.setText(lekcja.getTytul());
        setModuly(Modul.getModulyForLekcja(lekcja.getId()));

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lekcja.setFavourite(lekcja.getId(), !lekcja.isFavourite(), favourite);
            }
        });
        Lekcja.changeFavouriteIcon(favourite, lekcja.isFavourite());
    }

    private void setModuly(ArrayList<ModulORM> moduly) {
        this.moduly = moduly;
        boolean nextLewy = true;
        TextView nextTextView;
        for (ModulORM modul : moduly) {
            nextTextView = nextLewy ? modulyLewy : modulyPrawy;
            nextTextView.setText(modul.getShortName());
            nextLewy = !nextLewy;
        }
    }

    @Override
    public void onClick(View v) {
        UruchomController.runLekcja(fragment, lekcja);
    }

    protected void createThumbnails() {
        for (ModulORM modul : moduly) {
            PlikORM plik = Plik.getById(modul.getPlik());
            Bitmap bitmap = FileHelper.rescaleBitmap(plik.getPath(), FileHelper.RESCALE_PROPORTIONALLY, thumbnailsContainer.getLayoutParams().height);
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * thumbnailsContainer.getLayoutParams().height / bitmap.getHeight(), thumbnailsContainer.getLayoutParams().height, false);
            ImageView thumb = new ImageView(fragment.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(bitmap.getWidth(), ViewGroup.LayoutParams.MATCH_PARENT);
            thumb.setLayoutParams(params);
            thumb.setImageBitmap(bitmap);
            thumbnailsContainer.addView(thumb);
        }
    }
}
