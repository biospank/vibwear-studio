package it.vibwear.app.utils;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.adapters.Contact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SosPreference implements SwitchPreference {
 
    public static final String SOS_PREFS_NAME = "SOS_DETAILS";
    public static final String SOS_CONTACTS = "sos_contact_list";
	public static final String SOS_KEY_PREF = "pref_key_sos";
    public static final String SOS_MSG = "sos_msg";
    
	protected Context context;

    public SosPreference(Context context) {
        super();
        this.context = context;
    }
 
    // This four methods are used for maintaining favorites.
    public void saveContacts(List<Contact> favorites) {
        SharedPreferences settings;
        Editor editor;
 
        settings = context.getSharedPreferences(SOS_PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
 
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
 
        editor.putString(SOS_CONTACTS, jsonFavorites);
 
        editor.commit();
    }
 
    public void addContact(Contact contact) {
        List<Contact> contacts = getContacts();
        if (contacts == null)
        	contacts = new ArrayList<Contact>();
        contacts.add(contact);
        saveContacts(contacts);
    }
 
    public void removeContact(Contact contact) {
        ArrayList<Contact> contacts = getContacts();
        if (contacts != null) {
        	contacts.remove(contact);
            saveContacts(contacts);
        }
    }
 
    public ArrayList<Contact> getContacts() {
        SharedPreferences settings;
        List<Contact> contacts;
 
        settings = context.getSharedPreferences(SOS_PREFS_NAME,
                Context.MODE_PRIVATE);
 
        if (settings.contains(SOS_CONTACTS)) {
            String jsonFavorites = settings.getString(SOS_CONTACTS, null);
            Gson gson = new Gson();
            Contact[] favoriteItems = gson.fromJson(jsonFavorites,
            		Contact[].class);
 
            contacts = Arrays.asList(favoriteItems);
            contacts = new ArrayList<Contact>(contacts);
        } else
            return new ArrayList<Contact>();
 
        return (ArrayList<Contact>) contacts;
    }

	public String getSosMessage() {
		SharedPreferences settings = context.getSharedPreferences(SOS_PREFS_NAME,
                Context.MODE_PRIVATE);
        return settings.getString(SOS_MSG, null);
        
	}

	public void setSosMessage(String msg) {
		Editor editor;
		SharedPreferences settings = context.getSharedPreferences(SOS_PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        
        editor.putString(SOS_MSG, msg);
        
        editor.commit();
		
	}
	
	@Override
	public boolean switchState() {
		Editor editor;
		boolean active;
		SharedPreferences settings = context.getSharedPreferences(SOS_PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

		if(settings.getBoolean(SOS_KEY_PREF, false)) {
			editor.putBoolean(SOS_KEY_PREF, false);
			active = false;

		} else {
			editor.putBoolean(SOS_KEY_PREF, true);
			active = true;

		}

		editor.apply();
		
		return active;
		
	}

	@Override
	public boolean getState() {
		SharedPreferences settings = context.getSharedPreferences(SOS_PREFS_NAME,
                Context.MODE_PRIVATE);
		return settings.getBoolean(SOS_KEY_PREF, false);
		
	}
	
	@Override
	public String getLabel() {
		SharedPreferences settings = context.getSharedPreferences(SOS_PREFS_NAME,
                Context.MODE_PRIVATE);
		if(settings.getBoolean(SOS_KEY_PREF, false))
			return context.getString(R.string.activeSosServiceDesc);
		else
			return context.getString(R.string.sosServiceDesc);
	}
	
	@Override
	public int getImage() {
		SharedPreferences settings = context.getSharedPreferences(SOS_PREFS_NAME,
                Context.MODE_PRIVATE);
		if(settings.getBoolean(SOS_KEY_PREF, false))
			return R.drawable.ic_sos_active;
		else
			return R.drawable.ic_sos;
	}
	
}