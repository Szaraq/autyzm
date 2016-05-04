package pl.osik.autyzm.dzieci;

import android.content.Intent;
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
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.OperationsEnum;


public class DzieciFragment extends Fragment {

    @Bind(R.id.dzieci_list)
    RecyclerView dzieciList;
    @Bind(R.id.dzieci_fab)
    FloatingActionButton fab;

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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.dzieci_title);
        dzieciAdapter = new DzieciAdapter(getLayoutInflater(savedInstanceState), this);
        dzieciList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        dzieciList.setAdapter(dzieciAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DzieciDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(DzieciAdapter.BUNDLE_SWITCH_OPERACJA, OperationsEnum.DODAWANIE);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        dzieciAdapter.refresh();
    }
}
