package pl.osik.autyzm.uruchom;


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
import pl.osik.autyzm.lekcje.LekcjeHelper;
import pl.osik.autyzm.lekcje.LekcjeTytulActivity;
import tourguide.tourguide.TourGuide;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UruchomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UruchomFragment extends Fragment implements View.OnClickListener {
    TourGuide tourGuide;
    UruchomAdapter uruchomAdapter;
    DrawerLayout drawerLayout;

    @Bind(R.id.lekcje_list)
    RecyclerView lekcjeList;
    @Bind(R.id.lekcje_fab)
    FloatingActionButton fab;

    public UruchomFragment() {
        // Required empty public constructor
    }

    public static UruchomFragment newInstance(String param1, String param2) {
        UruchomFragment fragment = new UruchomFragment();
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
        View view = inflater.inflate(R.layout.fragment_uruchom, container, false);
        ButterKnife.bind(this, view);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.uruchom_title);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uruchomAdapter = new UruchomAdapter(getLayoutInflater(savedInstanceState), this);
        lekcjeList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        lekcjeList.setAdapter(uruchomAdapter);
        fab.setOnClickListener(this);

        addTourGuide();
    }

    private void addTourGuide() {
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),  drawerLayout, (Toolbar) getActivity().findViewById(R.id.toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(UruchomFragment.this.getActivity() == null) return;
                tourGuide = AppHelper.makeTourGuide(UruchomFragment.this.getActivity(), R.string.tourGuide_uruchom_fragment, Gravity.TOP, UruchomFragment.this).playOn(fab);
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public void onResume() {
        super.onResume();
        UruchomController.clearAll();
        uruchomAdapter.refresh();
    }

    /* FAB Onclick */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), LekcjeTytulActivity.class);
        LekcjeHelper.setOperacja(OperationsEnum.DODAWANIE);
        startActivity(intent);
        if(tourGuide != null) tourGuide.cleanUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
