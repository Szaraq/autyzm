package pl.osik.autyzm.multimedia;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.MyApp;
import pl.osik.autyzm.helpers.MySortedMap;
import pl.osik.autyzm.helpers.OperationsEnum;
import pl.osik.autyzm.helpers.listeners.MyOnKeyEnterListener;
import pl.osik.autyzm.main.StartFragment;
import pl.osik.autyzm.sql.Dziecko;
import pl.osik.autyzm.sql.Folder;
import pl.osik.autyzm.validate.ValidateCommand;
import pl.osik.autyzm.validate.ValidateNotNull;

/**
 * Created by m.osik2 on 2016-05-04.
 */
public class FolderyAdapter extends RecyclerView.Adapter<FolderyViewHolder> {

    private MultimediaFragment fragment;
    private final LayoutInflater layoutInflater;
    private final int idFolder;
    private MySortedMap foldery;

    public FolderyAdapter(LayoutInflater layoutInflater, Fragment fragment, int idFolder) {
        this.layoutInflater = layoutInflater;
        this.fragment = (MultimediaFragment) fragment;
        this.idFolder = idFolder;
        this.foldery = Folder.getFolderyInFolder(idFolder);
    }

    @Override
    public FolderyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_folderow, parent, false);
        FolderyViewHolder holder = new FolderyViewHolder(view);
        holder.setAdapter(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(FolderyViewHolder holder, int position) {
        holder.setFragment(fragment);
        String name = foldery.get(position);
        int id = foldery.get(name);
        holder.setName(name);
        holder.setId(id);
    }

    @Override
    public int getItemCount() {
        return foldery.size();
    }

    public void refresh() {
        foldery = Folder.getFolderyInFolder(idFolder);
        notifyDataSetChanged();
    }
}

class FolderyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private FolderyAdapter folderyAdapter;
    private MultimediaFragment fragment;
    private String name;
    private int id;
    private ValidateCommand validate;

    @Bind(R.id.foldery_context_menu)
    ImageView folderyContextMenu;
    @Bind(R.id.lista_folderow)
    LinearLayout listaFolderow;
    @Bind(R.id.folder_name)
    TextView folderName;

    public FolderyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        //AppHelper.changeListItemHeight(listaFolderow);
        folderyContextMenu.setOnClickListener(this);
        listaFolderow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.gotoNextFolder(id, name);
            }
        });
    }

    public void setAdapter(FolderyAdapter folderyAdapter) {
        this.folderyAdapter = folderyAdapter;
    }

    public void setFragment(MultimediaFragment fragment) {
        this.fragment = fragment;
    }

    public void setName(String name) {
        this.name = name;
        folderName.setText(name);
    }

    public void setId(int id) {
        this.id = id;
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
            dialog.setMessage(MyApp.getContext().getString(R.string.message_dziecko_do_usunięcia) + " " + name + "?")
                    .setTitle(R.string.popup_uwaga)
                    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Folder f = new Folder();
                            f.delete(id);
                            folderyAdapter.refresh();
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
        builder.setTitle(fragment.getResources().getString(R.string.multi_change_name_for) + " " + name);
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
        f.edit(id, data);
        folderyAdapter.refresh();
    }
}