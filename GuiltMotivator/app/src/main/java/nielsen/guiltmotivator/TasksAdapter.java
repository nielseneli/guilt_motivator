package nielsen.guiltmotivator;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/** This is my adapter. It works for an array of tasks and includes an onclick for each testview that opens up an alertdialog.
 * */

public class TasksAdapter extends ArrayAdapter<Task> {
    @BindView(R.id.tvText) TextView tvText;
    private ArrayList<Task> tasks;

    DictionaryOpenHelper mDbHelper = new DictionaryOpenHelper(getContext());
    // Gets the data repository in write mode
    final SQLiteDatabase db = mDbHelper.getWritableDatabase();

    public TasksAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
        this.tasks = tasks;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Task task = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }

        ButterKnife.bind(this, convertView);
        // Lookup view for data population

        // Populate the data into the template view using the data object
        tvText.setText(task.getText());

        final DictionaryOpenHelper mDbHelper = new DictionaryOpenHelper(getContext());

        //set an onclick listener to the textview.
        tvText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment newFragment = new Edit_Task_Fragment();
                if (newFragment != null)
                    switchFragment(newFragment);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    private void switchFragment(Fragment newFragment) {
        if (this.getContext() == null)
            return;
        if (this.getContext() instanceof MainActivity) {
            MainActivity feeds = (MainActivity) this.getContext();
            feeds.replaceFragment(newFragment);
        }
    }

}