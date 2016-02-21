package it.vibwear.app.utils;

import it.lampwireless.vibwear.app.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AlarmPreference implements VibrationPreference, SwitchPreference, TimePreference {

    public static final String ALARM_PREFS_NAME = "ALARM_DETAILS";
    public static final String ALARM_VIB_TIME = "alarm_vib_time";
	public static final String ALARM_KEY_PREF = "pref_key_alarm";
	public static final String ALARM_KEY_ACTIVE = "key_alarm_active";
	
	protected Context context;
    
	public AlarmPreference(Context context) {
		super();
		this.context = context;
	}

	public int getVibrationTime() {
		SharedPreferences settings = context.getSharedPreferences(ALARM_PREFS_NAME,
                Context.MODE_PRIVATE);
        return settings.getInt(ALARM_VIB_TIME, DEFAULT_VIB_TIME);
        
	}

	public void setVibrationTime(int progress) {
		Editor editor;
		SharedPreferences settings = context.getSharedPreferences(ALARM_PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        
        editor.putInt(ALARM_VIB_TIME, progress);
        
        editor.commit();
		
	}

	@Override
	public boolean switchState() {
		Editor editor;
		boolean active;
		SharedPreferences settings = context.getSharedPreferences(ALARM_PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

		if(settings.getBoolean(ALARM_KEY_ACTIVE, false)) {
			editor.putBoolean(ALARM_KEY_ACTIVE, false);
			active = false;

		} else {
			editor.putBoolean(ALARM_KEY_ACTIVE, true);
			active = true;

		}

		editor.apply();
		
		return active;
		
	}

	@Override
	public boolean getState() {
		SharedPreferences settings = context.getSharedPreferences(ALARM_PREFS_NAME,
                Context.MODE_PRIVATE);
		return settings.getBoolean(ALARM_KEY_ACTIVE, false);
		
	}
	
	@Override
	public String getLabel() {
		SharedPreferences settings = context.getSharedPreferences(ALARM_PREFS_NAME,
                Context.MODE_PRIVATE);
		long timeAlarm;
		
		if(settings.getBoolean(ALARM_KEY_ACTIVE, false)) {
    		timeAlarm = VibWearUtil.getTimeAlarmFor(getTimeSet());
			String formattedDate = VibWearUtil.getFullFormattedDateFor(timeAlarm, context);
			return formattedDate;
		} else {
			return context.getString(R.string.tapAlarmDesc);
		}
	}
	
	@Override
	public int getImage() {
		SharedPreferences settings = context.getSharedPreferences(ALARM_PREFS_NAME,
                Context.MODE_PRIVATE);
		if(settings.getBoolean(ALARM_KEY_ACTIVE, false))
			return R.drawable.ic_alarm_active;
		else
			return R.drawable.ic_alarm;
	}

	public long getTimeSet() {
		SharedPreferences settings = context.getSharedPreferences(ALARM_PREFS_NAME,
                Context.MODE_PRIVATE);
		return settings.getLong(ALARM_KEY_PREF, 0);
	}
	
	public void setTimeSet(long timeAlarm) {
		Editor editor;
		SharedPreferences settings = context.getSharedPreferences(ALARM_PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        
        editor.putLong(ALARM_KEY_PREF, timeAlarm);
        
        editor.commit();
		
	}

}
