package pl.osik.autyzm.multimedia;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.sql.Folder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MultimediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultimediaFragment extends Fragment {
    protected final int ROOT_ID = -1;
    protected final String ROOT_NAME = "ROOT";

    int folderId;
    String folderName;

    @Bind(R.id.folder_fab)
    FloatingActionButton fab;
    @Bind(R.id.foldery_list)
    RecyclerView folderyList;
    @Bind(R.id.pliki_list)
    RecyclerView plikiList;

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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(isRoot() ? getString(R.string.multimedia_title) : folderName);
        Bundle bundle = getArguments();
        if(bundle == null) {
            folderId = ROOT_ID;
            folderName = ROOT_NAME;
        } else {
            folderId = bundle.getInt(Folder.COLUMN_ID);
            folderName = bundle.getString(Folder.COLUMN_NAZWA);
        }
    }

    public boolean isRoot() {
        return folderId == ROOT_ID;
    }

}
