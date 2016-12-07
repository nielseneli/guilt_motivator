package nielsen.guiltmotivator;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by DHZ_Bill on 11/29/16.
 */
public class NotificationIntentService extends IntentService {
    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
//        intent = new Intent(this, NotificationIntentService.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(),
//                eventId, intent, 0);
//        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        am.cancel(pendingIntent);
//// Cancel the `PendingIntent` after you've canceled the alarm
//        pendingIntent.cancel();

    }

    private void processStartNotification() {
        // Do something. For example, fetch fresh data from backend to create a rich notification?

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Scheduled Notification")
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText("This notification has been triggered by Notification Service")
                .setSmallIcon(R.drawable.notification_icon);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                new Intent(this, NotificationActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());

        sendEmail();
    }

    private void sendEmail(){
        //get helper and get db in write mode
        DatabaseHelper mDbHelper = new DatabaseHelper(getApplicationContext());
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ArrayList<Task> list = mDbHelper.getAllTasks();
        Task task = list.get(0);
        ArrayList<Contact> contacts = mDbHelper.getContacts(task);
        final SharedPreferences sharedPref = getContext().getPreferences(Context.MODE_PRIVATE);
        final String tone = sharedPref.getString(MainActivity.SAVED_TONE, "polite");
        final String name = sharedPref.getString(MainActivity.SAVED_NAME, "none");
        final String politeMsg = "polite";
        final String profaneMsg = "profane";
        String msg = tone == "polite"? politeMsg : profaneMsg;

        for (int i = 0; i < contacts.size();i++){
            //Creating SendMail object
            SendMail sm = new SendMail(this, contacts.get(i).getAddress(), "From Guilt Motivator", msg);
            //Executing sendmail to send email
            sm.execute();
        }

    }
}
