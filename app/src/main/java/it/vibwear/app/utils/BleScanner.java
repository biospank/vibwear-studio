package it.vibwear.app.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;

import com.mbientlab.metawear.api.MetaWearController;

/**
 * Created by biospank on 23/06/15.
 */
public class BleScanner {
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mIsCustomUUID;
    public final long SCAN_RETRY_DELAY = 2000;
    public final long SCAN_DURATION = 500;
    private final short RSSI_MIN_THRESHOLD = 20;
    private BluetoothDevice mDevice;
    private static boolean found;
    private boolean keepScanning;

    public BleScanner(Context context, BluetoothDevice device) {
        mDevice = device;
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
    }

    /**
     * Scan for 5 seconds and then stop scanning when a BluetoothLE device is found then mLEScanCallback is activated This will perform regular scan for custom BLE Service UUID and then filter out.
     * using class ScannerServiceParser
     */
    public void startScan() {
        mBluetoothAdapter.startLeScan(mLEScanCallback);
    }

    public void stopScan() {
        mBluetoothAdapter.stopLeScan(mLEScanCallback);
    }

    private BluetoothAdapter.LeScanCallback mLEScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            final int rssiPercent = (int) (100.0f * (127.0f + rssi) / (127.0f + 20.0f));

            if(device.getAddress().equals(mDevice.getAddress())) {
                if(rssiPercent > RSSI_MIN_THRESHOLD) {
                    setDeviceFound(true);
                } else {
                    setDeviceFound(false);
                }
            }
        }
    };

    public boolean deviceFound() {
        return found;
    }

    public void setDeviceFound(boolean found) {
        this.found = found;
    }

    public boolean keepScanning() {
        return keepScanning;
    }

    public void setKeepScanning(boolean keepScanning) {
        this.keepScanning = keepScanning;
    }
}
