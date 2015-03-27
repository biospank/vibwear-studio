package it.vibwear.app.adapters;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.VibWearUtil;
import it.vibwear.app.fragments.AlarmDetailFragment;
import it.vibwear.app.fragments.ServicesFragment;
import it.vibwear.app.utils.AlarmPreference;
import it.vibwear.app.utils.TimePreference;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class AlarmServiceItem extends ServiceItem {

	private Intent alarmIntent;
	private PendingIntent pendingAlarm;

	public AlarmServiceItem(Activity activity) {
		super(activity);
		this.switchPref = new AlarmPreference(activity);
	}

	public void setIconView(ImageView icon) {
		this.iconWidget = icon;

		alarmIntent = new Intent(ServicesFragment.ALARM_VIB_ACTION);
		pendingAlarm = PendingIntent.getBroadcast(activity, 0, alarmIntent, 0);
		
		this.iconWidget.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(switchPref.switchState()) {
					TimePreference alarmPref = (TimePreference) switchPref;
		            long timeSet = alarmPref.getTimeSet();
		    		long timeAlarm = VibWearUtil.getTimeAlarmFor(timeSet);
		    		activateAlarm(pendingAlarm, timeAlarm);
		    		alarmPref.setTimeSet(timeAlarm);
				} else {
		    		deactivateAlarm(pendingAlarm);

				}
				
				iconWidget.setImageResource(switchPref.getImage());

//				textWidget.setText(VibWearUtil.getAlarmSummarySpanText(switchPref.getLabel()), BufferType.SPANNABLE);

//				Toast.makeText(activity, formattedText, Toast.LENGTH_SHORT).show();
				
			}
		});
		
		showUserIconSettings();
	}

	public void setTextView(TextView text) {
		this.textWidget = text;

		this.textWidget.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fm = activity.getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				AlarmDetailFragment alarmFrag = new AlarmDetailFragment();
				ft.replace(R.id.servicesLayout, alarmFrag, "smsDetail");
				ft.addToBackStack(null);
				ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
				ft.commit();
			}
			
		});
				
		showUserTextSettings();
	}

	public void activateAlarm(PendingIntent alarmIntent, long time) {
		
		AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
		int alarmType = AlarmManager.RTC_WAKEUP;
		// Trigger the device in 20 seconds
		//long timeOrLengthOfWait = 20000;
		
		//alarmManager.setInexactRepeating(alarmType, time, AlarmManager.INTERVAL_DAY, alarmIntent);
		alarmManager.set(alarmType, time, alarmIntent);
		
		String formattedDate = VibWearUtil.getFullFormattedDateFor(time,  activity);
		textWidget.setText(VibWearUtil.getAlarmSummarySpanText(formattedDate), BufferType.SPANNABLE);
	}
	
	public void deactivateAlarm(PendingIntent alarmIntent) {
		AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(alarmIntent);
	}
	
    public void showUserIconSettings() {
		boolean activeAlarm = switchPref.getState();

        if(activeAlarm) {
            long timeSet = ((TimePreference)switchPref).getTimeSet();
    		activateAlarm(pendingAlarm, VibWearUtil.getTimeAlarmFor(timeSet));
    	} else {
    		deactivateAlarm(pendingAlarm);
    	}
        
        super.showUserIconSettings();
        
	}

    public void showUserTextSettings() {
        
        long timeSet = ((TimePreference)switchPref).getTimeSet();
		if(timeSet > 0) {
    		long timeAlarm = VibWearUtil.getTimeAlarmFor(timeSet);
			String formattedDate = VibWearUtil.getFullFormattedDateFor(timeAlarm, activity);
			textWidget.setText(VibWearUtil.getAlarmSummarySpanText(formattedDate), BufferType.SPANNABLE);
        } else {
        	textWidget.setText(VibWearUtil.getAlarmDescSpanText(switchPref.getLabel()), BufferType.SPANNABLE);
        }

	}
	
	@Override
	public void update() {
		TimePreference alarmPref = (TimePreference) switchPref;
        long timeSet = alarmPref.getTimeSet();
		long timeAlarm = VibWearUtil.getTimeAlarmFor(timeSet);
		activateAlarm(pendingAlarm, timeAlarm);
		alarmPref.setTimeSet(timeAlarm);
		
		showUserTextSettings();
	}

	@Override
	public void refresh() {
		TimePreference alarmPref = (TimePreference) switchPref;
        long timeSet = alarmPref.getTimeSet();
		long timeAlarm = VibWearUtil.getTimeAlarmFor(timeSet);
		activateAlarm(pendingAlarm, timeAlarm);

		showUserTextSettings();
	}
	
}
