package nielsen.guiltmotivator;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
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
                Context context = getContext();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("Edit the text, or delete an item.");
                final EditText edittext = new EditText(context);
                edittext.setText(task.getText());
                alertDialogBuilder.setView(edittext);

                //sets the text from the eddittext to the textview.
                alertDialogBuilder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String textInput = edittext.getText().toString();
                        task.setText(textInput);
                        mDbHelper.editTask(task);
                        notifyDataSetChanged();

                    }
                });

                //deletes the current task.
                alertDialogBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mDbHelper.deleteTask(getItem(position));
                        tasks.remove(position);
                        notifyDataSetChanged();
                    }
                });

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tvText) TextView tvText;
        @BindView(R.id.checkBox) CheckBox checkBox;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}