package pl.osik.autyzm.helpers.views;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.MyApp;
import pl.osik.autyzm.helpers.listeners.MyOnKeyEnterListener;
import pl.osik.autyzm.helpers.orm.FolderORM;
import pl.osik.autyzm.multimedia.MultimediaFragment;
import pl.osik.autyzm.sql.Folder;
import pl.osik.autyzm.sql.Plik;
import pl.osik.autyzm.validate.ValidateCommand;
import pl.osik.autyzm.validate.ValidateNotNull;

/**
 * Created by m.osik2 on 2016-06-17.
 */
public class FolderView extends CardView implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {

    protected static int width = 0;
    final FolderORM folder;
    private final MultimediaFragment fragment;
    ValidateCommand validate;
    public static boolean hoover;

    @Bind(R.id.card_layout)
    CardView cardLayout;
    @Bind(R.id.folder_name)
    TextView folderName;
    @Bind(R.id.foldery_context_menu)
    ImageView folderyContextMenu;

    public FolderView(Context context, MultimediaFragment fragment, FolderORM folder) {
        super(context);
        this.fragment = fragment;
        this.folder = folder;
        init();
    }

    public FolderView(Context context, AttributeSet attrs, MultimediaFragment fragment, FolderORM folder) {
        super(context, attrs);
        this.fragment = fragment;
        this.folder = folder;
        init();
    }

    public FolderView(Context context, AttributeSet attrs, int defStyleAttr, MultimediaFragment fragment, FolderORM folder) {
        super(context, attrs, defStyleAttr);
        this.fragment = fragment;
        this.folder = folder;
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.lista_folderow, this);
        ButterKnife.bind(this, view);
        if(width == 0) width = (AppHelper.getScreenSize()[0] - AppHelper.dip2px(2) * 3) / 2;      //(szerokość ekranu - margines lewy - margines między kartami - margines prawy) / 2 karty w rzędzie
        ViewGroup.LayoutParams params = cardLayout.getLayoutParams();
        params.width = width;
        cardLayout.setLayoutParams(params);
        folderyContextMenu.setOnClickListener(this);
        folderName.setText(folder.getNazwa());
        changeToMoveMode();
    }

    public void changeToMoveMode() {
        OnClickListener folderOnClickListener = null;
        int elevation = 0;
        if(hoover) {
            elevation = AppHelper.dip2px(8);
            folderOnClickListener = new MoveToFolderOnClickListener(fragment);
        } else {
            elevation = AppHelper.dip2px(2);
            folderOnClickListener = new ChangeFolderOnClickListener(fragment);
        }
        cardLayout.setCardElevation(elevation);
        setOnClickListener(folderOnClickListener);
    }

    public FolderORM getFolder() {
        return folder;
    }

    @Override
    public void onClick(View v) {
        if(v == folderyContextMenu) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            AppHelper.setForceIconInPopupMenu(popupMenu);
            popupMenu.inflate(R.menu.foldery_context_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.foldery_edit) {
            askForFolderName();
        } else {
            //Zapytaj czy na pewno chcesz usunąć
            AlertDialog.Builder dialog = new AlertDialog.Builder(folderyContextMenu.getContext());
            dialog.setMessage(MyApp.getContext().getString(R.string.message_dziecko_do_usunięcia) + " " + folder.getNazwa() + "?")
                    .setTitle(R.string.popup_uwaga)
                    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Folder f = new Folder();
                            f.delete(folder.getId());
                            fragment.refresh();
                            AppHelper.showMessage(fragment.getView(), R.string.folder_usuniety);
                        }
                    })
                    .setNegativeButton(R.string.button_anuluj, null)
                    .setIcon(R.drawable.ic_uwaga);
            dialog.show();
        }
        return true;
    }

    private void askForFolderName() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        final EditText input = new EditText(fragment.getContext());
        input.setPadding(30, 30, 30, 30);
        input.setSingleLine(true);
        input.setHint(R.string.muti_edit_folder_placeholder);
        builder.setTitle(fragment.getResources().getString(R.string.multi_change_name_for) + " " + folder.getNazwa());
        input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        final ValidateNotNull validateNotNull = new ValidateNotNull();
        validate = new ValidateCommand();
        validate.addValidate(input, validateNotNull);
        builder.setView(input)
                .setPositiveButton(fragment.getActivity().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String error = validateNotNull.getErrorMsg();
                        if (validate.doValidateAll()) {
                            String inputTxt = input.getText().toString();
                            editFolderName(inputTxt);
                        } else {
                            AppHelper.showMessage(fragment.getView(), error);
                        }
                    }
                })
                .setNegativeButton(fragment.getActivity().getString(R.string.button_anuluj), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        input.setOnKeyListener(new MyOnKeyEnterListener(alert.getButton(AlertDialog.BUTTON_POSITIVE)));
    }

    private void editFolderName(String inputTxt) {
        Folder f = new Folder();
        ContentValues data = new ContentValues();
        data.put(Folder.COLUMN_NAZWA, inputTxt);
        f.edit(folder.getId(), data);
        fragment.refresh();
    }

    public static void setHoover(boolean hoover) {
        FolderView.hoover = hoover;
    }
}

class ChangeFolderOnClickListener implements View.OnClickListener {
    final MultimediaFragment fragment;

    public ChangeFolderOnClickListener(MultimediaFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onClick(View v) {
        FolderORM folder = ((FolderView) v).getFolder();
        fragment.gotoNextFolder(folder.getId(), folder.getNazwa());
    }
}

class MoveToFolderOnClickListener implements View.OnClickListener {
    final MultimediaFragment fragment;

    public MoveToFolderOnClickListener(MultimediaFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onClick(View v) {

        FolderORM folder = ((FolderView) v).getFolder();
        int plikId = PlikView.getNotFadedPlikId();

        Plik p = new Plik();
        ContentValues data = new ContentValues();
        data.put(Plik.COLUMN_FOLDER, folder.getId());
        p.edit(plikId, data);

        fragment.changeToMoveFile(false, null);
    }
}