package it.vibwear.app.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.mbientlab.metawear.AsyncOperation;
import com.mbientlab.metawear.DataSignal;
import com.mbientlab.metawear.Message;
import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.RouteManager;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.module.DataProcessor;
import com.mbientlab.metawear.module.Haptic;
import com.mbientlab.metawear.module.Settings;
import com.mbientlab.metawear.module.Switch;
import java.util.Map;
import it.vibwear.app.ModuleActivity;
import it.vibwear.app.VibWearActivity;
import it.vibwear.app.services.BoundMwService;

/**
 * Created by biospank on 18/01/16.
 */
public class MwConnectionFragment extends Fragment {
    private VibWearActivity mActivity;
    private OnMwConnectionCallbacks mCallbacks;

    private BluetoothDevice device;
    private MetaWearBoard mwBoard;
    private MetaWearBleService.LocalBinder mwService;
    private Haptic hapticController;
    private Switch switchController;
    private Settings settingsController;
    private String deviceName;

    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    public interface OnMwConnectionCallbacks {
        void onDeviceConnect();
        void onDeviceDisconnect();
        void onDeviceFailure();
    }

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity =  (VibWearActivity)activity;
        this.mCallbacks = (OnMwConnectionCallbacks)activity;
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private final MetaWearBoard.ConnectionStateHandler stateHandler = new MetaWearBoard.ConnectionStateHandler() {
        @Override
        public void connected() {
            if (isDeviceConnected()) {
                initModules();
                readDeviceInfo();
                readDeviceName();
                readMechanicalSwitch();
                getRemoteSignals();

                mActivity.updateUi();

                mCallbacks.onDeviceConnect();

            }

        }

        @Override
        public void disconnected() {
            mActivity.updateUi();

            if (device != null && mwBoard != null) {
                mCallbacks.onDeviceDisconnect();
            }

        }

        @Override
        public void failure(int status, Throwable error) {
            if (device != null && mwBoard != null) {
                mCallbacks.onDeviceFailure();
            }

            mActivity.updateUi();

        }
    };

    protected void initModules() {
        try {
            switchController = mwBoard.getModule(Switch.class);
            settingsController = mwBoard.getModule(Settings.class);
            settingsController.handleEvent().fromDisconnect().monitor(new DataSignal.ActivityHandler() {
                @Override
                public void onSignalActive(Map<String, DataProcessor> map, DataSignal.DataToken dataToken) {

                }
            }).commit();
            hapticController = mwBoard.getModule(Haptic.class);
        } catch (UnsupportedModuleException ume) {
            Toast.makeText(mActivity, ume.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public MetaWearBoard getMwBoard() {
        return mwBoard;
    }

    public BluetoothDevice getBtDevice() {
        return device;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String boardName) {
        this.deviceName = boardName;
    }

    public void readDeviceInfo() {

        AsyncOperation<MetaWearBoard.DeviceInformation> result = mwBoard.readDeviceInformation();

        result.onComplete(new AsyncOperation.CompletionHandler<MetaWearBoard.DeviceInformation>() {
            @Override
            public void success(final MetaWearBoard.DeviceInformation deviceInfo) {

            }

            @Override
            public void failure(Throwable error) {
                Log.e("readDeviceInfo", "Error reading device info", error);
            }
        });
    }

    public void readDeviceName() {
        AsyncOperation<Settings.AdvertisementConfig> result = settingsController.readAdConfig();

        result.onComplete(new AsyncOperation.CompletionHandler<Settings.AdvertisementConfig>() {
            @Override
            public void success(final Settings.AdvertisementConfig deviceInfo) {
                setDeviceName(deviceInfo.deviceName());
            }

            @Override
            public void failure(Throwable error) {
                Log.e("readDeviceName", "Error reading device name", error);
            }
        });
    }

    public void readRemoteRssi() {
        AsyncOperation<Integer> result = mwBoard.readRssi();

        result.onComplete(new AsyncOperation.CompletionHandler<Integer>() {
            @Override
            public void success(final Integer rssi) {

                final int rssiPercent = (int) (100.0f * (127.0f + rssi) / (127.0f + 20.0f));

                mActivity.updateSignalLevel(rssiPercent);
            }

            @Override
            public void failure(Throwable error) {
                Log.e("UpdateRssi", "Error reading RSSI value", error);
            }
        });
    }

    public void readBatteryLevel() {
        AsyncOperation<Byte> result = mwBoard.readBatteryLevel();

        result.onComplete(new AsyncOperation.CompletionHandler<Byte>() {
            @Override
            public void success(final Byte data) {
                mActivity.updateBatteryLevel(String.format("%s", data));
            }

            @Override
            public void failure(Throwable error) {
                Log.e("UpdateBatteryLevel", "Error reading battery level", error);
            }
        });
    }

    public void readMechanicalSwitch() {
        try {
            switchController = mwBoard.getModule(Switch.class);
        } catch (UnsupportedModuleException e) {
            e.printStackTrace();
        }
        switchController.routeData().fromSensor().stream("switch_stream").commit()
                .onComplete(new AsyncOperation.CompletionHandler<RouteManager>() {
                    @Override
                    public void success(RouteManager result) {
                        result.subscribe("switch_stream", new RouteManager.MessageHandler() {
                            @Override
                            public void process(Message msg) {

                                if (msg.getData(Boolean.class)) {

                                } else {
                                    mActivity.sendTextMessage();
                                }
                            }
                        });
                    }
                });

    }

    public void startMotor(Short length) {
        hapticController.startMotor(length);
    }

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

    public void requestSignalLevel() {
        if (isDeviceConnected()) {
            readRemoteRssi();
        }
    }

    public void requestBatteryLevel() {
        if (isDeviceConnected()) {
            readBatteryLevel();
        }
    }

    public boolean changeBoardName(String boardName) {
        if(isDeviceConnected()) {
            if(settingsController == null) {
                try {
                    settingsController = mwBoard.getModule(Settings.class);
                } catch (UnsupportedModuleException ume) {
                    return false;
                }
            }

            settingsController.configure().setDeviceName(boardName).commit();

            return true;
        }

        return false;

    }

    public boolean startBluetoothAdapter(ModuleActivity act) {
        BluetoothManager bluetoothManager =
                (BluetoothManager) act.getSystemService(Context.BLUETOOTH_SERVICE);

        if (!bluetoothManager.getAdapter().isEnabled()) {
            final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, ModuleActivity.REQUEST_ENABLE_BT);
            return true;
        } else {
            return false;
        }
    }

    public void bindMetaWearService(ModuleActivity act) {
        Intent intent = new Intent(act, BoundMwService.class);

        act.startService(intent);

        act.getApplicationContext().bindService(intent,
                metaWearServiceConnection, Context.BIND_AUTO_CREATE);

    }

    public void unbindMetawearService() {
        mActivity.getApplicationContext().unbindService(metaWearServiceConnection);
    }

    private ServiceConnection metaWearServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mwService = (MetaWearBleService.LocalBinder) service;
            mwService.executeOnUiThread();

//            if (device != null) {
//                initializeAndConnect();
//                if (isDeviceConnected()) {
//                    mwController.readDeviceInformation();
//                    settingsController.readDeviceName();
//                }
//            }
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            mActivity.updateUi();
        }

    };

    public void onDeviceSelected(BluetoothDevice device, String name) {
        if (isDeviceConnected()) {
            mwBoard.disconnect();
            mwBoard = null;
        }

        this.device = device;

        initializeAndConnect();
    }

    private void initializeAndConnect() {
        // Create a MetaWear board object for the Bluetooth Device
        mwBoard = mwService.getMetaWearBoard(device);
        mwBoard.setConnectionStateHandler(stateHandler);
        mwBoard.connect();
        //boardMacAddress = mwBoard.getMacAddress();
    }

    public boolean isDeviceConnected() {
        return (mwBoard != null && mwBoard.isConnected());
    }


    public void unbindDevice() {
        mwBoard.disconnect();
        mwBoard = null;
//        switchController.disableNotification();
//        device = null;
//
//        if(mwController != null)
//            mwController.close(true);
//
//        mwController = null;
    }



//    public void serializeBoard() {
//        SharedPreferences.Editor editor= getSharedPreferences("board_key_pref", MODE_PRIVATE).edit();
//
//        editor.putString("cicio", new String(mwBoard.serializeState()));
//        editor.commit();
//    }
//
//    public void deserializeBoard() {
//        SharedPreferences sharedPref = getSharedPreferences("board_key_pref", MODE_PRIVATE);
//        String stateStr = sharedPref.getString("cicio", null);
//
//        if (stateStr != null) {
//            mwBoard.deserializeState(stateStr.getBytes());
//        } else {
//            Log.i("ModuleActivity", "Cannot find state for this board");
//        }
//    }
//
//    protected void restartMwService() {
//        boardMacAddress = mwBoard.getMacAddress();
//
//        stopService(new Intent(ModuleActivity.this, BoundMwService.class));
//
//        bindMetaWearService();
//
//        final BluetoothManager btManager =
//                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        final BluetoothDevice remoteDevice =
//                btManager.getAdapter().getRemoteDevice(boardMacAddress);
//
//        // Create a MetaWear board object for the Bluetooth Device
//        mwBoard = mwService.getMetaWearBoard(remoteDevice);
//    }

}
