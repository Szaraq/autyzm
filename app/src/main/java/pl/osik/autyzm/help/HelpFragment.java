package pl.osik.autyzm.help;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

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
    //TODO FINALLY Przetestować wysyłanie e-maili na prawdziwym urządzeniu

    private static final String PHOTO_PATH = "file:///android_asset/PJWSTK.jpg";

    protected final HashMap<TextView, String> kontakty = new HashMap<TextView, String>();
        private void createKontakty() {
            kontakty.put(kontaktMichal, getResources().getString(R.string.help_email_michal));
            kontakty.put(kontaktIza, getResources().getString(R.string.help_email_iza));
            kontakty.put(kontaktKlaudia, getResources().getString(R.string.help_email_klaudia));
        }

    @Bind(R.id.help_photo)
    ImageView helpPhoto;
    @Bind(R.id.kontakt_Michal)
    TextView kontaktMichal;
    @Bind(R.id.kontakt_Iza)
    TextView kontaktIza;
    @Bind(R.id.kontakt_Klaudia)
    TextView kontaktKlaudia;

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
        createKontakty();
        appendMailsToTextViews();

        Glide.with(this)
                .load(PHOTO_PATH)
                .centerCrop()
                .into(helpPhoto);

        EmailSenderOnClickListener emailSenderOnClickListener = new EmailSenderOnClickListener(this);
        for (Map.Entry<TextView, String> entry : kontakty.entrySet()){
            entry.getKey().setOnClickListener(emailSenderOnClickListener);
        }

        return view;
    }

    private void appendMailsToTextViews() {
        for (Map.Entry<TextView, String> entry : kontakty.entrySet()){
            entry.getKey().append(entry.getValue());
        }
    }

}

class EmailSenderOnClickListener implements View.OnClickListener {

    HelpFragment fragment;

    public EmailSenderOnClickListener(HelpFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onClick(View v) {
        TextView textView = (TextView) v;
        String email = fragment.kontakty.get(textView);
        final String subject = "Aplikacja " + fragment.getResources().getString(R.string.app_name);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        fragment.startActivity(intent);
    }
}