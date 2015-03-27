package it.vibwear.app.utils;

import it.lampwireless.vibwear.app.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SmsPreference implements VibrationPreference, SwitchPreference {
    public static final String SMS_PREFS_NAME = "SMS_DETAILS";
    public static final String SMS_VIB_TIME = "sms_vib_time";
	public static final String SMS_KEY_PREF = "pref_key_inbound_sms";

	protected Context context;
    
    public SmsPreference(Context context) {
        super();
        this.context = context;
    }
 
	public int getVibrationTime() {
		SharedPreferences settings = context.getSharedPreferences(SMS_PREFS_NAME,
                Context.MODE_PRIVATE);
        return settings.getInt(SMS_VIB_TIME, DEFAULT_VIB_TIME);
        
	}

	public void setVibrationTime(int progress) {
		Editor editor;
		SharedPreferences settings = context.getSharedPreferences(SMS_PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        
        editor.putInt(SMS_VIB_TIME, progress);
        
        editor.commit();
		
	}

	@Override
	public boolean switchState() {
		Editor editor;
		boolean active;
		SharedPreferences settings = context.getSharedPreferences(SMS_PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

		if(settings.getBoolean(SMS_KEY_PREF, false)) {
			editor.putBoolean(SMS_KEY_PREF, false);
			active = false;

		} else {
			editor.putBoolean(SMS_KEY_PREF, true);
			active = true;

		}

		editor.apply();
		
		return active;
		
	}

	@Override
	public boolean getState() {
		SharedPreferences settings = context.getSharedPreferences(SMS_PREFS_NAME,
                Context.MODE_PRIVATE);
		return settings.getBoolean(SMS_KEY_PREF, false);
		
	}
	
	@Override
	public String getLabel() {
		SharedPreferences settings = context.getSharedPreferences(SMS_PREFS_NAME,
                Context.MODE_PRIVATE);
		if(settings.getBoolean(SMS_KEY_PREF, false))
			return context.getString(R.string.activeSmsServiceDesc);
		else
			return context.getString(R.string.smsServiceDesc);
	}
	
	@Override
	public int getImage() {
		SharedPreferences settings = context.getSharedPreferences(SMS_PREFS_NAME,
                Context.MODE_PRIVATE);
		if(settings.getBoolean(SMS_KEY_PREF, false))
			return R.drawable.ic_sms_active;
		else
			return R.drawable.ic_sms;
	}
	
}
