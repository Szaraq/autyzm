package pl.osik.autyzm.helpers.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.orm.PlikORM;
import pl.osik.autyzm.sql.Plik;

/**
 * Created by m.osik2 on 2016-06-17.
 */
public class PlikView extends CardView {

    PlikORM plik;

    @Bind(R.id.card_layout)
    CardView cardLayout;
    @Bind(R.id.plik_image)
    ImageView plikImage;
    @Bind(R.id.plik_name)
    TextView plikName;

    public PlikView(Context context) {
        super(context);
        init();
    }

    public PlikView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlikView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
    }
}
