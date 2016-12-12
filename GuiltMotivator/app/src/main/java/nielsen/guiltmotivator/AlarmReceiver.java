package nielsen.guiltmotivator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Testing AlarmReceiver.
 * From http://stackoverflow.com/questions/10221996/how-do-i-repeat-a-method-every-10-minutes-after-a-button-press-and-end-it-on-ano/10222390#10222390
 */

public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "received notification");
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ArrayList<Task> tasks = mDbHelper.getAllTasks();

        for (int j = 0; j < tasks.size(); j++) {
            Task task = tasks.get(j);
            Calendar currentTime = Calendar.getInstance();
            Calendar dueDate = task.getDueDate();
            if (!task.isChecked() && currentTime.compareTo(dueDate) >= 0) {
                sendEmail(context, task);
                task.toggleChecked();
                mDbHelper.editTask(task);
            }
        }

    }

    private void sendEmail(Context context, Task task){
//        Activity activity = (Activity) context;
        //get helper and get db in write mode
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ArrayList<Contact> contacts = mDbHelper.getContacts(task);

        final String politeMsg = "polite";
        final String profaneMsg = "profane";
//        String msg = tone == "polite"? politeMsg : profaneMsg;
        String msg = "You didn't do the thing.";

        for (int i = 0; i < contacts.size();i++){
            //Creating SendMail object
            SendMail sm = new SendMail(context, contacts.get(i).getAddress(), "From Guilt Motivator AlarmReceiver", msg);
            //Executing sendmail to send email
            sm.execute();
        }
    }

}
