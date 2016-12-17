package nielsen.guiltmotivator;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is the fragment where tasks are edited and added.
 */

public class EditTaskFragment extends Fragment {
    @BindView(R.id.addContact) ImageButton addButton;
    @BindView(R.id.contactlist) ListView contactList;
    @BindView(R.id.taskName) EditText taskName;
    @BindView(R.id.tvDueDate) TextView tvDueDate;
    @BindView(R.id.editTaskSaveButton) Button editTaskSaveButton;
    @BindView(R.id.editDueDate) ImageButton editDueDateButton;
    @BindView(R.id.dateLinLayout) LinearLayout dateLinLayout;
    @BindView(R.id.contactLinLayout) LinearLayout contactLinLayout;
    private Task task;

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
        ArrayList<Task> tasks = mDbHelper.getAllTasks();
        if (b != null) {
            // if this came with a bundle, fill in the fields according to that task
            Long id = b.getLong("id");
            task = getTaskById(tasks, id);
            taskName.setText(task.getText());
            SimpleDateFormat sdf = new SimpleDateFormat("EE,  MMM d HH:mm", Locale.US);
            tvDueDate.setText(sdf.format(task.getDueDate().getTime()));
        } else {
            // just create a new task.
            task = new Task();
        }
        // set up contacts thingy
        final ArrayList<Contact> contacts = mDbHelper.getContacts(task);
        final ContactAdapter adapter = new ContactAdapter(this.getContext(), contacts);
        contactList.setAdapter(adapter);

        //make it exit out of keyboard when you hit enter
        taskName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
               if ( (keyEvent.getAction() == KeyEvent.ACTION_DOWN  ) &&(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) ) {
                    // hide virtual keyboard
                    InputMethodManager imm =
                            (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(taskName.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        //alertDialog to change due dates (there are two because of a funky bug with linearLayouts)
        setDateOnClick(editDueDateButton, b);
        setDateOnClick(dateLinLayout, b);

        //same but with contacts
        setContactOnClick(addButton, adapter, b);
        setContactOnClick(contactLinLayout, adapter, b);

        editTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            //save a thing.
            @Override
            public void onClick(View view) {
                //make sure all the fields are filled out correctly, if not set up AlertDialog
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
                    } else { //you're making a new task.
                        // Make the task first...
                        ContentValues taskValues = new ContentValues();
                        taskValues.put(TaskDbContract.FeedEntry.COLUMN_NAME_TASK, taskName.getText().toString());
                        taskValues.put(TaskDbContract.FeedEntry.COLUMN_NAME_ISCHECKED, "false");
                        taskValues.put(TaskDbContract.FeedEntry.COLUMN_NAME_ISSENT, "false");
                        taskValues.put(TaskDbContract.FeedEntry.COLUMN_NAME_DUEDATE, task.getDueDate().getTime().toString());
                        long newRowId = db.insert(TaskDbContract.FeedEntry.TABLE_NAME, null, taskValues);
                        task.setId(newRowId);
                        //Then make the contacts...
                        for (int i=0; i<contacts.size(); i++) {
                            Contact contact = contacts.get(i);
                            ContentValues contactValues = getContactVals(contact.getName(), contact.getAddress(), contact.getMethod(), (int) task.getId(), contact.getTone());
                            long newContactRowId = db.insert(ContactDbContract.FeedEntry.TABLE_NAME, null, contactValues);
                            contact.setTaskId(task.getId());
                            contact.setLocalId(newContactRowId);
                        }
                    }
                    //then go switch back to the home fragment.
                    Fragment newFragment = new HomeFragment();
                    MainActivity main = (MainActivity) getContext();
                    if (getContext() == null)
                        return;
                    if (getContext() instanceof MainActivity)
                        main.replaceFragment(newFragment);
                }
            }
        });
        return v;
    }

    public void startService() {
        NotificationEventReceiver.setupAlarm(getContext());
    }

    public Task getTaskById(ArrayList<Task> tasks, Long id) {
        // get the task, if it exists already
        Task task = new Task();
        for (Task temp : tasks) {
            if (temp.getId() == id) {
                task = temp;
                break;
            }
        }
        return task;
    }

    public ContentValues getContactVals(String name, String address, String method, int id, String tone) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_NAME, name);
        contentValues.put(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_ADDRESS, address);
        contentValues.put(ContactDbContract.FeedEntry.COLUMN_NAME_CONTACT_METHOD, method);
        contentValues.put(ContactDbContract.FeedEntry.COLUMN_NAME_TONE, tone);
        contentValues.put(ContactDbContract.FeedEntry.COLUMN_NAME_TASK_ID, id);
        return contentValues;
    }

    public void setContactOnClick(View v, final ContactAdapter adapter, final Bundle b) {
        //I don't know why, but when I set a listener on the whole linear layout, it allows me to
        //click on everything but the button itself. I can get around this with a second onclicklistener,
        //but I don't wanna write it twice so here we are.
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseHelper mDbHelper = new DatabaseHelper(getContext());
                final SQLiteDatabase db = mDbHelper.getWritableDatabase();
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
                                //get the views
                                EditText nameEditText = (EditText) dialogView.findViewById(R.id.editTextContactName);
                                EditText addressEditText = (EditText) dialogView.findViewById(R.id.editTextContactAddress);
                                RadioGroup toneGroup = (RadioGroup) dialogView.findViewById(R.id.radioGroupContactTone);
                                //get stuff from them
                                String name = nameEditText.getText().toString();
                                String address = addressEditText.getText().toString();
                                String method = methodSpinner.getItemAtPosition(methodSpinner.getSelectedItemPosition())
                                        .toString();
                                int radioId = toneGroup.getCheckedRadioButtonId();
                                String tone = "";
                                switch(radioId){
                                    case R.id.politeButtonContact:
                                        tone = "polite";
                                        break;
                                    case R.id.rudeButtonContact:
                                        tone = "rude";
                                        break;
                                    case R.id.profaneButtonContact:
                                        tone = "profane";
                                        break;
                                }
                                Contact contact = new Contact(name, method, address);
                                contact.setTone(tone);
                                adapter.add(contact);
                                if (b != null) {
                                    //if youre editing an existing task
                                    ContentValues contactValues = getContactVals(contact.getName(), contact.getAddress(), contact.getMethod(), (int) task.getId(), contact.getTone());
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
            }
        });
    }

    public void setDateOnClick(View v, final Bundle b) {
        //created for the same reason as setContactOnClick, but it's the date one.
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get that custom alert dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_edit_due_date, null);
                final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.editTimePicker1);
                final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.editDatePicker1);
                // if the task exists, make it set to the previous saved time/date
                if (b != null) {
                    datePicker.updateDate(task.getDueDate().get(Calendar.YEAR), task.getDueDate().get(Calendar.MONTH),
                            task.getDueDate().get(Calendar.DAY_OF_MONTH));
                    timePicker.setCurrentHour(task.getDueDate().get(Calendar.HOUR));
                    timePicker.setCurrentMinute(task.getDueDate().get(Calendar.MINUTE));
                }
                builder.setView(dialogView)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // save the date
                                Calendar inputDate = Calendar.getInstance();
                                inputDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                                        timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                                task.setDueDate(inputDate);
                                SimpleDateFormat sdf = new SimpleDateFormat("EE,  MMM d HH:mm", Locale.US);
                                tvDueDate.setText(sdf.format(inputDate.getTime()));
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
    }
}

