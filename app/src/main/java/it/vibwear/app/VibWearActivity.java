package it.vibwear.app;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.fragments.LocationFragment;
import it.vibwear.app.fragments.ReconnectTaskFragment;
import it.vibwear.app.fragments.ServicesFragment;
import it.vibwear.app.fragments.AlarmFragment.AlarmListner;
import it.vibwear.app.fragments.LocationFragment.OnLocationChangeListener;
import it.vibwear.app.fragments.SettingsDetailFragment;
import it.vibwear.app.scanner.ScannerFragment;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import com.mbientlab.metawear.api.GATT;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;

public class VibWearActivity extends ModuleActivity implements OnLocationChangeListener,
        SettingsDetailFragment.OnSettingsChangeListener, AlarmListner, ReconnectTaskFragment.OnReconnectTaskCallbacks {
	private static final String VERSION = "1.5.1";
	private static final long SIGNAL_START_DELAY = 10000;
	private static final long SIGNAL_SCHEDULE_TIME = 15000;
	private static final long BATTERY_START_DELAY = 60000;
	private static final long BATTERY_SCHEDULE_TIME = 60000;
    private static final String TAG_TASK_FRAGMENT = "reconnect_task_fragment";
	private final int VIBWEAR_NOTIFICATION_ID = 9571;
	private LocationFragment locationFrag;
	private ServicesFragment servicesFrag;
	private Timer signalTimer;
	private Timer batteryTimer;
	private PowerManager powerMgr;
	private Notification.Builder mBuilder;
	protected ProgressDialog progress;

	IntentFilter intentFilter;

	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			intent.putExtra("standBy", isStandBy());
			
			if(isDeviceConnected() && servicesFrag.consumeIntent(intent)) {
				vibrate(ModuleActivity.NOTIFY_VIB_MODE, intent);
				updateNotificationTextWith(intent);
			}
			
			servicesFrag.update(intent);

		}
			
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);

		initializeView(savedInstanceState);

        attachReconnectTask();

	}

    protected void attachReconnectTask() {
        FragmentManager fm = getFragmentManager();
        reconnectTaskFragment = (ReconnectTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (reconnectTaskFragment == null) {
            reconnectTaskFragment = new ReconnectTaskFragment();
            fm.beginTransaction().add(reconnectTaskFragment, TAG_TASK_FRAGMENT).commit();
        } else {
            //if(reconnectTaskFragment.isRunning())
                //locationFrag.updateConnectionImageResource(LocationFragment.DEVICE_STATE_RECONNECTING);
        }

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
            if (isDeviceConnected())
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
		super.onResume();
		registerReceiver(intentReceiver, intentFilter);
        startScheduledTimers();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(isFinishing())
			showNotificationIcon(false);
		unregisterReceiver(intentReceiver);
	}

	@Override
	protected void onPause() {
		super.onPause();
		cancelScheduledTimers();
	}

	@Override
	public void onBackPressed() {
		if (isDeviceConnected()) {
			if(getFragmentManager().getBackStackEntryCount() == 0) {
				moveTaskToBack(true);
				return;
			}
		} else {
            if(reconnectTaskFragment != null && reconnectTaskFragment.isRunning()) {
                moveTaskToBack(true);
                return;
            }
        }
		super.onBackPressed();
	}
	
    @Override
    public void invalidateOptionsMenu() {
    	super.invalidateOptionsMenu();

		if (isDeviceConnected()) {
			locationFrag.updateConnectionImageResource(LocationFragment.DEVICE_STATE_CONNECTED);
			if(progress != null) progress.dismiss();
		} else {
            if(reconnectTaskFragment != null && reconnectTaskFragment.isRunning())
                locationFrag.updateConnectionImageResource(LocationFragment.DEVICE_STATE_RECONNECTING);
            else
                locationFrag.updateConnectionImageResource(LocationFragment.DEVICE_STATE_DISCONNECTED);
		}
    }
    
	@Override
	public void onLocationChange() {
		if(isDeviceConnected()) {
			unbindDevice();
            locationFrag.updateConnectionImageResource(LocationFragment.DEVICE_STATE_DISCONNECTED);
		} else {
            if(reconnectTaskFragment != null && reconnectTaskFragment.isRunning()) {
                reconnectTaskFragment.stopAsyncTask();
                locationFrag.updateConnectionImageResource(LocationFragment.DEVICE_STATE_DISCONNECTED);
                if(mwController != null)
                    unbindDevice();
            }

            final FragmentManager fm = getFragmentManager();
            final ScannerFragment dialog = ScannerFragment.getInstance(VibWearActivity.this, 
                    new UUID[] {GATT.GATTService.METAWEAR.uuid()}, true);
            dialog.show(fm, "scan_fragment");
		}

	}

	@Override
	public void onDeviceSelected(BluetoothDevice device, String name) {
		super.onDeviceSelected(device, name);
		progress = new ProgressDialog(this);
		progress.setTitle(R.string.progressTitle);
		progress.setMessage(getResources().getString(R.string.progressMsg));
		progress.show();
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

		if (settings.getBoolean(SettingsDetailFragment.NOTIFY_ME_KEY, false)) {
            vibrate(ModuleActivity.LOW_BATTERY_VIB_MODE, null);
//            requestUserAttention();
        }
    }

    @Override
    public void onBoardNameChange(String boardName) {
        if(isDeviceConnected()) {
            settingsController.setDeviceName(boardName);
            deviceName = boardName;
        }
    }

	protected void initializeView(Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			locationFrag = (LocationFragment) getFragmentManager().getFragment(savedInstanceState, "locationFragment");
			servicesFrag = (ServicesFragment) getFragmentManager().getFragment(savedInstanceState, "servicesFragment");
		} else {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			locationFrag = new LocationFragment();
			servicesFrag = new ServicesFragment();

//			// Intent launched by foreground notification
//			Bundle props = getIntent().getExtras();
//
//			if(props != null)
//				locationFrag.setArguments(props);

			ft.add(R.id.locationLayout, locationFrag, "locationFragment");
			ft.add(R.id.servicesLayout, servicesFrag, "servicesFragment");
			ft.commit();

		}
		
		powerMgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mBuilder = new Notification.Builder(this);

		intentFilter = new IntentFilter();
		intentFilter.addAction(ServicesFragment.CALL_VIB_ACTION);
		intentFilter.addAction(ServicesFragment.SMS_VIB_ACTION);
		intentFilter.addAction(ServicesFragment.ALARM_VIB_ACTION);
		intentFilter.addAction(ServicesFragment.CHAT_VIB_ACTION);
		intentFilter.addAction(ServicesFragment.AUDIO_VIB_ACTION);

	}
	
	private void startScheduledTimers() {
		if (signalTimer == null) {
			signalTimer = new Timer();
			signalTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					requestSignalLevel();
				}
			}, SIGNAL_START_DELAY, SIGNAL_SCHEDULE_TIME);
		}

		if (batteryTimer == null) {
			batteryTimer = new Timer();
			batteryTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    requestBatteryLevel();
                }
            }, BATTERY_START_DELAY, BATTERY_SCHEDULE_TIME);
		}
		
	}
	
	private void cancelScheduledTimers() {

        if (batteryTimer != null) {
            batteryTimer.cancel();
        }

        if (signalTimer != null) {
            signalTimer.cancel();
        }

		batteryTimer = null;
		signalTimer = null;
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

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(isDeviceConnected())
			showNotificationIcon(!hasFocus);
	}

	protected void showNotificationIcon(boolean show) {
		Random generator = new Random();
		if(show) {
			mBuilder.setSmallIcon(R.drawable.ic_launcher)
					.setTicker("Vibwear app listening")
					.setContentTitle("VibWear")
					.setContentText("Tap to show.");
			// Creates an explicit intent for an Activity in your app
			Intent startIntent = new Intent(this, VibWearActivity.class);

//			startIntent.putExtra("connected", true);
//			startIntent.putExtra("batteryLevel", locationFrag.getCurrentBatteryLevel());
//			startIntent.putExtra("signalLevel", locationFrag.getCurrentSignalLevel());

			PendingIntent startPendingIntent =
					PendingIntent.getActivity(this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder.setContentIntent(startPendingIntent);
			mBuilder.setOngoing(true);
			mwService.startForeground(VIBWEAR_NOTIFICATION_ID, mBuilder.build());
		} else {
			mwService.stopForeground(true);
		}
	}

	protected void updateNotificationTextWith(Intent intent) {
		Bundle extraInfo = intent.getExtras();

		String sourcePackageName = extraInfo.getString("sourcePackageName");

		mBuilder.setContentText(sourcePackageName);
        mBuilder.setOngoing(true);
		mwService.startForeground(VIBWEAR_NOTIFICATION_ID, mBuilder.build());

//		NotificationManager notificationManager =
//				(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//		notificationManager.notify(VIBWEAR_NOTIFICATION_ID, mBuilder.build());
	}

    @Override
    public void onReconnectStart() {

    }

    @Override
    public void onReconnectCancelled() {
//        reconnectTaskFragment.stopAsyncTask();
//        unbindDevice();
    }

    @Override
    public void onReconnectStop() {

    }

}
