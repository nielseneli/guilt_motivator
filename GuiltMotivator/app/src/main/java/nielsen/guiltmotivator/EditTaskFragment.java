package nielsen.guiltmotivator;

import android.content.ContentValues;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is the fragment where tasks are edited and added.
 */

public class EditTaskFragment extends Fragment {
    @BindView(R.id.addContact) ImageButton addButton;
    @BindView(R.id.contactlist) ListView contactList;
    @BindView(R.id.taskName) TextView taskName;
    @BindView(R.id.tvDueDate) TextView tvDueDate;
    @BindView(R.id.editTaskSaveButton) Button editTaskSaveButton;
    @BindView(R.id.editDueDate) ImageButton editDueDateButton;
    private Task task;

    private ArrayList<Task> tasks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_edit_task, container, false);
        ButterKnife.bind(this,v);
        // get the id from the bundle from the HomeFragment
        final Bundle b = getArguments();

        final DatabaseHelper mDbHelper = new DatabaseHelper(getContext());
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        tasks = mDbHelper.getAllTasks();

        if (b != null) { //if this came with a bundle
            Long id = b.getLong("id");
            task = getTaskById(tasks, id);
            taskName.setText(task.getText());
            tvDueDate.setText(task.getDueDate().getTime().toString());
        } else {
            task = new Task();
        }

        // set up contacts thingy
        final ArrayList<Contact> contacts = mDbHelper.getContacts(task);
        final ContactAdapter adapter = new ContactAdapter(this.getContext(), contacts);
        contactList.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_create_contact, null);
                //set up the spinner
                final Spinner methodSpinner = (Spinner) dialogView.findViewById(R.id.contactMethodSpinner);
                ArrayAdapter<CharSequence> methodAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.contact_methods_array, android.R.layout.simple_spinner_item);
                methodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                methodSpinner.setAdapter(methodAdapter);

                //set up the alert dialog actions
                alertDialogBuilder.setView(dialogView)
                        .setTitle("Add A Contact!")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText nameEditText = (EditText) dialogView.findViewById(R.id.editTextContactName);
                                EditText addressEditText = (EditText) dialogView.findViewById(R.id.editTextContactAddress);

                                String name = nameEditText.getText().toString();
                                String address = addressEditText.getText().toString();
                                String method = methodSpinner.getItemAtPosition(methodSpinner.getSelectedItemPosition())
                                        .toString();

                                Contact contact = new Contact(name, method, address);
                                adapter.add(contact);

                                if (b != null) {
                                    ContentValues contactValues = new ContentValues();
                                    contactValues.put(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_NAME, contact.getName());
                                    contactValues.put(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_ADDRESS, contact.getAddress());
                                    contactValues.put(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_METHOD, contact.getMethod());
                                    contactValues.put(ContactDbContract.FeedEntry.COLUMN_NAME_TASK_ID, (int) task.getId());

                                    long newContactRowId = db.insert(ContactDbContract.FeedEntry.TABLE_NAME, null, contactValues);
                                    contact.setTaskId(task.getId());
                                    contact.setLocalId(newContactRowId);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                            }
                        });
                alertDialogBuilder.show();
                editTaskSaveButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
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
                                editTaskSaveButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                startService();
                builder.show();

            }
        });

        editTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            //save a thing.
            @Override
            public void onClick(View view) {
                Boolean showAlertDialog = false;

                String missingInfo;
                if (task.getDueDate() == null){
                    missingInfo = "a due date!";
                    showAlertDialog = true;
                } else if (task.getText() == null && taskName.getText().toString().equals("")) {
                    missingInfo = "a task name!";
                    showAlertDialog = true;
                } else if (contacts.size() == 0) {
                    missingInfo = "any contacts!";
                    showAlertDialog = true;
                } else {
                    missingInfo = "";
                }

                if (showAlertDialog) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("You haven't selected " + missingInfo)
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.show();
                } else {
                    task.setText(taskName.getText().toString());
                    if (b != null) { //if you're editing an existing task
                        mDbHelper.editTask(task);


                    } else { //you're making a new task. Make the task first...
                        ContentValues taskValues = new ContentValues();
                        taskValues.put(TaskDbContract.FeedEntry.COLUMN_NAME_TASK, taskName.getText().toString());
                        taskValues.put(TaskDbContract.FeedEntry.COLUMN_NAME_ISCHECKED, "false");
                        taskValues.put(TaskDbContract.FeedEntry.COLUMN_NAME_DUEDATE, task.getDueDate().getTime().toString());

                        // Insert the new row, returning the primary key value of the new row
                        long newRowId = db.insert(TaskDbContract.FeedEntry.TABLE_NAME, null, taskValues);
                        task.setId(newRowId);

                        //Then make the contacts...
                        for (int i=0; i<contacts.size(); i++) {
                            Contact contact = contacts.get(i);
                            ContentValues contactValues = new ContentValues();
                            contactValues.put(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_NAME, contact.getName());
                            contactValues.put(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_ADDRESS, contact.getAddress());
                            contactValues.put(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_METHOD, contact.getMethod());
                            contactValues.put(ContactDbContract.FeedEntry.COLUMN_NAME_TASK_ID, (int) task.getId());

                            long newContactRowId = db.insert(ContactDbContract.FeedEntry.TABLE_NAME, null, contactValues);
                            contact.setTaskId(task.getId());
                            contact.setLocalId(newContactRowId);
                        }

                    }
                }
            }
        });

        return v;
    }

    public void startService() {
//        DatabaseHelper mDbHelper = new DatabaseHelper(getContext());
//
//        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
//
//        //grab arraylist of tasks from the database
//        ArrayList<Task> list = mDbHelper.getAllTasks();
        NotificationEventReceiver.setupAlarm(getContext());
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
