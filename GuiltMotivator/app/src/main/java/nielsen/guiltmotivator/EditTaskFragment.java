package nielsen.guiltmotivator;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EditTaskFragment extends Fragment {
    @BindView(R.id.addContact) ImageButton addButton;
    @BindView(R.id.contactlist) ListView contactList;
    @BindView(R.id.taskName) TextView taskName;
    @BindView(R.id.tvDueDate) TextView tvDueDate;
    @BindView(R.id.editButton) ImageButton editButton;
    @BindView(R.id.editDueDate) ImageButton editDueDateButton;

    private ArrayList<Task> tasks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_edit_task, container, false);
        ButterKnife.bind(this,v);
        // get the id from the bundle from the HomeFragment
        Bundle b = getArguments();
        Long id = b.getLong("id");
        // get the task information from the database
        final DictionaryOpenHelper mDbHelper = new DictionaryOpenHelper(getContext());
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        tasks = mDbHelper.getAllTasks();
        final Task task = getTaskById(tasks, id);
        taskName.setText(task.getText());
        tvDueDate.setText(task.getDueDate().getTime().toString());
        // set up contacts thingy
        ArrayList<Contact> contacts = new ArrayList<>();
        final ContactAdapter adapter = new ContactAdapter(this.getContext(),contacts);
        contactList.setAdapter(adapter);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contact contact = new Contact("Test","SMS");
                adapter.add(contact);
            }
        });
        // edit that task's name
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Edit the task name");
                final EditText editText = new EditText(getActivity());
                editText.setText(task.getText());
                builder.setView(editText);
                builder.setPositiveButton("enter", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String inputText = editText.getText().toString();
                                task.setText(inputText);
                                taskName.setText(inputText);
                                mDbHelper.editTask(task);
                                mDbHelper.close();
                            }
                        })
                        .show();
            }
        });

        editDueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.edit_todo_dialog, null);
                builder.setView(dialogView)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.editTimePicker1);
                                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.editDatePicker1);
                                Calendar inputDate = Calendar.getInstance();
                                inputDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());

                                task.setDueDate(inputDate);
                                tvDueDate.setText(inputDate.getTime().toString());
                                mDbHelper.editTask(task);
                                mDbHelper.close();
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

        return v;
    }

    public Task getTaskById(ArrayList<Task> tasks, Long id) {
        Task task = new Task();
        for (Task temp : tasks) {
            if (temp.getId() == id) {
                task = temp;
                break;
            }
        }
        return task;
    }
}
