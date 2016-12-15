package nielsen.guiltmotivator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/** Database Helper. Gets, saves and deletes entries in both the contacts table and the tasks one. **/

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    //create strings that represent SQL queries
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";
    private static final String COMMA_SEP = ",";
    private static final String TASKS_TABLE_CREATE_ENTRIES =
            "CREATE TABLE " + TaskDbContract.FeedEntry.TABLE_NAME + " (" +
                    TaskDbContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskDbContract.FeedEntry.COLUMN_NAME_TASK + TEXT_TYPE + COMMA_SEP +
                    TaskDbContract.FeedEntry.COLUMN_NAME_ISCHECKED + TEXT_TYPE + COMMA_SEP +
                    TaskDbContract.FeedEntry.COLUMN_NAME_DUEDATE + TEXT_TYPE + COMMA_SEP +
                    TaskDbContract.FeedEntry.COLUMN_NAME_ISSENT + TEXT_TYPE + " )";
    private static final String CONTACTS_TABLE_CREATE_ENTRIES =
            "CREATE TABLE " + ContactDbContract.FeedEntry.TABLE_NAME + " (" +
                    ContactDbContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    ContactDbContract.FeedEntry.COLUMN_NAME_TASK_ID + INT_TYPE + COMMA_SEP +
                    ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_NAME + TEXT_TYPE + COMMA_SEP +
                    ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_METHOD + TEXT_TYPE + " )";
    private static final String TASKS_TABLE_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskDbContract.FeedEntry.TABLE_NAME;
    private static final String CONTACTS_TABLE_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ContactDbContract.FeedEntry.TABLE_NAME;
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TASKS_TABLE_DELETE_ENTRIES);
        db.execSQL(CONTACTS_TABLE_DELETE_ENTRIES);
        db.execSQL(TASKS_TABLE_CREATE_ENTRIES);
        db.execSQL(CONTACTS_TABLE_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        //https://github.com/codepath/android_guides/wiki/Local-Databases-with-SQLiteOpenHelper
        // SELECT * FROM POSTS
        String POSTS_SELECT_QUERY =
                "SELECT * FROM " + TaskDbContract.FeedEntry.TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        // get information and turn it into the things we want
        try {
            if (cursor.moveToFirst()) {
                do {
                    Task newTask = new Task();
                    // set text, whether it's checked, and id from SQL to task
                    newTask.setText(cursor.getString(cursor.getColumnIndex(TaskDbContract.FeedEntry.COLUMN_NAME_TASK)));
                    newTask.setChecked(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(TaskDbContract.FeedEntry.COLUMN_NAME_ISCHECKED))));
                    newTask.setSent(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(TaskDbContract.FeedEntry.COLUMN_NAME_ISSENT))));
                    newTask.setId(cursor.getLong(cursor.getColumnIndex(TaskDbContract.FeedEntry._ID)));
                    // set due date from SQL to task
                    String dueDateString = cursor.getString(cursor.getColumnIndex(TaskDbContract.FeedEntry.COLUMN_NAME_DUEDATE));
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.US);
                    Date dueDateDate = sdf.parse(dueDateString);
                    Calendar dueDateCalendar = Calendar.getInstance();
                    dueDateCalendar.setTime(dueDateDate);
                    newTask.setDueDate(dueDateCalendar);
                    // add task to arraylist tasks
                    tasks.add(newTask);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from tasks table");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return tasks;
    }

    public boolean deleteTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();
        deleteContactsFromTask(task);
        return db.delete(TaskDbContract.FeedEntry.TABLE_NAME,
                TaskDbContract.FeedEntry._ID + "=" + task.getId(), null) > 0;
    }

    public boolean editTask(Task task) {
        // http://stackoverflow.com/questions/9798473/sqlite-in-android-how-to-update-a-specific-row
        SQLiteDatabase db = getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(TaskDbContract.FeedEntry._ID, task.getId());
        args.put(TaskDbContract.FeedEntry.COLUMN_NAME_TASK, task.getText());
        args.put(TaskDbContract.FeedEntry.COLUMN_NAME_ISCHECKED,
                Boolean.toString(task.isChecked()));
        args.put(TaskDbContract.FeedEntry.COLUMN_NAME_ISSENT, 
                Boolean.toString(task.getSent()));
        args.put(TaskDbContract.FeedEntry.COLUMN_NAME_DUEDATE,
                task.getDueDate().getTime().toString());
        return db.update(TaskDbContract.FeedEntry.TABLE_NAME, args,
                TaskDbContract.FeedEntry._ID + "=" + task.getId(), null) > 0;
    }

    public ArrayList<Contact> getContacts(Task task) {
        //who ya gonna call? probably the people you assigned as contacts for the task.
        ArrayList<Contact> contacts = new ArrayList<>();
        int taskId = (int) task.getId();
        String GET_CONTACTS_QUERY = "SELECT * FROM " + ContactDbContract.FeedEntry.TABLE_NAME
        + " WHERE " + ContactDbContract.FeedEntry.COLUMN_NAME_TASK_ID + "=" + taskId + ";";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(GET_CONTACTS_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String contactName = cursor.getString(cursor.getColumnIndex(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_NAME));
                    String method = cursor.getString(cursor.getColumnIndex(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_METHOD));
                    String address = cursor.getString(cursor.getColumnIndex(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_ADDRESS));

                    Contact newContact = new Contact(contactName, method, address);
                    newContact.setTaskId(cursor.getLong(cursor.getColumnIndex(ContactDbContract.FeedEntry.COLUMN_NAME_TASK_ID)));
                    newContact.setLocalId(cursor.getLong(cursor.getColumnIndex(ContactDbContract.FeedEntry._ID)));
                    contacts.add(newContact);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from contacts table");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return contacts;
    }

    public boolean editContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(ContactDbContract.FeedEntry._ID, contact.getLocalId());
        args.put(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_NAME, contact.getName());
        args.put(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_METHOD, contact.getMethod());
        args.put(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_ADDRESS, contact.getAddress());
        args.put(ContactDbContract.FeedEntry.COLUMN_NAME_TASK_ID, contact.getTaskId());

        return db.update(ContactDbContract.FeedEntry.TABLE_NAME, args,
                ContactDbContract.FeedEntry._ID + "=" + contact.getLocalId(), null) > 0;
    }

    public boolean deleteContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(ContactDbContract.FeedEntry.TABLE_NAME,
                ContactDbContract.FeedEntry._ID + "=" + contact.getLocalId(), null) > 0;
    }

    public boolean deleteContactsFromTask(Task task) {
        //when you delete a task, you should also delete all contacts associated with it in case
        //that ID gets used again
        SQLiteDatabase db = getWritableDatabase();
        int taskId = (int) task.getId();
        return db.delete(ContactDbContract.FeedEntry.TABLE_NAME, ContactDbContract.FeedEntry.COLUMN_NAME_TASK_ID + "=" + taskId, null) > 0;
    }

}