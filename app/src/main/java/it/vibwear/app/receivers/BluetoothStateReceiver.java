package it.vibwear.app.receivers;

/**
 * Created by biospank on 16/07/15.
 */

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import it.vibwear.app.ModuleActivity;

public class BluetoothStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

        if (state == BluetoothAdapter.STATE_TURNING_OFF) {
            ((ModuleActivity) context).stopReconnectTaskAndUnbindDevice();
        }
    }
}