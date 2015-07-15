package it.vibwear.app.fragments;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.VibWearActivity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class LocationFragment extends Fragment {
	private View layout;
	private ImageView icSignal;
	private ImageView icLocation;
	private ImageView icBattery;
    private ImageView icSettings;

	private OnLocationChangeListener mCallback;
	private String batteryLevel = null;
	private int signalLevel = -1;
	private boolean connected = false;

    public static final int DEVICE_STATE_CONNECTED = 1;
    public static final int DEVICE_STATE_DISCONNECTED = 2;
    public static final int DEVICE_STATE_RECONNECTING = 3;

	public interface OnLocationChangeListener {
		public void onLocationChange();
		public void onLowSignal();
        public void onLowBattery();
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
        icSettings = (ImageView) layout.findViewById(R.id.icSettings);

		icLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onLocationChange();
			}
			
		});

        icSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                VibWearActivity activity = (VibWearActivity) getActivity();
                if(activity.isDeviceConnected()) {
                    FragmentManager fm = activity.getFragmentManager();
                    Fragment settingsFrag = fm.findFragmentByTag("settingsDetail");
                    if (settingsFrag == null) {
                        if(fm.getBackStackEntryCount() > 0)
                            fm.popBackStackImmediate();
                        FragmentTransaction ft = fm.beginTransaction();
                        settingsFrag = SettingsDetailFragment.newInstance(activity.getDeviceName());
                        ft.replace(R.id.servicesLayout, settingsFrag, "settingsDetail");
                        ft.addToBackStack(null);
                        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                        ft.commit();
                    }
                }
            }

        });

        if(savedInstanceState != null) {
			boolean connected = savedInstanceState.getBoolean("connected");

			if(connected) {
                updateConnectionImageResource(DEVICE_STATE_CONNECTED);
				updateBatteryLevelImageResource(savedInstanceState.getString("batteryLevel"));
				updateSignalImageResource(savedInstanceState.getInt("signalLevel"));
                icSettings.setImageResource(R.drawable.ic_settings_active);
			} else {
                updateConnectionImageResource(DEVICE_STATE_DISCONNECTED);
                icSettings.setImageResource(R.drawable.ic_settings);
            }
		}
//		else {
//			Bundle props = getArguments();
//
//			if(props != null) {
//
//				boolean connected = props.getBoolean("connected", false);
//
//				if (connected) {
//					updateConnectionImageResource(connected);
//					updateBatteryLevelImageResource(props.getString("batteryLevel"));
//					updateSignalImageResource(props.getInt("signalLevel"));
//				}
//			}
//		}


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

	public void updateConnectionImageResource(int status) {
        switch (status) {
            case DEVICE_STATE_CONNECTED:
                this.connected = true;
                icLocation.clearAnimation();
                icLocation.setImageResource(R.drawable.ic_connection_on);
                icSettings.setImageResource(R.drawable.ic_settings_active);
                if(isAdded())
                    layout.findViewById(R.id.header).setBackgroundColor(getResources().getColor(R.color.headColorOn));

                break;
            case DEVICE_STATE_DISCONNECTED:
                this.connected = false;
                icLocation.clearAnimation();
                icLocation.setImageResource(R.drawable.ic_connection);
                icSettings.setImageResource(R.drawable.ic_settings);
                icSignal.setImageResource(R.drawable.ic_signal);
                icBattery.setImageResource(R.drawable.ic_battery);
                if(isAdded())
                    layout.findViewById(R.id.header).setBackgroundColor(getResources().getColor(R.color.headColorOff));

                break;
            case DEVICE_STATE_RECONNECTING:
                this.connected = false;
                Animation rotation = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.reconnect_refresh);
                rotation.setRepeatCount(Animation.INFINITE);
                icLocation.setImageResource(R.drawable.ic_reconnect);
                icLocation.startAnimation(rotation);
                icSettings.setImageResource(R.drawable.ic_settings);
                icSignal.setImageResource(R.drawable.ic_signal);
                icBattery.setImageResource(R.drawable.ic_battery);
                if(isAdded())
                    layout.findViewById(R.id.header).setBackgroundColor(getResources().getColor(R.color.headColorOff));

        }
		
	}

	public void updateSignalImageResource(int rssiPercent) {
		this.signalLevel = rssiPercent;
		
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
		this.batteryLevel = batteryLevel;
		int bl = Integer.parseInt(batteryLevel);
		
		if(bl > 80) {
            icBattery.setImageResource(R.drawable.ic_battery_full);
        } else if(bl > 60 && bl <= 80) {
            icBattery.setImageResource(R.drawable.ic_battery_high);
        } else if(bl > 30 && bl <= 60) {
            icBattery.setImageResource(R.drawable.ic_battery_mid);
        } else if(bl > 0 && bl <= 30) {
            icBattery.setImageResource(R.drawable.ic_battery_low);
            mCallback.onLowBattery();
        }

    }
	
	public String getCurrentBatteryLevel() {
		return batteryLevel;
	}

	public int getCurrentSignalLevel() {
		return signalLevel;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
        if(connected) {
            outState.putBoolean("connected", true);
            outState.putString("batteryLevel", getCurrentBatteryLevel());
            outState.putInt("signalLevel", getCurrentSignalLevel());
        } else {
            outState.putBoolean("connected", false);
        }

	}

}
