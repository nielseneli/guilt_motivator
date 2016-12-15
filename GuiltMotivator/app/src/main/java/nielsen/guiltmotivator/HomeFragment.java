package nielsen.guiltmotivator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The main fragment containing the list of tasks and a button to add tasks.
 */
public class HomeFragment extends Fragment {
    //preparing to butter...
    @BindView(R.id.tasklist)
    ListView listView;
    @BindView(R.id.add_button)
    FloatingActionButton addButton;

    private String name;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getting the view
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        //spreading the butter
        ButterKnife.bind(this, view);
        //get helper and get db in write mode
        DatabaseHelper mDbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//        mDbHelper.onCreate(db);
        //grab arraylist of tasks from the database
        ArrayList<Task> list = mDbHelper.getAllTasks();
        final TasksAdapter tasksAdapter = new TasksAdapter(list, getContext());
        listView.setAdapter(tasksAdapter);
        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        name = sharedPref.getString(MainActivity.SAVED_NAME, "none");
        if (name.equals("none")) {
            //user doesn't have a name saved. Open an alertDialog.
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle("Welcome to Guilt Motivator!")
                    .setMessage("Please enter your name.");
            LayoutInflater dialogInflater = getActivity().getLayoutInflater();
            final View dialogView = dialogInflater.inflate(R.layout.dialog_set_name_pronouns, null);
            // make a drop down menu, woo
            final Spinner pronounsSpinner = (Spinner) dialogView.findViewById(R.id.pronounsSpinner);
            final ArrayAdapter<CharSequence> pronounsAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.pronouns_array, android.R.layout.simple_spinner_item);
            pronounsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            pronounsSpinner.setAdapter(pronounsAdapter);

            alertDialogBuilder.setView(dialogView)
                    .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // get name
                            EditText nameEditText = (EditText) dialogView.findViewById(R.id.editTextName);
                            String textInput = nameEditText.getText().toString();
                            // get pronouns
                            String pronouns = pronounsSpinner.getItemAtPosition(
                                    pronounsSpinner.getSelectedItemPosition()).toString();
                            String pronoun = "";
                            if (pronouns.equals("He/him/his")) {
                                pronoun = "he";
                            } else if (pronouns.equals("She/her/hers")) {
                                pronoun = "she";
                            } else if (pronouns.equals("They/them/theirs")) {
                                pronoun = "they";
                            }
                            // add the name and pronouns to sharedPrefs
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(MainActivity.SAVED_NAME, textInput);
                            editor.apply();
                            SharedPreferences.Editor editor2 = sharedPref.edit();
                            editor2.putString(MainActivity.SAVED_PRONOUNS, pronoun);
                            editor2.apply();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }

        //setting an onclick for the button that adds items.
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment newFragment = new EditTaskFragment();
                MainActivity main = (MainActivity) getContext();

                if (getContext() == null) {
                    return;
                }
                if (getContext() instanceof MainActivity) {
                    main.replaceFragment(newFragment);
                }
            }
        });

        return view;
    }

}
