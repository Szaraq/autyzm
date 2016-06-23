package pl.osik.autyzm.lekcje;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.MyApp;
import pl.osik.autyzm.helpers.OperationsEnum;
import pl.osik.autyzm.helpers.orm.LekcjaORM;
import pl.osik.autyzm.sql.Lekcja;
import pl.osik.autyzm.uruchom.UruchomController;

/**
 * Created by m.osik2 on 2016-05-16.
 */
public class LekcjeAdapter extends RecyclerView.Adapter<LekcjeViewHolder> {

    public static final String BUNDLE_SWITCH_OPERACJA = "operacja";

    private ArrayList<LekcjaORM> lekcje = Lekcja.getLekcjaList(true);
    private final LekcjeFragment fragment;
    private final LayoutInflater layoutInflater;

    public LekcjeAdapter(LayoutInflater layoutInflater, LekcjeFragment fragment) {
        this.layoutInflater = layoutInflater;
        this.fragment = fragment;
    }

    @Override
    public LekcjeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_lekcji, parent, false);
        ButterKnife.bind(this, view);
        LekcjeViewHolder holder = new LekcjeViewHolder(view);
        holder.setAdapter(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(LekcjeViewHolder holder, int position) {
        holder.setFragment(fragment);
        holder.setLekcja(lekcje.get(position));
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

class LekcjeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private LekcjeFragment fragment;
    private LekcjeAdapter adapter;
    private LekcjaORM lekcja;

    @Bind(R.id.lekcja_name)
    TextView lekcjaName;
    @Bind(R.id.lekcja_context_menu)
    ImageView lekcjaContextMenu;

    public LekcjeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        lekcjaContextMenu.setOnClickListener(this);
        lekcjaName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UruchomController.runLekcja(fragment, lekcja);
            }
        });
    }

    public void setFragment(LekcjeFragment fragment) {
        this.fragment = fragment;
    }

    public void setAdapter(LekcjeAdapter adapter) {
        this.adapter = adapter;
    }

    public void setLekcja(LekcjaORM lekcja) {
        this.lekcja = lekcja;
        lekcjaName.setText(lekcja.getTytul());
    }

    @Override
    public void onClick(View v) {
        if(v == lekcjaContextMenu) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            AppHelper.setForceIconInPopupMenu(popupMenu);
            popupMenu.inflate(R.menu.lekcje_context_menu);
            popupMenu.setOnMenuItemClickListener(this);
            if(lekcja.isFavourite()) {
                MenuItem fav = popupMenu.getMenu().findItem(R.id.lekcje_favourite);
                fav.setTitle(R.string.favourite_remove)
                        .setIcon(R.drawable.ic_favourite_remove);
            }

            popupMenu.show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.lekcje_edit) {
            gotoDetails(OperationsEnum.EDYCJA);
        } else if(item.getItemId() == R.id.lekcje_delete) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(lekcjaContextMenu.getContext());
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
        } else if(item.getItemId() == R.id.lekcje_favourite) {
            Lekcja.setFavourite(lekcja.getId(), !lekcja.isFavourite());
            adapter.refresh();
        }
        return true;
    }

    protected void gotoDetails(OperationsEnum operacja) {
        Intent intent = new Intent(fragment.getActivity(), LekcjeTytulActivity.class);
        LekcjeHelper.setLekcja(lekcja);
        LekcjeHelper.setOperacja(operacja);
        fragment.startActivity(intent);
    }
}
