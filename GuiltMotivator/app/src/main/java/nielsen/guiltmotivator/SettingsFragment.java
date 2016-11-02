package nielsen.guiltmotivator;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 Choose between Blue Screen of Death, Monster Green and... red. Red is probably the least bad.
 */
public class SettingsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private int currentBackground;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //get the buttons
        Button red = (Button) view.findViewById(R.id.red);
        Button blue = (Button) view.findViewById(R.id.blue);
        Button green = (Button) view.findViewById(R.id.green);

        //set up the onclick shiz with buttonSetup method
        buttonSetup(red, 0xffff0000);
        buttonSetup(blue, 0xff0000ff);
        buttonSetup(green, 0xff00ff00);

        return view;
    }
    public void onCreate() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        int defaultValue = getResources().getColor(R.color.white);
        int background = sharedPref.getInt(MainActivity.SAVED_COLOR, defaultValue);


        getView().setBackgroundColor(background);
    }

    public void buttonSetup(Button name, final int color) {
        //change the background color on click
        name.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //getView().setBackgroundColor(color);
                getActivity().getWindow().getDecorView().setBackgroundColor(color);
                currentBackground = color;

                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(MainActivity.SAVED_COLOR, currentBackground);
                editor.apply();
            }
        });
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
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(MainActivity.SAVED_COLOR, currentBackground);
        editor.commit();
    }
}