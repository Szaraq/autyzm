package pl.osik.autyzm.dzieci;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.helpers.AppHelper;
import pl.osik.autyzm.helpers.OperationsEnum;
import tourguide.tourguide.TourGuide;


public class DzieciFragment extends Fragment {
    DrawerLayout drawerLayout;
    TourGuide tourGuide;

    @Bind(R.id.dzieci_list)
    RecyclerView dzieciList;
    @Bind(R.id.dzieci_fab)
    FloatingActionButton fab;

    private DzieciAdapter dzieciAdapter;

    public DzieciFragment() {
        // Required empty public constructor
    }

    public static DzieciFragment newInstance() {
        final DzieciFragment fragment = new DzieciFragment();
        final Bundle args = new Bundle();
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
        final View view = inflater.inflate(R.layout.fragment_dzieci, container, false);
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
                if(tourGuide != null) tourGuide.cleanUp();
            }
        });
        addTourGuide();
    }

    private void addTourGuide() {
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),  drawerLayout, (Toolbar) getActivity().findViewById(R.id.toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(DzieciFragment.this.getActivity() == null) return;
                tourGuide = AppHelper.makeTourGuide(DzieciFragment.this.getActivity(), R.string.tourGuide_dzieci_list, Gravity.TOP, DzieciFragment.this);
                if(tourGuide != null) tourGuide.playOn(fab);
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public void onResume() {
        super.onResume();
        dzieciAdapter.refresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
