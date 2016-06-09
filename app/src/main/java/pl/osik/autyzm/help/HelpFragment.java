package pl.osik.autyzm.help;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.osik.autyzm.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpFragment extends Fragment {

    //TODO FINALLY Poprawić adresy i telefony kontaktowe

    private static final String PHOTO_PATH = "file:///android_asset/PJWSTK.jpg";

    @Bind(R.id.help_photo)
    ImageView helpPhoto;

    public HelpFragment() {
        // Required empty public constructor
    }

    public static HelpFragment newInstance(String param1, String param2) {
        HelpFragment fragment = new HelpFragment();
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
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.help_title);
        ButterKnife.bind(this, view);
        Glide.with(this)
                .load(PHOTO_PATH)
                .centerCrop()
                .into(helpPhoto);
        return view;
    }

}
