package nielsen.guiltmotivator;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/** This is the adapter for tasks. It works for an array of tasks and includes an onclick for each textView that opens up the EditTaskFragment,
 * and a delete button.
 * */

public class TasksAdapter extends ArrayAdapter<Task> {

    private ArrayList<Task> tasks;

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
        // Populate the data into the template view using the data object
        holder.tvText.setText(task.getText());
        holder.checkBox.setChecked(task.isChecked());
        SimpleDateFormat sdf = new SimpleDateFormat("EE,  MMM d HH:mm", Locale.US);
        holder.dueDate.setText(sdf.format(task.getDueDate().getTime()));
        final DatabaseHelper mDbHelper = new DatabaseHelper(getContext());
        // set onClickListeners. Edit or delete task, then go into sql.
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.toggleChecked();
                mDbHelper.editTask(task);
                notifyDataSetChanged();
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tasks.remove(position);
                                mDbHelper.deleteTask(task);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
            }
        });

        //set an onclick listener to the textview, which opens up the EditTaskFragment.
        holder.tvText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment newFragment = new EditTaskFragment();
                Bundle args = new Bundle();
                args.putLong("id", task.getId());
                newFragment.setArguments(args);
                switchFragment(newFragment);
            }
        });
        holder.dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new EditTaskFragment();
                Bundle args = new Bundle();
                args.putLong("id", task.getId());
                newFragment.setArguments(args);
                switchFragment(newFragment);
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tvText) TextView tvText;
        @BindView(R.id.checkBox) CheckBox checkBox;
        @BindView(R.id.deleteButton) ImageButton deleteButton;
        @BindView(R.id.dueDateHome) TextView dueDate;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void switchFragment(Fragment newFragment) {
        if (this.getContext() instanceof MainActivity) {
            MainActivity feeds = (MainActivity) this.getContext();
            feeds.replaceFragment(newFragment);
        }
    }

}