package pl.osik.autyzm.lekcje;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.dzieci.DzieciAdapter;
import pl.osik.autyzm.dzieci.DzieciDetailsActivity;
import pl.osik.autyzm.helpers.OperationsEnum;

public class LekcjeFragment extends Fragment {

    LekcjeAdapter lekcjeAdapter;

    @Bind(R.id.lekcje_list)
    RecyclerView lekcjeList;
    @Bind(R.id.lekcje_fab)
    FloatingActionButton fab;

    public LekcjeFragment() {
        // Required empty public constructor
    }

    public static LekcjeFragment newInstance(String param1, String param2) {
        LekcjeFragment fragment = new LekcjeFragment();
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
        View view = inflater.inflate(R.layout.fragment_lekcje, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.lekcje_title);
        lekcjeAdapter = new LekcjeAdapter(getLayoutInflater(savedInstanceState), this);
        lekcjeList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        lekcjeList.setAdapter(lekcjeAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LekcjeTytulActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(DzieciAdapter.BUNDLE_SWITCH_OPERACJA, OperationsEnum.DODAWANIE);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

}
