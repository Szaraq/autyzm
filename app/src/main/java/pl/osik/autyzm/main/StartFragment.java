package pl.osik.autyzm.main;

import android.content.Intent;
import android.os.Bundle;
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


public class StartFragment extends Fragment {

    private StartLastAdapter startLastAdapter;
    private StartFavouritesAdapter startFavouritesAdapter;

    @Bind(R.id.start_lastUsedList)
    RecyclerView startLastUsedList;
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
        startLastAdapter = new StartLastAdapter(getLayoutInflater(savedInstanceState), this);
        startLastUsedList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        startLastUsedList.setAdapter(startLastAdapter);

        startFavouritesAdapter = new StartFavouritesAdapter(getLayoutInflater(savedInstanceState), this);
        startFavouritesList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        startFavouritesList.setAdapter(startFavouritesAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
