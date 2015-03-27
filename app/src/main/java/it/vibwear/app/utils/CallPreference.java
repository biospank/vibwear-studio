package it.vibwear.app.utils;

import it.lampwireless.vibwear.app.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CallPreference implements VibrationPreference, SwitchPreference {

    public static final String CALL_PREFS_NAME = "CALL_DETAILS";
    public static final String CALL_VIB_TIME = "call_vib_time";
	public static final String CALL_KEY_PREF = "pref_key_incoming_call";
	
	protected Context context;
    
	public CallPreference(Context context) {
		super();
		this.context = context;
	}

	public int getVibrationTime() {
		SharedPreferences settings = context.getSharedPreferences(CALL_PREFS_NAME,
                Context.MODE_PRIVATE);
        return settings.getInt(CALL_VIB_TIME, DEFAULT_VIB_TIME);
        
	}

	public void setVibrationTime(int progress) {
		Editor editor;
		SharedPreferences settings = context.getSharedPreferences(CALL_PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        
        editor.putInt(CALL_VIB_TIME, progress);
        
        editor.commit();
		
	}

	@Override
	public boolean switchState() {
		Editor editor;
		boolean active;
		SharedPreferences settings = context.getSharedPreferences(CALL_PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

		if(settings.getBoolean(CALL_KEY_PREF, false)) {
			editor.putBoolean(CALL_KEY_PREF, false);
			active = false;

		} else {
			editor.putBoolean(CALL_KEY_PREF, true);
			active = true;

		}

		editor.apply();
		
		return active;
		
	}

	@Override
	public boolean getState() {
		SharedPreferences settings = context.getSharedPreferences(CALL_PREFS_NAME,
                Context.MODE_PRIVATE);
		return settings.getBoolean(CALL_KEY_PREF, false);
		
	}
	
	@Override
	public String getLabel() {
		SharedPreferences settings = context.getSharedPreferences(CALL_PREFS_NAME,
                Context.MODE_PRIVATE);
		if(settings.getBoolean(CALL_KEY_PREF, false))
			return context.getString(R.string.activeCallServiceDesc);
		else
			return context.getString(R.string.callServiceDesc);
	}
	
	@Override
	public int getImage() {
		SharedPreferences settings = context.getSharedPreferences(CALL_PREFS_NAME,
                Context.MODE_PRIVATE);
		if(settings.getBoolean(CALL_KEY_PREF, false))
			return R.drawable.ic_call_active;
		else
			return R.drawable.ic_call;
	}
	
}
