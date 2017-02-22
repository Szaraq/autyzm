package pl.osik.autismemotion.multimedia;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ScrollView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autismemotion.R;
import pl.osik.autismemotion.helpers.AppHelper;
import pl.osik.autismemotion.helpers.FileHelper;
import pl.osik.autismemotion.helpers.FilePickerActivity;
import pl.osik.autismemotion.helpers.FilePlacingInterface;
import pl.osik.autismemotion.helpers.listeners.MyOnKeyEnterListener;
import pl.osik.autismemotion.helpers.orm.FolderORM;
import pl.osik.autismemotion.helpers.orm.PlikORM;
import pl.osik.autismemotion.helpers.views.FolderView;
import pl.osik.autismemotion.helpers.views.PlikView;
import pl.osik.autismemotion.sql.Folder;
import pl.osik.autismemotion.sql.Plik;
import pl.osik.autismemotion.sql.User;
import pl.osik.autismemotion.validate.ValidateCommand;
import pl.osik.autismemotion.validate.ValidateExistsInDatabase;
import pl.osik.autismemotion.validate.ValidateNotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MultimediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultimediaFragment extends Fragment implements View.OnClickListener, FilePlacingInterface {
    DrawerLayout drawerLayout;
    int folderId;
    String folderName;
    final ValidateCommand validate = new ValidateCommand();
    boolean chooser = false;
    ArrayList<FolderORM> foldery;
    ArrayList<PlikORM> pliki;

    final ArrayList<FolderView> folderyViews = new ArrayList<>();
    final ArrayList<PlikView> plikiViews = new ArrayList<>();

    public final static String CHOOSER = "chooser";

    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.fab_menu)
    FloatingActionMenu fabMenu;
    @Bind(R.id.multimedia_fab_folder)
    FloatingActionButton fabFolder;
    @Bind(R.id.multimedia_fab_plik)
    FloatingActionButton fabPlik;
    @Bind(R.id.foldery_layout)
    GridLayout folderyLayout;
    @Bind(R.id.pliki_layout)
    GridLayout plikiLayout;

    public MultimediaFragment() {
        // Required empty public constructor
    }

    public static MultimediaFragment newInstance() {
        MultimediaFragment fragment = new MultimediaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multimedia, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        if(bundle != null)
            chooser = bundle.getBoolean(CHOOSER, chooser);

        folderId = Folder.ROOT_ID;
        folderName = Folder.ROOT_NAME;

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(isRoot() ? getString(R.string.multimedia_title) : folderName);
        refresh();
        fabPlik.setOnClickListener(this);
        fabFolder.setOnClickListener(this);
        fabPlik.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
    }

    public void refresh() {
        createFolders();
        createPliki();
    }

    private void createFolders() {
        if(folderyLayout.getChildCount() > 0) {
            folderyLayout.removeViews(0, folderyLayout.getChildCount());
            folderyViews.clear();
        }

        foldery = Folder.getFolderyInFolder(folderId);

        for (final FolderORM folder : foldery) {
            FolderView view = new FolderView(getContext(), this, folder);
            folderyLayout.addView(view);
            folderyViews.add(view);
        }
    }

    private void createPliki() {
        if(plikiLayout.getChildCount() > 0) {
            plikiLayout.removeViews(0, plikiLayout.getChildCount());
            plikiViews.clear();
        }
        pliki = Plik.getPlikiInFolder(folderId, true);
        ViewGroup.OnClickListener plikOnclickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlikORM plik = ((PlikView) v).getPlik();
                if(chooser) {
                    Intent extra = new Intent();
                    extra.putExtra(FilePickerActivity.EXTRA_FILE_PATH, plik.getPath());
                    extra.putExtra(PlikORM.EXTRA_PLIK_ID, plik.getId());
                    MultimediaFragment.this.getActivity().setResult(FilePickerActivity.RESULT_OK, extra);
                    MultimediaFragment.this.getActivity().finish();
                } else {
                    Intent intent = new Intent(MultimediaFragment.this.getActivity(), ShowMediaActivity.class);
                    intent.putExtra(ShowMediaActivity.EXTRA_PLIK, plik);
                    MultimediaFragment.this.getActivity().startActivity(intent);
                }
            }
        };
        for (final PlikORM plik : pliki) {
            PlikView view = new PlikView(getContext(), this);
            view.setPlik(plik);
            plikiLayout.addView(view);
            view.setOnClickListener(plikOnclickListener);
            plikiViews.add(view);
        }
    }

    private boolean isRoot() {
        return Folder.isRoot(folderId);
    }

    public void onBackPressed() {
        if(isRoot()) {
            if(chooser) getActivity().finish();
            else return;
        }
        HashMap<String, Object> parent = Folder.getParentFolder(folderId);
        gotoNextFolder((int) parent.get(Folder.COLUMN_ID), (String) parent.get(Folder.COLUMN_NAZWA));
    }

    public void gotoNextFolder(int id, String name) {
        folderId = id;
        folderName = name;
        refresh();
    }

    /* FAB OnClick */
    @Override
    public void onClick(View v) {
        if(v.getId() == fabFolder.getId()) {
            askForFolderName();
        } else if(v.getId() == fabPlik.getId()) {
            FileHelper.FileManager.pickPhoto(this.getActivity(), ArrayUtils.addAll(FileHelper.FileManager.EXTENSION_ARRAY_PHOTO, FileHelper.FileManager.EXTENSION_ARRAY_VIDEO));
        }
        fabMenu.close(true);
    }

    private String inputTxt;

    private void askForFolderName() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        final EditText input = new EditText(this.getContext());
        input.setPadding(30, 30, 30, 30);
        input.setSingleLine(true);
        input.setHint(R.string.muti_nowy_folder_placeholder);
        builder.setTitle(R.string.multi_nowy_folder);
        input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        final ValidateNotNull validateNotNull = new ValidateNotNull();
        final ValidateExistsInDatabase validateExistsInDatabase = new ValidateExistsInDatabase(new Folder(), Folder.COLUMN_NAZWA);
        validate.addValidate(input, validateNotNull);
        validate.addValidate(input, validateExistsInDatabase);
        builder.setView(input)
                .setPositiveButton(getActivity().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (validate.doValidateAll()) {
                            inputTxt = input.getText().toString();
                            addNewFolder();
                        } else if (input.getText().length() == 0) {
                            AppHelper.showMessage(getView(), validateNotNull.getErrorMsg());
                        } else {
                            AppHelper.showMessage(getView(), validateExistsInDatabase.getErrorMsg());
                        }
                    }
                })
                .setNegativeButton(getActivity().getString(R.string.button_anuluj), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        input.setOnKeyListener(new MyOnKeyEnterListener(alert.getButton(AlertDialog.BUTTON_POSITIVE)));
    }

    private void addNewFolder() {
        Folder f = new Folder();
        ContentValues data = new ContentValues();
        data.put(Folder.COLUMN_NAZWA, inputTxt);
        data.put(Folder.COLUMN_FOLDER, folderId);
        f.insert(data);
        refresh();
    }

    private void addNewPlik(String path) {
        ValidateExistsInDatabase validateExistsInDatabase = new ValidateExistsInDatabase(new Plik(), Plik.COLUMN_PATH);
        String query = "SELECT * FROM " + validateExistsInDatabase.getTable().getTableName()
                + " JOIN " + Folder.TABLE_NAME + " ON " + Plik.TABLE_NAME + "." + Plik.COLUMN_FOLDER + "=" + Folder.TABLE_NAME + "." + Folder.COLUMN_ID
                + " WHERE " + validateExistsInDatabase.getColumn() + " = ?"
                + " AND " + Folder.TABLE_NAME + "." + Folder.COLUMN_USER + "=" + User.getCurrentId();
        if(validateExistsInDatabase.validateWithQuery(path, query)) {
            Plik p = new Plik();
            ContentValues data = new ContentValues();
            data.put(Plik.COLUMN_PATH, path);
            data.put(Plik.COLUMN_FOLDER, folderId);
            data.put(Plik.COLUMN_GHOST, 0);
            p.insert(data);
            refresh();
        } else {
            AppHelper.showMessage(getView(), R.string.validate_error_existsInDB_plik);
        }
    }

    @Override
    public void placeFile(String path) {
        addNewPlik(path);
    }

    public void changeToMoveFile(boolean fade, @Nullable PlikORM plik) {
        if(fade) AppHelper.showMessage(getView(), R.string.multimedia_choose_folder);
        PlikView.setFaded(fade, plik);
        FolderView.setHoover(fade);
        refresh();
        scrollView.fullScroll(View.FOCUS_UP);
    }


    @Override
    public void onPause() {
        super.onPause();
        changeToMoveFile(false, null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
