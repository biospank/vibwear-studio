package it.vibwear.app.fragments;

import java.util.Calendar;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.utils.VibWearUtil;
import it.vibwear.app.utils.AlarmPreference;
import it.vibwear.app.utils.TimePreference;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class AlarmFragment extends Fragment {
	protected TimePreference timePreference;
	protected TimePicker alarmPick;
	protected AlarmListner listener;
	
	public interface AlarmListner {
		public void onTimeAlarmChanged();
	}

    public AlarmFragment() {

    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

//		final AlarmListener listener = (AlarmListener) getActivity();
		
		View layout = inflater.inflate(R.layout.fragment_alarm, container, false);
		
		long timeSet = timePreference.getTimeSet();
		
		Calendar alarmSet = VibWearUtil.getCalendarFor(timeSet);
		
		alarmPick = (TimePicker)layout.findViewById(R.id.timePickerAlarm);
		
		alarmPick.setIs24HourView(true);
		alarmPick.setCurrentHour(alarmSet.get(Calendar.HOUR_OF_DAY));
		alarmPick.setCurrentMinute(alarmSet.get(Calendar.MINUTE));
		
		alarmPick.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
				cal.set(Calendar.MINUTE, minute);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);

				long timeAlarm = ((Calendar) VibWearUtil.getCalendarFor(cal.getTimeInMillis())).getTimeInMillis();
				timePreference.setTimeSet(timeAlarm);
				listener.onTimeAlarmChanged();
			}
		});
		
		return layout;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			timePreference = new AlarmPreference(activity);
			listener = (AlarmListner) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AlarmListener");
        }
	}

	public long getTimeAlarm() {
		return timePreference.getTimeSet();
	}


}
