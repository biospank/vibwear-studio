package it.vibwear.app;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.fragments.CallDetailFragment;
import it.vibwear.app.fragments.LocationFragment;
import it.vibwear.app.fragments.ServicesFragment;
import it.vibwear.app.fragments.AlarmFragment.AlarmListner;
import it.vibwear.app.fragments.LocationFragment.OnLocationChangeListener;
import it.vibwear.app.fragments.SettingsDetailFragment;
import it.vibwear.app.scanner.ScannerFragment;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import com.mbientlab.metawear.api.GATT;
import com.mbientlab.metawear.api.characteristic.DeviceInformation;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class VibWearActivity extends ModuleActivity implements OnLocationChangeListener, SettingsDetailFragment.OnSettingsChangeListener, AlarmListner {
	private static final String VERSION = "1.3.0";
	private static final long SIGNAL_START_DELAY = 10000;
	private static final long SIGNAL_SCHEDULE_TIME = 5000;
	private static final long BATTERY_START_DELAY = 60000;
	private static final long BATTERY_SCHEDULE_TIME = 60000;
	private LocationFragment locationFrag;
	private ServicesFragment servicesFrag;
	private Timer signalTimer;
	private Timer batteryTimer;
	private PowerManager powerMgr;

	IntentFilter intentFilter;

	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			intent.putExtra("standBy", isStandBy());
			
			if(mwController != null && mwController.isConnected() && servicesFrag.consumeIntent(intent))
				vibrate(ModuleActivity.NOTIFY_VIB_MODE, intent);
			
			servicesFrag.update(intent);

		}
			
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);

		initializeView(savedInstanceState);

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
 
        case R.id.menu_test:
            if (mwController != null && mwController.isConnected())
				vibrate(ModuleActivity.NOTIFY_VIB_MODE, null);
            
            break;
 
        case R.id.menu_about:
        	AlertDialog.Builder builder=new AlertDialog.Builder(this);
        	builder.setIcon(R.drawable.ic_launcher);
        	builder.setTitle("About");
        	if(firmwareVersion != null) {
            	builder.setMessage("VibWear v. " + VERSION + "\nFirmware v. " + firmwareVersion);
        	} else {
        		builder.setMessage("VibWear v. " + VERSION);
        	}
        	builder.setCancelable(true);
        	builder.create();
        	builder.show();
        }
 
		return true;
    }
        
	@Override
	protected void onResume() {
		// gestione audio
		// startService(audioCaptureIntent);
//		showNotificationIcon(false);
		super.onResume();
		startScheduledTimers();
		registerReceiver(intentReceiver, intentFilter);
	}
	
	@Override
	public void onDestroy() {
//		showNotificationIcon(false);
		super.onDestroy();
		cancelScheduledTimers();
		unregisterReceiver(intentReceiver);
	}

	@Override
	protected void onPause() {
		//unregisterReceiver(intentReceiver);
		// gestione audio
		// stopService(audioCaptureIntent);
//		if(!isFinishing())
//			showNotificationIcon(true);
		super.onPause();
		
	}

	@Override
	public void onBackPressed() {
		if (mwController != null && mwController.isConnected()) {
			if(getFragmentManager().getBackStackEntryCount() == 0) {
				moveTaskToBack(true);
				return;
			}
		}
		super.onBackPressed();
	}
	
    @Override
    public void invalidateOptionsMenu() {
    	// TODO Auto-generated method stub
    	super.invalidateOptionsMenu();

		if (mwController != null && mwController.isConnected()) {
			locationFrag.updateConnectionImageResource(true);
		} else {
			locationFrag.updateConnectionImageResource(false);
		}
    }
    
	@Override
	public void onLocationChange() {
		if(mwController != null && mwController.isConnected()) {
			unbindDevice();
            locationFrag.updateConnectionImageResource(false);
		} else {
            final FragmentManager fm = getFragmentManager();
            final ScannerFragment dialog = ScannerFragment.getInstance(VibWearActivity.this, 
                    new UUID[] {GATT.GATTService.METAWEAR.uuid()}, true);
            dialog.show(fm, "scan_fragment");
		}

	}

//	@Override
//	public void onSignalRequest() {
//		if(mwController.isConnected()) {
//			boolean res = mBluetoothGatt.readRemoteRssi();
//			Log.d("remote rssi", "result: " + res);
//		}
//	}
	
//	@Override
//	public void onBatteryRequest() {
//		if (mwController.isConnected()) {
//			mwController.readBatteryLevel();
//		}
//	}

	@Override
	protected void updateSignalLevel(int rssiPercent) {
		locationFrag.updateSignalImageResource(rssiPercent);
	}

	@Override
	protected void updateBatteryLevel(String batteryLevel) {
		locationFrag.updateBatteryLevelImageResource(batteryLevel);
	}
	
	@Override
	public void onLowSignal() {
		//vibrate(ModuleActivity.LOW_SIGNAL_VIB_MODE, null);
	}

//    public void onHeard() {
//        if(mwController != null && mwController.isConnected()) {
//            vibrate(ModuleActivity.NOTIFY_VIB_MODE, new Intent().se);
//        }
//    }

    @Override
    public void onLowBattery() {
        SharedPreferences settings = getSharedPreferences(SettingsDetailFragment.LOW_BATTERY_PREFS_NAME,
                Context.MODE_PRIVATE);

        if(settings.getBoolean(SettingsDetailFragment.NOTIFY_ME_KEY, false)) {
            vibrate(ModuleActivity.LOW_BATTERY_VIB_MODE, null);
//            requestUserAttention();
        }
    }

    @Override
    public void onBoardNameChange(String boardName) {
        if(mwController != null && mwController.isConnected()) {
            settingsController.setDeviceName(boardName);
            deviceName = boardName;
        }
    }

    public boolean isBoardConnected() {return (mwController != null && mwController.isConnected());}

	protected void initializeView(Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			locationFrag = (LocationFragment) getFragmentManager().getFragment(savedInstanceState, "locationFragment");
			servicesFrag = (ServicesFragment) getFragmentManager().getFragment(savedInstanceState, "servicesFragment");
		} else {
	//		locationFrag = (LocationFragment)getFragmentManager().findFragmentById(R.id.fragmentLocation);
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			locationFrag = new LocationFragment();
			servicesFrag = new ServicesFragment();
			ft.add(R.id.locationLayout, locationFrag, "locationFrag");
			ft.add(R.id.servicesLayout, servicesFrag, "servicesFrag");
			ft.commit();
		}
		
		powerMgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
		
		// gestione audio
//		chkAudio = (CheckBox) findViewById(R.id.chkAudio);
//		audioCaptureIntent =  new Intent(this, AudioCaptureService.class);
//        sCn = new ChatNotificationService(this);

		intentFilter = new IntentFilter();
		intentFilter.addAction(ServicesFragment.CALL_VIB_ACTION);
		intentFilter.addAction(ServicesFragment.SMS_VIB_ACTION);
		intentFilter.addAction(ServicesFragment.ALARM_VIB_ACTION);
		intentFilter.addAction(ServicesFragment.CHAT_VIB_ACTION);
		intentFilter.addAction(ServicesFragment.AUDIO_VIB_ACTION);

//		registerReceiver(intentReceiver, intentFilter);
		
	}
	
	private void startScheduledTimers() {
		signalTimer = new Timer();
		batteryTimer = new Timer();
		
		signalTimer.scheduleAtFixedRate(new TimerTask() {
		    @Override
		    public void run() {
		    	requestSignalLevel();
	         }
	    }, SIGNAL_START_DELAY, SIGNAL_SCHEDULE_TIME);
		
		batteryTimer.scheduleAtFixedRate(new TimerTask() {
		    @Override
		    public void run() {
		    	requestBatteryLevel();
	         }
	    }, BATTERY_START_DELAY, BATTERY_SCHEDULE_TIME);
		
	}
	
	private void cancelScheduledTimers() {
		batteryTimer.cancel();
		signalTimer.cancel();
	}

	public void onTimeAlarmChanged() {
		servicesFrag.refresh();
	}
	
	private boolean isStandBy() {
		return !powerMgr.isScreenOn();
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getFragmentManager().putFragment(outState, "locationFragment", locationFrag);
        getFragmentManager().putFragment(outState, "servicesFragment", servicesFrag);
        
    }

    private void requestUserAttention() {
        Intent intent = new Intent(getBaseContext(), VibWearActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent);
    }

}
