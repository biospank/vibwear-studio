package it.vibwear.app.fragments;

import java.util.List;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.adapters.Contact;
import it.vibwear.app.adapters.ContactListAdapter;
import it.vibwear.app.utils.SosPreference;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class SosDetailFragment extends Fragment {
	// Declare
	static final int PICK_CONTACT=1;
	protected Context context;
	protected SosPreference contactPreference;
	protected ContactListAdapter adapter;
	
	protected EditText etSosMsg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		context = container.getContext();

		View sosDetail = inflater.inflate(R.layout.fragment_sos_detail, container, false);
		ImageButton btSosContacts = (ImageButton)sosDetail.findViewById(R.id.bt_sos_contacts);
		
		btSosContacts.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				  Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				  startActivityForResult(intent, PICK_CONTACT);
			}
		});
		
		ListView lvSosContacts = (ListView)sosDetail.findViewById(R.id.lv_sos_contacts);
		
		contactPreference = new SosPreference(context);
		
		List<Contact> contacts = contactPreference.getContacts();
		
		adapter = new ContactListAdapter(container.getContext(), contacts);

	    lvSosContacts.setAdapter(adapter);
		
		etSosMsg = (EditText)sosDetail.findViewById(R.id.et_sos_msg);
		
		etSosMsg.setText(contactPreference.getSosMessage());

		return sosDetail;
	}

	@Override
	public void onDestroyView() {
		contactPreference.setSosMessage(etSosMsg.getText().toString());
		super.onDestroyView();

	}

	// code
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case (PICK_CONTACT):
			if (resultCode == Activity.RESULT_OK) {

				Uri contactData = data.getData();
				Cursor c = context.getContentResolver().query(contactData, null, null, null, null);
				if (c.moveToFirst()) {

					String id = c
							.getString(c
									.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

					if(!adapter.hasContact(new Contact(id))) {
						Contact contact = new Contact();
						contact.setId(id);
	
						String hasPhone = c
								.getString(c
										.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
	
						if (hasPhone.equalsIgnoreCase("1")) {
							Cursor phones = context.getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
													+ " = " + id, null, null);
							phones.moveToFirst();

							String name = c
									.getString(c
											.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
							contact.setName(name);
							
							String cNumber = phones.getString(phones.getColumnIndex("data1"));
//							Log.d("Contacts", "number is:" + cNumber);
							
							contact.setPhone(cNumber);

							adapter.add(contact);
						} else {
							Toast.makeText(context, R.string.contact_with_number_msg, Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
			break;
		}
	}
	
}
