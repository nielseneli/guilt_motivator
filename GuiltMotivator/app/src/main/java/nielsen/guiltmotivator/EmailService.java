package nielsen.guiltmotivator;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zlan on 12/12/16.
 */
public class EmailService extends Service {

    private static WeakReference<Activity> mActivityRef;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            checkAllTasks();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 60000); //execute in every 10 ms
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +100, restartServicePI);

    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        //start a separate thread and start listening to your network object
    }

    public static void updateActivity(Activity activity) {
        mActivityRef = new WeakReference<>(activity);
    }

    public void checkAllTasks(){
        DatabaseHelper mDbHelper = new DatabaseHelper(getApplicationContext());
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //grab arraylist of tasks from the database
        ArrayList<Task> tasks = mDbHelper.getAllTasks();
        Calendar cur = Calendar.getInstance();
        for (int i = 0; i < tasks.size(); i++){
            if (tasks.get(i).getDueDate().compareTo(cur) <= 0 && !tasks.get(i).isChecked()){
                sendEmail(getApplicationContext(),tasks.get(i));
            }
        }
    }

    private void sendEmail(Context context, Task task){
//        Activity activity = (Activity) context;
        //get helper and get db in write mode
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ArrayList<Contact> contacts = mDbHelper.getContacts(task);
        Activity activity = mActivityRef.get();
        final SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        final String tone = sharedPref.getString(MainActivity.SAVED_TONE, "polite");
        final String name = sharedPref.getString(MainActivity.SAVED_NAME, "none");
        final String pronouns = sharedPref.getString(MainActivity.SAVED_PRONOUNS, "they");
        String right_pronoun_objective;
        String right_pronoun_subjective;
        String right_pronoun_possessive;
        String right_pronoun_subj_article;
        if (pronouns.equals("he")) {
            right_pronoun_objective = getResources().getString(R.string.him);
            right_pronoun_subjective = getResources().getString(R.string.he);
            right_pronoun_possessive = getResources().getString(R.string.his);
            right_pronoun_subj_article = right_pronoun_subjective + " is";
        } else if (pronouns.equals("she")) {
            right_pronoun_objective = getResources().getString(R.string.her);
            right_pronoun_subjective = getResources().getString(R.string.she);
            right_pronoun_possessive = getResources().getString(R.string.her);
            right_pronoun_subj_article = right_pronoun_subjective + " is";
        } else { //if pronouns are they
            right_pronoun_objective = getResources().getString(R.string.them);
            right_pronoun_subjective = getResources().getString(R.string.they);
            right_pronoun_possessive = getResources().getString(R.string.theirs);
            right_pronoun_subj_article = right_pronoun_subjective + " are";
        }
        for (int i = 0; i < contacts.size();i++){
            //Creating SendMail object
            String msg = getMessage(right_pronoun_objective, right_pronoun_subjective,
                    right_pronoun_possessive, right_pronoun_subj_article, tone, name, contacts.get(i).getName(), task.getText());
            SendMail sm = new SendMail(context, contacts.get(i).getAddress(), "From Guilt Motivator", msg);
            //Executing sendmail to send email
            sm.execute();
        }
        task.toggleChecked();
        mDbHelper.editTask(task);
    }

    public String getMessage(String pronoun_objective, String pronoun_subjective,
                             String pronoun_possessive, String pronoun_subj_article, String tone,
                             String username, String contact, String task) {
        // TODO: This disappears into a different file, delete it from here when it's moved
        String text = "";
        if (tone.equals("polite")) {
            text = String.format(getResources().getString(R.string.polite_message),
                    contact, username, pronoun_subjective, task, pronoun_objective);
        } else if (tone.equals("rude")) {
            text = String.format(getResources().getString(R.string.rude_message),
                    contact, username, pronoun_subjective, task, pronoun_possessive, pronoun_subj_article);
        } else if (tone.equals("profane")) {
            text = String.format(getResources().getString(R.string.profane_message),
                    contact, username, pronoun_subjective, task, pronoun_subj_article);
        }
        return text;
    }

}
