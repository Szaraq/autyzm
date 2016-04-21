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
        ButterKnife.bind(this.getActivity());
        dzieciAdapter = new DzieciAdapter(getLayoutInflater(savedInstanceState));
        dzieciList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        dzieciList.setAdapter(dzieciAdapter);
        return inflater.inflate(R.layout.fragment_dzieci, container, false);
    }
}
