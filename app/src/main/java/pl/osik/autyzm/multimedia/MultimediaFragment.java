package pl.osik.autyzm.multimedia;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.FileHelper;
import pl.osik.autyzm.helpers.FilePickerActivity;
import pl.osik.autyzm.helpers.FilePlacingInterface;
import pl.osik.autyzm.helpers.listeners.MyOnKeyEnterListener;
import pl.osik.autyzm.helpers.orm.FolderORM;
import pl.osik.autyzm.helpers.orm.PlikORM;
import pl.osik.autyzm.helpers.views.FolderView;
import pl.osik.autyzm.helpers.views.PlikView;
import pl.osik.autyzm.sql.Folder;
import pl.osik.autyzm.sql.Plik;
import pl.osik.autyzm.validate.ValidateCommand;
import pl.osik.autyzm.validate.ValidateExistsInDatabase;
import pl.osik.autyzm.validate.ValidateNotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MultimediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultimediaFragment extends Fragment implements View.OnClickListener, FilePlacingInterface {

    //TODO RecyclerView wspólny dla folderów i plików - subheader
    //TODO Przenoszenie plików do folderów

    int folderId;
    String folderName;
    ValidateCommand validate = new ValidateCommand();
    boolean chooser = false;
    Bundle savedInstanceState;
    ArrayList<FolderORM> foldery;
    ArrayList<PlikORM> pliki;

    public final static String CHOOSER = "chooser";

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
        this.savedInstanceState = savedInstanceState;
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
        if(folderyLayout.getChildCount() > 0) folderyLayout.removeViews(0, folderyLayout.getChildCount());
        foldery = Folder.getFolderyInFolder(folderId);
        ViewGroup.OnClickListener folderOnclickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FolderORM folder = ((FolderView) v).getFolder();
                gotoNextFolder(folder.getId(), folder.getNazwa());
            }
        };
        for (final FolderORM folder : foldery) {
            FolderView view = new FolderView(getContext(), this, folder);
            folderyLayout.addView(view);
            view.setOnClickListener(folderOnclickListener);
        }
    }

    private void createPliki() {
        if(plikiLayout.getChildCount() > 0) plikiLayout.removeViews(0, plikiLayout.getChildCount());
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
            PlikView view = new PlikView(getContext());
            view.setPlik(plik);
            plikiLayout.addView(view);
            view.setOnClickListener(plikOnclickListener);
        }
    }

    private boolean isRoot() {
        return Folder.isRoot(folderId);
    }

    public void onBackPressed() {
        if(isRoot()) return;
        HashMap<String, Object> parent = Folder.getParentFolder(folderId);
        gotoNextFolder((int) parent.get(Folder.COLUMN_ID), (String) parent.get(Folder.COLUMN_NAZWA));
    }

    protected void gotoNextFolder(int id, String name) {
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
        if(validateExistsInDatabase.validate(path)) {
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
}
