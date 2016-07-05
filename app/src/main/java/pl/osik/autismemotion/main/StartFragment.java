package pl.osik.autismemotion.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import pl.osik.autismemotion.R;
import pl.osik.autismemotion.sql.FirstUse;
import pl.osik.autismemotion.uruchom.UruchomController;


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

        askForTourGuide();
        return view;
    }

    private void askForTourGuide() {
        if(!FirstUse.isFirstUsed(getClass())) return;
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage(getContext().getString(R.string.tourGuide_ask_for_tourGuide1) + " " + getContext().getString(R.string.app_name) + " " + getContext().getString(R.string.tourGuide_ask_for_tourGuide2))
                .setTitle(R.string.tourGuide_ask_for_tourGuide_title)
                .setPositiveButton(R.string.yes, null)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirstUse.setAllUsed();
                    }
                })
                .setIcon(R.drawable.ic_pytanie);
        dialog.show();
        FirstUse.setUsed(getClass());
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
