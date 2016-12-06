package nielsen.guiltmotivator;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 Choose between Blue Screen of Death, Monster Green and... red. Red is probably the least bad.
 */
public class SettingsFragment extends Fragment {
    View view;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        // get radiogroup/buttons about tone
        RadioGroup radio_group_tone = (RadioGroup) view.findViewById(R.id.radio_group_tone);
        RadioButton polite = (RadioButton) view.findViewById(R.id.polite);
        RadioButton profane = (RadioButton) view.findViewById(R.id.profane);

        // get radiogroup/buttons about pronouns
        RadioGroup radio_group_pronouns = (RadioGroup) view.findViewById(R.id.radio_group_pronouns);
        RadioButton he = (RadioButton) view.findViewById(R.id.he);
        RadioButton she = (RadioButton) view.findViewById(R.id.she);
        RadioButton they = (RadioButton) view.findViewById(R.id.they);

        // get user name thing
        TextView userName = (TextView) view.findViewById(R.id.userName);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String tone = sharedPref.getString(MainActivity.SAVED_TONE, "polite");
        String pronouns = sharedPref.getString(MainActivity.SAVED_PRONOUNS, "they");
        String username = sharedPref.getString(MainActivity.SAVED_NAME, "none");

        userName.setText(username);

        if (tone.equals("polite")) {
            radio_group_tone.check(polite.getId());
        } else if (tone.equals("profane")) {
            radio_group_tone.check(profane.getId());
        }

        if (pronouns.equals("he")) {
            radio_group_pronouns.check(he.getId());
        } else if (pronouns.equals("she")) {
            radio_group_pronouns.check(she.getId());
        } else if (pronouns.equals("they")) {
            radio_group_pronouns.check(they.getId());
        }

        return view;
    }
    public void onCreate() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String tone = sharedPref.getString(MainActivity.SAVED_TONE, "polite");
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSettingsFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     */
    public interface OnFragmentInteractionListener {

        void onSettingsFragmentInteraction(Uri uri);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}