package nielsen.guiltmotivator;

import android.content.Context;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import static nielsen.guiltmotivator.R.string.contact;

/**
 * Created by zlan on 11/7/16.
 *
 */
public class ContactAdapter extends ArrayAdapter<Contact> {
    public static int pos;
    private ArrayList<Contact> contacts;
    private DatabaseHelper mDbHelper = new DatabaseHelper(getContext());
    final SQLiteDatabase db = mDbHelper.getWritableDatabase();


    public ContactAdapter(Context context, ArrayList<Contact> items) {
        super(context, 0, items);
        this.contacts = items;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ContactHolder holder;
        //myDb = new DatabaseHelper(this.getContext());
        // Get the data item for this position
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView != null) {
            holder = (ContactHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_item, parent, false);
            holder = new ContactHolder();
            convertView.setTag(holder);
        }

        holder.contact = getItem(position);

        // I didn't figure out how to implement ButterKnife here.
        // Because all elements here is a property of the holder class

        holder.name = (TextView) convertView.findViewById(R.id.itemName);
        holder.method = (TextView) convertView.findViewById(R.id.itemMethod);
        holder.delete = (ImageButton) convertView.findViewById(R.id.delete);

        holder.name.setText(holder.contact.getName());
        holder.method.setText(holder.contact.getMethod());

        // create onClickListener to edit the contact info
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                final View dialogView = inflater.inflate(R.layout.dialog_create_contact, null);
                //set up the spinner
                final Spinner methodSpinner = (Spinner) dialogView.findViewById(R.id.contactMethodSpinner);
                ArrayAdapter<CharSequence> methodAdapter = ArrayAdapter.createFromResource(getContext(), R.array.contact_methods_array,
                        android.R.layout.simple_spinner_item);
                methodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                methodSpinner.setAdapter(methodAdapter);
                //set up the alert dialog actions
                alertDialogBuilder.setView(dialogView)
                        .setTitle("Edit Contact!")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText nameEditText = (EditText) dialogView.findViewById(R.id.editTextContactName);
                                EditText addressEditText = (EditText) dialogView.findViewById(R.id.editTextContactAddress);

                                nameEditText.setHint(holder.contact.getName());
                                addressEditText.setHint(holder.contact.getAddress());

                                String name = nameEditText.getText().toString();
                                String address = addressEditText.getText().toString();
                                String method = methodSpinner.getItemAtPosition(methodSpinner.getSelectedItemPosition()).toString();

                                if (!name.equals("")) {
                                    holder.name.setText(name);
                                    holder.contact.setName(name);
                                }
                                if (!method.equals("")) {
                                    holder.method.setText(method);
                                    holder.contact.setMethod(method);
                                }
                                if (!address.equals("")) {
                                    holder.contact.setAddress(address);
                                }

                                mDbHelper.editContact(holder.contact);
                                notifyDataSetChanged();

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //nuthin
                    }
                });
                alertDialogBuilder.show();
            }
        });

        
        // Create onClickListener for delete Button
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact deleted = holder.contact;
                remove(deleted); //remove the item from the adapter
                mDbHelper.deleteContact(deleted);
                notifyDataSetChanged();
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
        holder.method.setText(holder.contact.getMethod());
    }

    public static class ContactHolder {
        TextView name;
        TextView method;
        ImageButton delete;
        Contact contact;
    }
}
