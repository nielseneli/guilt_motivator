package nielsen.guiltmotivator;

import android.content.Context;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This is the adapter for contacts. It lives in the EditTaskFragment and contains the name and address of each contact.
 *
 */
public class ContactAdapter extends ArrayAdapter<Contact> {

    private DatabaseHelper mDbHelper = new DatabaseHelper(getContext());

    public ContactAdapter(Context context, ArrayList<Contact> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ContactHolder holder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView != null) {
            holder = (ContactHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false);
            holder = new ContactHolder();
            convertView.setTag(holder);
        }
        //get the Contact at the position.
        holder.contact = getItem(position);
        //get all of the layout elements
        holder.name = (TextView) convertView.findViewById(R.id.itemName);
        holder.address = (TextView) convertView.findViewById(R.id.itemAddress);
        holder.delete = (ImageButton) convertView.findViewById(R.id.delete);
        //set the values for the layout attributes from the task's information
        holder.name.setText(holder.contact.getName());
        holder.address.setText(holder.contact.getAddress());
        //create the alert dialogs so when you click on any part of the contact you can edit.
        createEditContactAlertDialog(holder.address, holder);
        createEditContactAlertDialog(holder.name, holder);
        // Create onClickListener for delete Button
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View editTaskView = inflater.inflate(R.layout.fragment_edit_task, null);
                Button editTaskSaveButton = (Button) editTaskView.findViewById(R.id.editTaskSaveButton);
                Contact deleted = holder.contact;
                remove(deleted); //remove the item from the adapter
                mDbHelper.deleteContact(deleted);
                notifyDataSetChanged();
                editTaskSaveButton.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
            }
        });
        convertView.setTag(holder);
        setupItem(holder);
        // Return the completed view to render on screen
        return convertView;
    }

    // set the TextView
    private void setupItem(ContactHolder holder){
        holder.name.setText(holder.contact.getName());
    }

    public static class ContactHolder {
        TextView name;
        TextView address;
        ImageButton delete;
        Contact contact;
    }

    public void createEditContactAlertDialog(View v, final ContactHolder holder) {
        //because I want users to be able to click on any part of the contact and be able to edit, I want something I can call multiple times.
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                final LayoutInflater inflater = LayoutInflater.from(getContext());
                final View dialogView = inflater.inflate(R.layout.dialog_create_contact, null);

                //set up the spinner
                final Spinner methodSpinner = (Spinner) dialogView.findViewById(R.id.contactMethodSpinner);
                ArrayAdapter<CharSequence> methodAdapter = ArrayAdapter.createFromResource(getContext(), R.array.contact_methods_array,
                        android.R.layout.simple_spinner_item);
                methodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                methodSpinner.setAdapter(methodAdapter);

                final EditText nameEditText = (EditText) dialogView.findViewById(R.id.editTextContactName);
                final EditText addressEditText = (EditText) dialogView.findViewById(R.id.editTextContactAddress);

                nameEditText.setText(holder.contact.getName());
                addressEditText.setText(holder.contact.getAddress());

                //set up the alert dialog actions
                alertDialogBuilder.setView(dialogView)
                        .setTitle("Edit Contact!")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String name = nameEditText.getText().toString();
                                String address = addressEditText.getText().toString();
                                String method = methodSpinner.getItemAtPosition(methodSpinner.getSelectedItemPosition()).toString();

                                if (!name.equals("")) {
                                    holder.name.setText(name);
                                    holder.contact.setName(name);
                                }
                                if (!method.equals("")) {
                                    holder.contact.setMethod(method);
                                }
                                if (!address.equals("")) {
                                    holder.contact.setAddress(address);
                                }

                                mDbHelper.editContact(holder.contact);
                                notifyDataSetChanged();

                                View editTaskView = inflater.inflate(R.layout.fragment_edit_task, null);
                                Button editTaskSaveButton = (Button) editTaskView.findViewById(R.id.editTaskSaveButton);
                                editTaskSaveButton.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //nothing
                    }
                });
                alertDialogBuilder.show();
            }
        });
    }
}
