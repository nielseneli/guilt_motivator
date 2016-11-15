package nielsen.guiltmotivator;

import android.content.Context;
import nielsen.guiltmotivator.Contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by zlan on 11/7/16.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {
    public static int pos;

    public ContactAdapter(Context context, ArrayList<Contact> items) {
        super(context, 0, items);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ContactHolder holder = new ContactHolder();
        //myDb = new DatabaseHelper(this.getContext());
        // Get the data item for this position
        holder.contact = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contactitem, parent, false);
        }
        // I didn't figure out how to implement ButterKnife here.
        // Because all elements here is a property of the holder class
        holder.name = (TextView) convertView.findViewById(R.id.itemName);
        holder.method = (TextView)convertView.findViewById(R.id.itemMethod);
        holder.delete = (ImageButton)convertView.findViewById(R.id.delete);
        // create onClickListener for edit Button (goes to Fragment Ingredient


        // Create onClickListener for delete Button
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(holder.contact); //remove the item from the adapter

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
