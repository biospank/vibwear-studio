package it.vibwear.app;

import java.util.List;
import it.lampwireless.vibwear.app.R;
import it.vibwear.app.adapters.Contact;
import it.vibwear.app.fragments.MwConnectionFragment;
import it.vibwear.app.fragments.ReconnectTaskFragment;
import it.vibwear.app.fragments.ServicesFragment;
import it.vibwear.app.receivers.BluetoothStateReceiver;
import it.vibwear.app.utils.AlarmPreference;
import it.vibwear.app.utils.AudioPreference;
import it.vibwear.app.utils.CallPreference;
import it.vibwear.app.utils.ChatPreference;
import it.vibwear.app.utils.DefaultPreference;
import it.vibwear.app.utils.SmsPreference;
import it.vibwear.app.utils.SosPreference;
import it.vibwear.app.utils.VibrationPreference;
import com.mbientlab.metawear.MetaWearBoard;
import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsManager;

public class ModuleActivity extends Activity implements MwConnectionFragment.OnMwConnectionCallbacks, ReconnectTaskFragment.OnReconnectTaskCallbacks {
    public static final int LOW_BATTERY_VIB_MODE = 0;
    public static final int LOW_SIGNAL_VIB_MODE = 1;
    public static final int NOTIFY_VIB_MODE = 2;
    public static final int REQUEST_ENABLE_BT = 1;

    private static final Short LOW_SIGNAL_VIBRATION_TIME = 3;
    private static final Short LOW_SIGNAL_VIBRATION_LENGHT = 300;
    private static final Short LOW_SIGNAL_VIBRATION_GAP = 500;
    public static final String TAG_TASK_FRAGMENT = "reconnect_task_fragment";
    public static final String TAG_MW_FRAGMENT = "mw_connect_fragment";

    protected ReconnectTaskFragment reconnectTaskFragment;
    protected MwConnectionFragment mwConnectionFragment;
    protected BluetoothStateReceiver bluetoothStateReceiver;
    private String boardMacAddress = null;


    public MetaWearBoard getMwBoard() {
        return mwConnectionFragment.getMwBoard();
    }

    protected void tryReconnect() {
        if (!isReconnectTaskRunning())
            reconnectTaskFragment.startNewAsyncTask(mwConnectionFragment.getBtDevice());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        attachMwConnection();

        attachReconnectTask();

    }

    protected void attachMwConnection() {
        FragmentManager fm = getFragmentManager();
        mwConnectionFragment = (MwConnectionFragment) fm.findFragmentByTag(TAG_MW_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mwConnectionFragment == null) {
            mwConnectionFragment = new MwConnectionFragment();
            fm.beginTransaction().add(mwConnectionFragment, TAG_MW_FRAGMENT).commit();
        }

        mwConnectionFragment.startBluetoothAdapter(this);

        mwConnectionFragment.bindMetaWearService(this);

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

    protected void vibrate(int vibMode, Intent intent) {
        if (vibMode == LOW_SIGNAL_VIB_MODE || vibMode == LOW_BATTERY_VIB_MODE) {
            Thread background = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < LOW_SIGNAL_VIBRATION_TIME; i++) {
                        mwConnectionFragment.startMotor(LOW_SIGNAL_VIBRATION_LENGHT);
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
                    mwConnectionFragment.startMotor((short) (vibPref.getVibrationTime() * 1000));
                }
            });

            background.start();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            try {
                unregisterReceiver(bluetoothStateReceiver);
            } catch (IllegalArgumentException iae) {}

            mwConnectionFragment.unbindMetawearService();
        }

    }

    @Override
    public void onReconnectCancelled() {
        stopReconnectTaskAndUnbindDevice();
    }

    public void stopReconnectTaskAndUnbindDevice() {
        reconnectTaskFragment.stopAsyncTask();
        mwConnectionFragment.unbindDevice();
    }

    protected boolean isReconnectTaskRunning() {
        return (reconnectTaskFragment != null && reconnectTaskFragment.isRunning());
    }

    protected void registerBluetoothStateReceiver() {
        bluetoothStateReceiver = new BluetoothStateReceiver();
        registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    protected void sendTextMessage() {
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

    @Override
    public void onDeviceConnect() {
        if(reconnectTaskFragment != null) {
            reconnectTaskFragment.stopAsyncTask();
            reconnectTaskFragment.dismissDialog();
        }
    }

    @Override
    public void onDeviceDisconnect() {
        tryReconnect();
    }

    @Override
    public void onDeviceFailure() {
        tryReconnect();
    }

    @Override
    public void onRemoteFailure() {
        getMwBoard().disconnect();
    }

    public boolean isDeviceConnected() {
        return mwConnectionFragment.isDeviceConnected();
    }

}
