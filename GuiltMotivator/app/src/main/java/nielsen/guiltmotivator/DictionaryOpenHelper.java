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

/** Sets up the database columns. **/

public class DictionaryOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "DictionaryOpenHelper";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DictionaryOpenContract.FeedEntry.TABLE_NAME + " (" +
                    DictionaryOpenContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    DictionaryOpenContract.FeedEntry.COLUMN_NAME_TASK + TEXT_TYPE + COMMA_SEP +
                    DictionaryOpenContract.FeedEntry.COLUMN_NAME_ISCHECKED + TEXT_TYPE + COMMA_SEP +
                    DictionaryOpenContract.FeedEntry.COLUMN_NAME_DUEDATE + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DictionaryOpenContract.FeedEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public DictionaryOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
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
                "SELECT * FROM " + DictionaryOpenContract.FeedEntry.TABLE_NAME;

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Task newTask = new Task();
                    newTask.setText(cursor.getString(cursor.getColumnIndex(DictionaryOpenContract.FeedEntry.COLUMN_NAME_TASK)));
                    newTask.setChecked(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DictionaryOpenContract.FeedEntry.COLUMN_NAME_ISCHECKED))));
                    newTask.setId(cursor.getLong(cursor.getColumnIndex(DictionaryOpenContract.FeedEntry._ID)));

                    String dueDateString = cursor.getString(cursor.getColumnIndex(DictionaryOpenContract.FeedEntry.COLUMN_NAME_DUEDATE));
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
                    Date dueDateDate = sdf.parse(dueDateString);
                    Calendar dueDateCalendar = Calendar.getInstance();
                    dueDateCalendar.setTime(dueDateDate);
                    newTask.setDueDate(dueDateCalendar);

                    tasks.add(newTask);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return tasks;
    }

    public boolean deleteTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(DictionaryOpenContract.FeedEntry.TABLE_NAME , DictionaryOpenContract.FeedEntry._ID
                + "=" + task.getId(), null) > 0;

    }

    public boolean editTask(Task task) {
        //http://stackoverflow.com/questions/9798473/sqlite-in-android-how-to-update-a-specific-row
        SQLiteDatabase db = getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(DictionaryOpenContract.FeedEntry._ID, task.getId());
        args.put(DictionaryOpenContract.FeedEntry.COLUMN_NAME_TASK, task.getText());
        args.put(DictionaryOpenContract.FeedEntry.COLUMN_NAME_ISCHECKED, Boolean.toString(task.isChecked()));
        args.put(DictionaryOpenContract.FeedEntry.COLUMN_NAME_DUEDATE, task.getDueDate().getTime().toString());
        return db.update(DictionaryOpenContract.FeedEntry.TABLE_NAME, args, DictionaryOpenContract.FeedEntry._ID + "=" + task.getId(), null) > 0;
    }

}