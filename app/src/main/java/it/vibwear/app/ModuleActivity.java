package it.vibwear.app;

import java.util.List;
import it.lampwireless.vibwear.app.R;
import it.vibwear.app.adapters.Contact;
import it.vibwear.app.fragments.ReconnectTaskFragment;
import it.vibwear.app.fragments.ServicesFragment;
import it.vibwear.app.receivers.BluetoothStateReceiver;
import it.vibwear.app.scanner.ScannerFragment.OnDeviceSelectedListener;
import it.vibwear.app.services.BoundMwService;
import it.vibwear.app.utils.AlarmPreference;
import it.vibwear.app.utils.AudioPreference;
import it.vibwear.app.utils.BleScanner;
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
import com.mbientlab.metawear.api.controller.Settings;

import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;

public class ModuleActivity extends Activity implements OnDeviceSelectedListener, ReconnectTaskFragment.OnReconnectTaskCallbacks {
    public static final String EXTRA_BLE_DEVICE =
            "it.lampwireless.vibwear.app.ModuleActivity.EXTRA_BLE_DEVICE";
    protected static final String ARG_ITEM_ID = "item_id";

    public static final int LOW_BATTERY_VIB_MODE = 0;
    public static final int LOW_SIGNAL_VIB_MODE = 1;
    public static final int NOTIFY_VIB_MODE = 2;

    private static final int DFU = 0;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final Short LOW_SIGNAL_VIBRATION_TIME = 3;
    private static final Short LOW_SIGNAL_VIBRATION_LENGHT = 300;
    private static final Short LOW_SIGNAL_VIBRATION_GAP = 500;
    private static final String TAG_TASK_FRAGMENT = "reconnect_task_fragment";

    //private final BroadcastReceiver metaWearUpdateReceiver= MetaWearBleService.getMetaWearBroadcastReceiver();
    private LocalBroadcastManager broadcastManager = null;
    protected MetaWearBleService mwService;
    protected MetaWearController mwController;
    protected Haptic hapticController;
    protected static BluetoothDevice device;
    protected MechanicalSwitch switchController;
    protected Settings settingsController;
    protected String firmwareVersion;
    protected String deviceName;
    protected boolean isMwServiceBound = false;
    protected ReconnectTaskFragment reconnectTaskFragment;
    protected BluetoothStateReceiver bluetoothStateReceiver;

    private DeviceCallbacks dCallback = new MetaWearController.DeviceCallbacks() {

        @Override
        public void connected() {
            if (isDeviceConnected()) {
                mwController.readDeviceInformation();
                settingsController.readDeviceName();
                switchController.enableNotification();
                reconnectTaskFragment.dismissDialog();
            }

            invalidateOptionsMenu();

            getRemoteSignals();
        }

        @Override
        public void disconnected() {
            if (device != null && mwController != null) {
                tryReconnect();
            } else {
                switchController.disableNotification();
            }
            invalidateOptionsMenu();
        }

        public void receivedGATTCharacteristic(GATTCharacteristic characteristic, byte[] data) {
            if (characteristic == Battery.BATTERY_LEVEL) {
                updateBatteryLevel(String.format("%s", data[0]));
            } else if (characteristic == DeviceInformation.FIRMWARE_VERSION) {
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

        /*
	        receivedGattError is no more called on disconnect
	        look at disconnect callback
	     */
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

            if (msg.isEmpty())
                msg = getString(R.string.sos_default_msg);

            for (Contact contact : contacts) {
                smsManager.sendTextMessage(contact.getPhone(), null, msg, null, null);
            }
        }
    };

    private Settings.Callbacks sCallback = new Settings.Callbacks() {

        @Override
        public void receivedDeviceName(String name) {
            deviceName = name;
        }
    };

    protected void updateSignalLevel(int rssiPercent) {
    }

    ;

    protected void updateBatteryLevel(String batteryLevel) {
    }

    ;

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

    protected void tryReconnect() {
        if (!isReconnectTaskRunning())
            reconnectTaskFragment.startNewAsyncTask(device);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startBluetoothAdapter();

        bindMetaWearService();

        if (savedInstanceState != null) {
            device = (BluetoothDevice) savedInstanceState.getParcelable(EXTRA_BLE_DEVICE);
        }

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
            if (isReconnectTaskRunning())
                reconnectTaskFragment.showDialog();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DFU:
                device = data.getParcelableExtra(EXTRA_BLE_DEVICE);
                if (device != null) {
                    mwController.connect();
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_CANCELED) {
                    finish();
                } else if (resultCode == Activity.RESULT_OK) {
                    registerBluetoothStateReceiver();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private ServiceConnection metaWearServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mwService = ((MetaWearBleService.LocalBinder) service).getService();
            broadcastManager = LocalBroadcastManager.getInstance(mwService);
            broadcastManager.registerReceiver(MetaWearBleService.getMetaWearBroadcastReceiver(),
                    MetaWearBleService.getMetaWearIntentFilter());
            mwService.useLocalBroadcastManager(broadcastManager);

            if (device != null) {
                initializeAndConnect();
                if (isDeviceConnected()) {
                    mwController.readDeviceInformation();
                    settingsController.readDeviceName();
                }
            }
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            invalidateOptionsMenu();
        }

    };

    /* (non-Javadoc)
     * @see no.nordicsemi.android.nrftoolbox.scanner.ScannerFragment.OnDeviceSelectedListener#onDeviceSelected(android.bluetooth.BluetoothDevice, java.lang.String)
     */
    @Override
    public void onDeviceSelected(BluetoothDevice device, String name) {
        if (isDeviceConnected()) {
            mwController.close(true);
            mwController = null;
        }
        ModuleActivity.device = device;
        initializeAndConnect();
    }

    private void initializeAndConnect() {
        mwController = mwService.getMetaWearController(device);
        switchController = (MechanicalSwitch) mwController.getModuleController(Module.MECHANICAL_SWITCH);
        settingsController = (Settings) mwController.getModuleController(Module.SETTINGS);
        mwController.addDeviceCallback(dCallback);
        mwController.addModuleCallback(mCallback);
        mwController.addModuleCallback(sCallback);
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
        if (vibMode == LOW_SIGNAL_VIB_MODE || vibMode == LOW_BATTERY_VIB_MODE) {
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

            if (intent != null) {
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

                    case ServicesFragment.AUDIO_VIB_ACTION:
                        vibPref = new AudioPreference(getApplicationContext());
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
        if (isDeviceConnected()) {
            mwController.readRemoteRSSI();
        }
    }

    protected void requestBatteryLevel() {
        if (isDeviceConnected()) {
            mwController.readBatteryLevel();
        }
    }

    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastManager != null) {
            broadcastManager.unregisterReceiver(MetaWearBleService.getMetaWearBroadcastReceiver());
        }

        if (isFinishing()) {
            try {
                unregisterReceiver(bluetoothStateReceiver);
            } catch (IllegalArgumentException iae) {}
        }

//        getApplicationContext().unbindService(this);
//        mwController.removeDeviceCallback(dCallback);
//        mwController.removeModuleCallback(mCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(metaWearUpdateReceiver, MetaWearBleService.getMetaWearIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(metaWearUpdateReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (device != null) {
            outState.putParcelable(EXTRA_BLE_DEVICE, device);
        }
    }

    public void unbindDevice() {
        switchController.disableNotification();
        device = null;

        if(mwController != null)
            mwController.close(true);

        mwController = null;
    }

    public boolean isDeviceConnected() {
        return (mwController != null && mwController.isConnected());
    }

    private void bindMetaWearService() {
        Intent intent = new Intent(this, BoundMwService.class);

        startService(intent);

        getApplicationContext().bindService(intent,
                metaWearServiceConnection, Context.BIND_AUTO_CREATE);

        isMwServiceBound = true;

    }

    private void unbindMetaWearService() {
        try {
            if (isMwServiceBound) {
                getApplicationContext().unbindService(metaWearServiceConnection);
                isMwServiceBound = false;
            }
        } catch (IllegalArgumentException iae) {
        }
    }

    public MetaWearController getMwController() {
        return mwController;
    }

    @Override
    public void onReconnectCancelled() {
        stopReconnectTaskAndUnbindDevice();
    }

    public void stopReconnectTaskAndUnbindDevice() {
        reconnectTaskFragment.stopAsyncTask();
        unbindDevice();
    }

    protected boolean isReconnectTaskRunning() {
        return (reconnectTaskFragment != null && reconnectTaskFragment.isRunning());
    }

    protected void registerBluetoothStateReceiver() {
        bluetoothStateReceiver = new BluetoothStateReceiver();
        registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    protected boolean startBluetoothAdapter() {
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (!bluetoothManager.getAdapter().isEnabled()) {
            final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            return true;
        } else {
            return false;
        }
    }
}
