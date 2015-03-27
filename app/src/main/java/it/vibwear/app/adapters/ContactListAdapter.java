package it.vibwear.app.adapters;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.utils.SosPreference;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactListAdapter extends ArrayAdapter<Contact> {

	
    private Context context;
    List<Contact> contacts;
    SosPreference contactPreference;
 
    public ContactListAdapter(Context context, List<Contact> contacts) {
        super(context, R.layout.contact_list_item, contacts);
        this.context = context;
        this.contacts = contacts;
        contactPreference = new SosPreference(context);
    }
 
    private class ViewHolder {
        TextView contactNameTxt;
        TextView contactPhoneTxt;
        ImageView deleteImg;
    }
 
    @Override
    public int getCount() {
        return contacts.size();
    }
 
    @Override
    public Contact getItem(int position) {
        return contacts.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_list_item, null);
            holder = new ViewHolder();
            holder.contactNameTxt = (TextView) convertView
                    .findViewById(R.id.txt_contact_name);
            holder.contactPhoneTxt = (TextView) convertView
                    .findViewById(R.id.txt_contact_phone);
            holder.deleteImg = (ImageView) convertView
                    .findViewById(R.id.imgbtn_delete);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        final Contact contact = (Contact) getItem(position);
        holder.contactNameTxt.setText(contact.getName());
        holder.contactPhoneTxt.setText(contact.getPhone());
        holder.deleteImg.setImageResource(R.drawable.ic_remove_contact);
        
        holder.deleteImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				remove(contact);
			}
		});

        return convertView;
    }
    
    /*Checks whether a particular contact exists in SharedPreferences*/
    public boolean hasContact(Contact other) {
        boolean check = false;
        List<Contact> contacts = contactPreference.getContacts();
        if (contacts != null) {
            for (Contact contact : contacts) {
                if (contact.equals(other)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }
 
    @Override
    public void add(Contact contact) {
//        super.add(contact);
        contacts.add(contact);
        contactPreference.addContact(contact);
        notifyDataSetChanged();
    }
 
    @Override
    public void remove(Contact contact) {
//        super.remove(contact);
        contacts.remove(contact);
        contactPreference.removeContact(contact);
        notifyDataSetChanged();
    }    
}

