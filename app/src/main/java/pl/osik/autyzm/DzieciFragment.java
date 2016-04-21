package pl.osik.autyzm;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.adapters.DzieciAdapter;


public class DzieciFragment extends Fragment {

    @Bind(R.id.dzieci_list)
    RecyclerView dzieciList;

    private DzieciAdapter dzieciAdapter;

    public DzieciFragment() {
        // Required empty public constructor
    }

    public static DzieciFragment newInstance() {
        DzieciFragment fragment = new DzieciFragment();
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
        View view = inflater.inflate(R.layout.fragment_dzieci, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dzieciAdapter = new DzieciAdapter(getLayoutInflater(savedInstanceState));
        dzieciList.setLayoutManager(new LinearLayoutManager(getActivity()));
        dzieciList.setAdapter(dzieciAdapter);
    }
}
