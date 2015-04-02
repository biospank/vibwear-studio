package it.vibwear.app;

import java.util.List;
import it.lampwireless.vibwear.app.R;
import it.vibwear.app.adapters.Contact;
import it.vibwear.app.fragments.ServicesFragment;
import it.vibwear.app.scanner.ScannerFragment.OnDeviceSelectedListener;
import it.vibwear.app.utils.AlarmPreference;
import it.vibwear.app.utils.CallPreference;
import it.vibwear.app.utils.ChatPreference;
import it.vibwear.app.utils.DefaultPreference;
import it.vibwear.app.utils.SmsPreference;
import it.vibwear.app.utils.SosPreference;
import it.vibwear.app.utils.VibrationPreference;

import com.mbientlab.metawear.api.GATT.GATTCharacteristic;
import com.mbientlab.metawear.api.MetaWearBleService;
import com.mbientlab.metawear.api.MetaWearController;
import com.mbientlab.metawear.api.MetaWearController.DeviceCallbacks;
import com.mbientlab.metawear.api.MetaWearController.ModuleCallbacks;
import com.mbientlab.metawear.api.Module;
import com.mbientlab.metawear.api.characteristic.Battery;
import com.mbientlab.metawear.api.characteristic.DeviceInformation;
import com.mbientlab.metawear.api.controller.Haptic;
import com.mbientlab.metawear.api.controller.MechanicalSwitch;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.SmsManager;

public class ModuleActivity extends Activity implements ServiceConnection, OnDeviceSelectedListener {
    public static final String EXTRA_BLE_DEVICE = 
            "it.lampwireless.vibwear.app.ModuleActivity.EXTRA_BLE_DEVICE";
    protected static final String ARG_ITEM_ID = "item_id";

    public static final int LOW_SIGNAL_VIB_MODE = 1;
    public static final int NOTIFY_VIB_MODE = 2;
    
    private static final int DFU = 0;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final Short LOW_SIGNAL_VIBRATION_TIME = 3;
    private static final Short LOW_SIGNAL_VIBRATION_LENGHT = 300;
    private static final Short LOW_SIGNAL_VIBRATION_GAP = 500;

    private final BroadcastReceiver metaWearUpdateReceiver= MetaWearBleService.getMetaWearBroadcastReceiver();
    protected MetaWearBleService mwService;
    protected MetaWearController mwController;
    protected Haptic hapticController;
    protected static BluetoothDevice device;
    protected MechanicalSwitch switchController;
    protected String firmwareVersion;

    
    private DeviceCallbacks dCallback= new MetaWearController.DeviceCallbacks() {
    	
        @Override
        public void connected() {
            mwController.readDeviceInformation();
    		switchController.enableNotification();
            invalidateOptionsMenu();
            
            getRemoteSignals();
        }

        @Override
        public void disconnected() {
            if (device != null && mwController != null) {
//                mwController.setRetainState(true);
                mwController.reconnect(false);
            } else {
            	switchController.disableNotification();
            }
//            invalidateOptionsMenu();
        }
    	
        public void receivedGATTCharacteristic(GATTCharacteristic characteristic, byte[] data) {
        	if(characteristic == Battery.BATTERY_LEVEL) {
        		updateBatteryLevel(String.format("%s", data[0]));
        	} else if(characteristic == DeviceInformation.FIRMWARE_VERSION) {
        		firmwareVersion = new String(data);
        	}
        }

	    @Override
	    public void receivedRemoteRSSI(int rssi) {
    		final int rssiPercent = (int) (100.0f * (127.0f + rssi) / (127.0f + 20.0f));
    		
    		runOnUiThread(new Runnable() {
    		     @Override
    		     public void run() {

		    		updateSignalLevel(rssiPercent);

    		    }
    		});
	    }
	    
	    public void receivedGattError(GattOperation gattOp, int status) {
			if (gattOp.name() == GattOperation.CONNECTION_STATE_CHANGE.toString() &&
					status == 133)
	            if (device != null && mwController != null)
	            	mwController.reconnect(false);
			
	    }
    };

    private ModuleCallbacks mCallback = new MechanicalSwitch.Callbacks() {
        @Override
        public void pressed() {

        }

        @Override
        public void released() {
        	SosPreference contactPreference = new SosPreference(getApplicationContext());
        	List<Contact> contacts = contactPreference.getContacts();
        	SmsManager smsManager = SmsManager.getDefault();
        	
        	String msg = contactPreference.getSosMessage();
        	
        	if(msg.isEmpty())
        		msg = getString(R.string.sos_default_msg);
        	
        	for(Contact contact : contacts) {
                smsManager.sendTextMessage(contact.getPhone(), null, msg, null, null);
        	}
        }
    };
    
    protected void updateSignalLevel(int rssiPercent) {};
    protected void updateBatteryLevel(String batteryLevel) {};

    protected void getRemoteSignals() {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                requestSignalLevel();
            }
        }, 500);

        handler.postDelayed(new Runnable() {
            public void run() {
                requestBatteryLevel();
            }
        }, 500);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (!bluetoothManager.getAdapter().isEnabled()) {
            final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        
        getApplicationContext().bindService(new Intent(this, MetaWearBleService.class), 
                this, Context.BIND_AUTO_CREATE);
        
        if (savedInstanceState != null) {
            device = (BluetoothDevice) savedInstanceState.getParcelable(EXTRA_BLE_DEVICE);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
        case DFU:
            device= data.getParcelableExtra(EXTRA_BLE_DEVICE);
            if (device != null) {
            	mwController.connect();
            }
            break;
        case REQUEST_ENABLE_BT:
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* (non-Javadoc)
     * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mwService = ((MetaWearBleService.LocalBinder) service).getService();
        if (device != null) {
        	initializeAndConnect();
        }
    }
    
    /* (non-Javadoc)
     * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
    	invalidateOptionsMenu();
    }
    
    /* (non-Javadoc)
     * @see no.nordicsemi.android.nrftoolbox.scanner.ScannerFragment.OnDeviceSelectedListener#onDeviceSelected(android.bluetooth.BluetoothDevice, java.lang.String)
     */
    @Override
    public void onDeviceSelected(BluetoothDevice device, String name) {
        if (mwController != null && mwController.isConnected()) {
            mwController.close(true);
            mwController = null;
        }
        ModuleActivity.device = device;
        initializeAndConnect();
    }
    
	private void initializeAndConnect() {
		mwController = mwService.getMetaWearController(device);
    	switchController = (MechanicalSwitch) mwController.getModuleController(Module.MECHANICAL_SWITCH);
        mwController.addDeviceCallback(dCallback);
        mwController.addModuleCallback(mCallback);
        hapticController = (Haptic) mwController.getModuleController(Module.HAPTIC);
        mwController.connect();
	}

    /* (non-Javadoc)
     * @see no.nordicsemi.android.nrftoolbox.scanner.ScannerFragment.OnDeviceSelectedListener#onDialogCanceled()
     */
    @Override
    public void onDialogCanceled() {
        // TODO Auto-generated method stub
        
    }
    
	protected void vibrate(int vibMode, Intent intent) {
		if(vibMode == LOW_SIGNAL_VIB_MODE) {
			Thread background = new Thread(new Runnable() {
                public void run() { 
        			for (int i = 0; i < LOW_SIGNAL_VIBRATION_TIME; i++) {
        				hapticController.startMotor(LOW_SIGNAL_VIBRATION_LENGHT);
        				SystemClock.sleep(LOW_SIGNAL_VIBRATION_GAP);
        			}
                } 
			});
			
			background.start();
			
		} else {

			final VibrationPreference vibPref;
			
			if(intent != null) {
				switch (intent.getAction()) {
				case ServicesFragment.CALL_VIB_ACTION:
					vibPref = new CallPreference(getApplicationContext());
					break;
	
				case ServicesFragment.SMS_VIB_ACTION:
					vibPref = new SmsPreference(getApplicationContext());
					break;
						
				case ServicesFragment.CHAT_VIB_ACTION:
					vibPref = new ChatPreference(getApplicationContext());
					break;
						
				case ServicesFragment.ALARM_VIB_ACTION:
					vibPref = new AlarmPreference(getApplicationContext());
					break;
	
				default:
					vibPref = new DefaultPreference(getApplicationContext());
						
				}
	
			} else {
				vibPref = new DefaultPreference(getApplicationContext());
			}

			Thread background = new Thread(new Runnable() {
                public void run() { 
//                	Log.d("alarm vibration", "" + vibPref.getVibrationTime());
    				hapticController.startMotor((short) (vibPref.getVibrationTime() * 1000));
                } 
			});
			
			background.start();

		}
	}
	
	protected void requestSignalLevel() {
    	if(mwController != null && mwController.isConnected()) {
    		mwController.readRemoteRSSI();
		}
	}

	protected void requestBatteryLevel() {
    	if(mwController != null && mwController.isConnected()) {
    		mwController.readBatteryLevel();
		}
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
//        getApplicationContext().unbindService(this);
//        mwController.removeDeviceCallback(dCallback);
//        mwController.removeModuleCallback(mCallback);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(metaWearUpdateReceiver, MetaWearBleService.getMetaWearIntentFilter());
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(metaWearUpdateReceiver);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (device != null) {
            outState.putParcelable(EXTRA_BLE_DEVICE, device);
        }
    }
    
    protected void unbindDevice() {
    	switchController.disableNotification();
    	device = null;
        mwController.setRetainState(false);
        mwController.close(true);
        mwController= null;
    }
    
    
}
