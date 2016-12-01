package nielsen.guiltmotivator;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The main fragment containing the list of tasks and a button to add tasks. Sends data 2 SQL 4 safekeeping.
 */
public class HomeFragment extends Fragment {
    //preparing to butter...
    @BindView(R.id.tasklist)
    ListView listView;
    @BindView(R.id.add_button)
    Button addButton;

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
        DictionaryOpenHelper mDbHelper = new DictionaryOpenHelper(getContext());

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //grab arraylist of tasks from the database
        ArrayList<Task> list = mDbHelper.getAllTasks();
        final TasksAdapter tasksAdapter = new TasksAdapter(list, getContext());
        listView.setAdapter(tasksAdapter);

        String sql = "SELECT " + DictionaryOpenContract.FeedEntry.COLUMN_NAME_TASK + "FROM " + DictionaryOpenContract.FeedEntry.TABLE_NAME;

        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        final String tone = sharedPref.getString(MainActivity.SAVED_TONE, "polite");
        name = sharedPref.getString(MainActivity.SAVED_NAME, "none");

        if (name.equals("none")) {
            //user doesn't have a name saved. Open an alertDialog.
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Welcome to Guilt Motivator!");
            alertDialogBuilder.setMessage("Please enter your name.");
            final EditText edittext = new EditText(getActivity());
            alertDialogBuilder.setView(edittext);
            alertDialogBuilder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String textInput = edittext.getText().toString();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(MainActivity.SAVED_NAME, textInput);
                    editor.apply();
                }
            });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }


        //setting an onclick for the button that adds items.
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.create_todo_dialog, null);
                builder.setView(dialogView)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //get the EditText and Pickers
                                EditText tnEditText = (EditText) dialogView.findViewById(R.id.tnEditText);
                                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker1);
                                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker1);

                                //get the string from the edittext and put that task in the tasksAdapter
                                String taskNameTextInput = tnEditText.getText().toString();
                                Task taskInput = new Task(taskNameTextInput);
                                tasksAdapter.add(taskInput);

                                //get values from the picker and make a calendar instance with them.
                                Calendar inputDate = Calendar.getInstance();
                                inputDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                                taskInput.setDueDate(inputDate);

                                String dueDateString = inputDate.getTime().toString();

                                //make a calendar of the current date
                                Calendar currentDate = Calendar.getInstance();

                                int diff = (int) Math.abs(inputDate.getTimeInMillis() - currentDate.getTimeInMillis());

                                String inputString;
                                if (tone.equals("profane")) {
                                    inputString = "You didn't do \"" + taskNameTextInput + " \" and you're a worthless piece of shit.";
                                } else {
                                    inputString = "Shame on you. You didn't do \"" + taskNameTextInput + ".\" You should consider being less awful.";
                                }

                                scheduleNotification(inputString, (int) taskInput.getId(), diff);

                                //create content values to prepare for SQL.
                                ContentValues values = new ContentValues();
                                values.put(DictionaryOpenContract.FeedEntry.COLUMN_NAME_TASK, taskNameTextInput);
                                values.put(DictionaryOpenContract.FeedEntry.COLUMN_NAME_ISCHECKED, "false");
                                values.put(DictionaryOpenContract.FeedEntry.COLUMN_NAME_DUEDATE, dueDateString);

                                // Insert the new row, returning the primary key value of the new row
                                long newRowId = db.insert(DictionaryOpenContract.FeedEntry.TABLE_NAME, null, values);
                                taskInput.setId(newRowId);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                builder.show();
            }
        });

        return view;
    }

    public void onCreate() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        int defaultValue = getResources().getColor(R.color.white);
        int background = sharedPref.getInt(MainActivity.SAVED_COLOR, defaultValue);


        getView().setBackgroundColor(background);
    }

    public interface OnFragmentInteractionListener {

        public void onMainFragmentInteraction(Uri uri);
    }

    //Helper functions for notifications:
    //https://gist.github.com/BrandonSmith/6679223

    private void scheduleNotification(String content, int id, int delay) {
        Notification.Builder builder = new Notification.Builder(getContext());
        builder.setContentTitle("Hey " + name + ". ");
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = builder.build();

        Intent notificationIntent = new Intent(getActivity(), NotificationPublisher.class);
//        notificationIntent.setData(Uri.parse("timer:" + id));
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
}
