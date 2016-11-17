package nielsen.guiltmotivator;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/** This is my adapter. It works for an array of tasks and includes an onclick for each testview that opens up an alertdialog.
 * */

public class TasksAdapter extends ArrayAdapter<Task> {

    private ArrayList<Task> tasks;

    private DictionaryOpenHelper mDbHelper = new DictionaryOpenHelper(getContext());
    // Gets the data repository in write mode
    final SQLiteDatabase db = mDbHelper.getWritableDatabase();

    public TasksAdapter(ArrayList<Task> tasks, Context context) {
        super(context, 0, tasks);
        this.tasks = tasks;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Task task = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        ButterKnife.bind(this, convertView);
        // Lookup view for data population

        // Populate the data into the template view using the data object
        holder.tvText.setText(task.getText());
        holder.checkBox.setChecked(task.isChecked());

        final DictionaryOpenHelper mDbHelper = new DictionaryOpenHelper(getContext());

        // set an onclick listener to the checkbox
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                task.toggleChecked();
                mDbHelper.editTask(task);
                notifyDataSetChanged();
            }
        });

        //set an onclick listener to the textview.
        holder.tvText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment newFragment = new Edit_Task_Fragment();
                Bundle args = new Bundle();
                args.putLong("ID", task.getId());
                newFragment.setArguments(args);
                if (newFragment != null)
                    switchFragment(newFragment);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tvText)
        TextView tvText;
        @BindView(R.id.checkBox)
        CheckBox checkBox;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void switchFragment(Fragment newFragment) {
        if (this.getContext() == null)
            return;
        if (this.getContext() instanceof MainActivity) {
            MainActivity feeds = (MainActivity) this.getContext();
            feeds.replaceFragment(newFragment);
        }
    }

}