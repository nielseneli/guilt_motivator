package nielsen.guiltmotivator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


/**
 Choose how rekt you want to get. And also how you want the app to refer to you.
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
        RadioButton rude = (RadioButton) view.findViewById(R.id.rude);
        RadioButton profane = (RadioButton) view.findViewById(R.id.profane);
        // get radiogroup/buttons about pronouns
        RadioGroup radio_group_pronouns = (RadioGroup) view.findViewById(R.id.radio_group_pronouns);
        RadioButton he = (RadioButton) view.findViewById(R.id.he);
        RadioButton she = (RadioButton) view.findViewById(R.id.she);
        RadioButton they = (RadioButton) view.findViewById(R.id.they);
        // get the text for the pronouns buttons
        Resources res = getResources();
        String he_pronouns = String.format(res.getString(R.string.pronouns_string),
                res.getString(R.string.he), res.getString(R.string.him), res.getString(R.string.his));
        String she_pronouns = String.format(res.getString(R.string.pronouns_string),
                res.getString(R.string.she), res.getString(R.string.her), res.getString(R.string.hers));
        String they_pronouns = String.format(res.getString(R.string.pronouns_string),
                res.getString(R.string.they), res.getString(R.string.them), res.getString(R.string.theirs));
        // set the text for the pronouns buttons
        he.setText(he_pronouns);
        she.setText(she_pronouns);
        they.setText(they_pronouns);
        // get the sharedPrefs
        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String tone = sharedPref.getString(MainActivity.SAVED_TONE, "polite");
        String pronouns = sharedPref.getString(MainActivity.SAVED_PRONOUNS, "they");
        final String username = sharedPref.getString(MainActivity.SAVED_NAME, "none");
        // get username thing
        final TextView userName = (TextView) view.findViewById(R.id.userName);
        userName.setText(username);
        // make the username editable and saved in sharedPrefs
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(getActivity());
                editText.setText(username);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(editText)
                        .setPositiveButton("enter", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String inputText = editText.getText().toString();
                                userName.setText(inputText);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(MainActivity.SAVED_NAME, inputText);
                                editor.apply();
                            }
                        })
                        .show();
            }
        });

        // set the radio buttons to be appropriately checked!
        if (tone.equals("polite")) {
            radio_group_tone.check(polite.getId());
        } else if (tone.equals("rude")) {
            radio_group_tone.check(rude.getId());
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