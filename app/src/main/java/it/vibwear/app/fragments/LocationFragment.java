package it.vibwear.app.fragments;

import it.lampwireless.vibwear.app.R;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class LocationFragment extends Fragment {
	private View layout;
	private ImageView icSignal;
	private ImageView icLocation;
	private ImageView icBattery;

	private OnLocationChangeListener mCallback;

	public interface OnLocationChangeListener {
		public void onLocationChange();
		public void onLowSignal();
//		public void onSignalRequest();
//		public void onBatteryRequest();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layout = inflater.inflate(R.layout.fragment_location, container, false);
		
		icSignal = (ImageView) layout.findViewById(R.id.icSignal);
		icBattery = (ImageView) layout.findViewById(R.id.icBattery);
		icLocation = (ImageView) layout.findViewById(R.id.icLocation);
		
		icLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onLocationChange();
			}
			
		});

		
//		icSignal.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				mCallback.onSignalRequest();
//			}
//			
//		});

//		icBattery.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				mCallback.onBatteryRequest();
//			}
//
//		});

		return layout;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
            mCallback = (OnLocationChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLocationChangeListner");
        }
	}

	public void updateConnectionImageResource(boolean connected) {
		if(connected) {
			icLocation.setImageResource(R.drawable.ic_connection_on);
			layout.setBackgroundColor(getResources().getColor(R.color.headColorOn));
		} else {
			icLocation.setImageResource(R.drawable.ic_connection);
			icSignal.setImageResource(R.drawable.ic_signal);
			icBattery.setImageResource(R.drawable.ic_battery);
			layout.setBackgroundColor(getResources().getColor(R.color.headColorOff));
		}
	}
	
	public void updateSignalImageResource(int rssiPercent) {
		if(rssiPercent > 0 && rssiPercent <= 25) {
			icSignal.setImageResource(R.drawable.ic_signal_low);
			mCallback.onLowSignal();
		} else if(rssiPercent > 25 && rssiPercent <= 40) {
			icSignal.setImageResource(R.drawable.ic_signal_mid);
		} else if(rssiPercent > 40) {
			icSignal.setImageResource(R.drawable.ic_signal_high);
		}
	}
	
	public void updateBatteryLevelImageResource(String batteryLevel) {
		int bl = Integer.parseInt(batteryLevel);
		
		if(bl > 80)
			icBattery.setImageResource(R.drawable.ic_battery_full);
		else if(bl > 60 && bl <= 80)
			icBattery.setImageResource(R.drawable.ic_battery_high);
		else if(bl > 30 && bl <= 60)
			icBattery.setImageResource(R.drawable.ic_battery_mid);
		else if(bl > 0 && bl <= 30)
			icBattery.setImageResource(R.drawable.ic_battery_low);
		
	}
	
}
