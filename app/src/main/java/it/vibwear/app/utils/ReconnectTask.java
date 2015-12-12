package it.vibwear.app.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.mbientlab.metawear.MetaWearBoard;


/**
 * Created by biospank on 28/06/15.
 */
public class ReconnectTask extends AsyncTask<BleScanner, Boolean, Boolean> {

    private MetaWearBoard mwBoard;
    private BleScanner bleScanner;

    public ReconnectTask(MetaWearBoard board) {
        this.mwBoard = board;
    }

    @Override
    protected Boolean doInBackground(BleScanner... params) {

        bleScanner = params[0];

        bleScanner.setKeepScanning(true);
        bleScanner.setDeviceFound(false);

        while (bleScanner.keepScanning()) {
            try {
                Thread.sleep(bleScanner.SCAN_RETRY_DELAY);
                bleScanner.startScan();
                Thread.sleep(bleScanner.SCAN_DURATION);
                bleScanner.stopScan();
            } catch (InterruptedException e) {}

            if(bleScanner.deviceFound() || isCancelled()) {
                bleScanner.setKeepScanning(false);
            }
        }

        return bleScanner.deviceFound();

    }

    @Override
    protected void onPostExecute(Boolean deviceFound) {
        super.onPostExecute(deviceFound);

        if(deviceFound)
            mwBoard.connect();

    }

    @Override
    protected void onCancelled() {
        bleScanner.setKeepScanning(false);

        super.onCancelled();

    }

}
