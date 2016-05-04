package pl.osik.autyzm.multimedia;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.dzieci.DzieciAdapter;
import pl.osik.autyzm.sql.Folder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MultimediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultimediaFragment extends Fragment {

    int folderId;
    String folderName;

    @Bind(R.id.folder_fab)
    FloatingActionButton fab;
    @Bind(R.id.foldery_list)
    RecyclerView folderyList;
    @Bind(R.id.pliki_list)
    RecyclerView plikiList;

    private FolderyAdapter folderyAdapter;

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
        if(bundle == null) {
            folderId = Folder.ROOT_ID;
            folderName = Folder.ROOT_NAME;
        } else {
            folderId = bundle.getInt(Folder.COLUMN_ID);
            folderName = bundle.getString(Folder.COLUMN_NAZWA);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(isRoot() ? getString(R.string.multimedia_title) : folderName);
        folderyAdapter = new FolderyAdapter(getLayoutInflater(savedInstanceState), this, folderId);
        folderyList.setLayoutManager(new GridLayoutManager(this.getActivity(), 2));
        folderyList.setAdapter(folderyAdapter);
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
        Fragment newFragment = new MultimediaFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Folder.COLUMN_ID, id);
        bundle.putString(Folder.COLUMN_NAZWA, name);
        newFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerLayout, newFragment);
        fragmentTransaction.commit();
    }

}
