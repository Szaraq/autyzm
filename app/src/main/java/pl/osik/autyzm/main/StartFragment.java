package pl.osik.autyzm.main;

import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;
import pl.osik.autyzm.uruchom.UruchomController;


public class StartFragment extends Fragment {

    private StartLastAdapter startLastAdapter;

    @Bind(R.id.no_lessons_container)
    PercentRelativeLayout noLessonsContainer;
    @Bind(R.id.last_used_header)
    TextView lastUsedHeader;
    @Bind(R.id.start_lastUsedList)
    RecyclerView startLastUsedList;
    @Bind(R.id.favourites_header)
    TextView favouritesHeader;
    @Bind(R.id.start_favouritesList)
    RecyclerView startFavouritesList;

    public StartFragment() {
        // Required empty public constructor
    }

     public static StartFragment newInstance(String param1, String param2) {
        StartFragment fragment = new StartFragment();
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
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("Start");
        startLastUsedList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        startFavouritesList.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.nav_start));

        startLastAdapter = new StartLastAdapter(getLayoutInflater(savedInstanceState), this);
        startLastUsedList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        startLastUsedList.setAdapter(startLastAdapter);
        if(startLastAdapter.lekcjaList.size() == 0) startLastUsedList.setVisibility(View.GONE);

        StartFavouritesAdapter startFavouritesAdapter = new StartFavouritesAdapter(getLayoutInflater(savedInstanceState), this);
        startFavouritesList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        startFavouritesList.setAdapter(startFavouritesAdapter);
        if(startLastAdapter.lekcjaList.size() == 0 && startFavouritesAdapter.lekcjaList.size() == 0) setNoLessonsStart();
    }

    private void setNoLessonsStart() {
        lastUsedHeader.setVisibility(View.GONE);
        startLastUsedList.setVisibility(View.GONE);
        favouritesHeader.setVisibility(View.GONE);
        startFavouritesList.setVisibility(View.GONE);

        noLessonsContainer.setVisibility(View.VISIBLE);
    }

    @Override
     public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        startLastAdapter.refresh();
        UruchomController.clearAll();
    }
}
