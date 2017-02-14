package pl.osik.autismemotion.uruchom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.AppHelper;
import pl.osik.autismemotion.helpers.FileHelper;
import pl.osik.autismemotion.helpers.MyApp;
import pl.osik.autismemotion.helpers.MyPreDrawListener;
import pl.osik.autismemotion.helpers.OperationsEnum;
import pl.osik.autismemotion.helpers.orm.LekcjaORM;
import pl.osik.autismemotion.helpers.orm.ModulORM;
import pl.osik.autismemotion.helpers.orm.PlikORM;
import pl.osik.autismemotion.lekcje.LekcjeHelper;
import pl.osik.autismemotion.lekcje.LekcjeTytulActivity;
import pl.osik.autismemotion.sql.Lekcja;
import pl.osik.autismemotion.sql.Modul;
import pl.osik.autismemotion.sql.Plik;

/**
 * Created by m.osik2 on 2016-05-16.
 */
public class UruchomAdapter extends RecyclerView.Adapter<UruchomViewHolder> {

    public static final String BUNDLE_SWITCH_OPERACJA = "operacja";

    private ArrayList<LekcjaORM> lekcje = Lekcja.getLekcjaList(true);
    private final UruchomFragment fragment;

    public UruchomAdapter(LayoutInflater layoutInflater, UruchomFragment fragment) {
        LayoutInflater layoutInflater1 = layoutInflater;
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
        lekcje = Lekcja.getLekcjaList(true);
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
    FrameLayout thumbnailsContainer;
    @Bind(R.id.thumbnail)
    ImageView thumbnail;
    @Bind(R.id.lekcja_name)
    TextView lekcjaName;
    @Bind(R.id.moduly_container)
    LinearLayout modulyContainer;
    @Bind(R.id.favourite)
    ImageView favourite;
    @Bind(R.id.buttonDelete)
    Button buttonDelete;
    @Bind(R.id.buttonEdit)
    Button buttonEdit;

    public UruchomViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        containerLayout.setOnClickListener(this);
        adjustImageTo16x9();
    }

    private void adjustImageTo16x9() {
        LinearLayout.LayoutParams cardParams = (LinearLayout.LayoutParams) containerLayout.getLayoutParams();
        int width = AppHelper.getScreenSize()[0] - cardParams.leftMargin - cardParams.rightMargin; //px
        int height = AppHelper.getHeightForRatio(width, 16, 9);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) thumbnailsContainer.getLayoutParams();
        params.height = height;
        thumbnailsContainer.setLayoutParams(params);
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
        setModuly(Modul.getModulyForLekcja(lekcja.getId(), true));

        favourite.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);
        Lekcja.changeFavouriteIcon(favourite, lekcja.isFavourite());
    }

    private void setModuly(ArrayList<ModulORM> moduly) {
        if(modulyContainer.getChildCount() > 1) modulyContainer.removeViews(1, modulyContainer.getChildCount()-1);
        this.moduly = moduly;
        for (ModulORM modul : moduly) {
            TextView modulView = new TextView(fragment.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(AppHelper.dip2px(16), 0, AppHelper.dip2px(16), 0);
            modulView.setTextColor(fragment.getContext().getResources().getColor(android.R.color.black));
            modulView.setText(modul.getName());
            modulView.setSingleLine(true);
            modulView.setEllipsize(TextUtils.TruncateAt.END);

            modulView.setLayoutParams(params);
            modulyContainer.addView(modulView);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == favourite.getId()) {
            lekcja.setFavourite(!lekcja.isFavourite());
            Lekcja.setFavourite(lekcja.getId(), lekcja.isFavourite(), favourite);
        } else if(v.getId() == buttonDelete.getId()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(fragment.getContext());
            dialog.setMessage(MyApp.getContext().getString(R.string.message_dziecko_do_usunięcia) + " " + lekcja.getTytul() + "?")
                    .setTitle(R.string.popup_uwaga)
                    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Lekcja l = new Lekcja();
                            l.delete(lekcja.getId());
                            adapter.refresh();
                            AppHelper.showMessage(fragment.getView(), R.string.message_lekcja_usunięta);
                        }
                    })
                    .setNegativeButton(R.string.button_anuluj, null)
                    .setIcon(R.drawable.ic_uwaga);
            dialog.show();
        } else if(v.getId() == buttonEdit.getId()) {
            gotoDetails(OperationsEnum.EDYCJA);
        } else {
            UruchomController.runLekcja(fragment, lekcja);
        }
    }

    protected void createThumbnails() {
        ViewTreeObserver vto = thumbnail.getViewTreeObserver();
        PlikORM plik = Plik.getById(moduly.get(0).getPlik(), true);
        String path = plik.getPath();
        if(plik.getType() == FileHelper.FileTypes.PHOTO) {
            vto.addOnPreDrawListener(new MyPreDrawListener(thumbnail, path, fragment.getActivity()));
            thumbnail.setColorFilter(Color.argb(50, 0, 0, 0), PorterDuff.Mode.DARKEN);
        } else if(plik.getType() == FileHelper.FileTypes.VIDEO) {
            Bitmap bitmap = FileHelper.getThumbnail(path, 0, AppHelper.dip2px(150));
            vto.addOnPreDrawListener(new MyPreDrawListener(thumbnail, bitmap, fragment.getActivity()));
        }
    }

    protected void gotoDetails(OperationsEnum operacja) {
        Intent intent = new Intent(fragment.getActivity(), LekcjeTytulActivity.class);
        LekcjeHelper.setLekcja(lekcja);
        LekcjeHelper.setOperacja(operacja);
        fragment.startActivity(intent);
    }
}
