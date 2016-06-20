package pl.osik.autyzm.helpers.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.FileHelper;
import pl.osik.autyzm.helpers.MyApp;
import pl.osik.autyzm.helpers.orm.PlikORM;
import pl.osik.autyzm.multimedia.MultimediaFragment;
import pl.osik.autyzm.sql.Folder;
import pl.osik.autyzm.sql.Plik;

/**
 * Created by m.osik2 on 2016-06-17.
 */
public class PlikView extends CardView {

    PlikORM plik;
    private MultimediaFragment fragment;

    @Bind(R.id.multimedia_icon)
    ImageView multimediaIcon;
    @Bind(R.id.card_layout)
    CardView cardLayout;
    @Bind(R.id.plik_image)
    ImageView plikImage;
    @Bind(R.id.plik_name)
    TextView plikName;
    @Bind(R.id.multimedia_context_menu)
    ImageView contextMenu;

    public PlikView(Context context, MultimediaFragment fragment) {
        super(context);
        this.fragment = fragment;
        init();
    }

    public PlikView(Context context, AttributeSet attrs, MultimediaFragment fragment) {
        super(context, attrs);
        this.fragment = fragment;
        init();
    }

    public PlikView(Context context, AttributeSet attrs, int defStyleAttr, MultimediaFragment fragment) {
        super(context, attrs, defStyleAttr);
        this.fragment = fragment;
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.lista_plikow, this);
        ButterKnife.bind(this, view);
        if(FolderView.width == 0) FolderView.width = (AppHelper.getScreenSize()[0] - AppHelper.dip2px(2) * 3) / 2;      //(szerokość ekranu - margines lewy - margines między kartami - margines prawy) / 2 karty w rzędzie
        ViewGroup.LayoutParams params = cardLayout.getLayoutParams();
        params.width = FolderView.width;
        cardLayout.setLayoutParams(params);
    }

    public PlikORM getPlik() {
        return plik;
    }

    public void setPlik(PlikORM plik) {
        this.plik = plik;
        plikName.setText(plik.getName(true));

        String thumbnail = Plik.getThumbAbsolutePath(plik);
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
        setIcon();
        setContextMenu();
    }

    private void setIcon() {
        @DrawableRes int icon;
        @ColorRes int iconColor;
        if(FileHelper.getType(plik.getPath()) == FileHelper.FileTypes.PHOTO) {
            icon = R.drawable.ic_filetype_image;
            iconColor = R.color.colorMultimediaIconImage;
        } else {
            icon = R.drawable.ic_filetype_movie;
            iconColor = R.color.colorMultimediaIconVideo;
        }
        multimediaIcon.setImageResource(icon);
        multimediaIcon.setColorFilter(getResources().getColor(iconColor));
    }

    private void setContextMenu() {
        contextMenu.setOnClickListener(new ContextMenuListener(this));
    }

    public MultimediaFragment getFragment() {
        return fragment;
    }

    public static class ContextMenuListener implements OnClickListener, PopupMenu.OnMenuItemClickListener {
        PlikView plikView;
        PlikORM plik;

        public ContextMenuListener(PlikView plikView) {
            this.plikView = plikView;
            plik = plikView.getPlik();
        }

        @Override
        public void onClick(View v) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            AppHelper.setForceIconInPopupMenu(popupMenu);
            popupMenu.inflate(R.menu.pliki_context_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(item.getItemId() == R.id.pliki_move) {

            } else if(item.getItemId() == R.id.pliki_delete) {
                //Zapytaj czy na pewno chcesz usunąć
                AlertDialog.Builder dialog = new AlertDialog.Builder(plikView.getContext());
                dialog.setMessage(MyApp.getContext().getString(R.string.message_dziecko_do_usunięcia) + " " + plik.getName(true) + "?")
                        .setTitle(R.string.popup_uwaga)
                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Plik p = new Plik();
                                p.delete(plik.getId());
                                plikView.getFragment().refresh();
                                AppHelper.showMessage(plikView, R.string.plik_usuniety);
                            }
                        })
                        .setNegativeButton(R.string.button_anuluj, null)
                        .setIcon(R.drawable.ic_uwaga);
                dialog.show();
            }
            return true;
        }
    }
}
