package it.vibwear.app.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.ModuleActivity;
import it.vibwear.app.utils.BleScanner;
import it.vibwear.app.utils.ReconnectTask;

/**
 * Created by biospank on 28/06/15.
 */
public class ReconnectTaskFragment extends Fragment {

    // private TaskCallbacks mCallbacks;
    private ReconnectTask mTask;
    private ModuleActivity mActivity;
    private ProgressDialog mProgressDialog;

    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    interface TaskCallbacks {
        void onPreExecute();
        void onProgressUpdate(int percent);
        void onCancelled();
        void onPostExecute();
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
        this.mActivity =  (ModuleActivity)activity;
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
        //mCallbacks = null;
    }

    public void startNewAsyncTask(BluetoothDevice device) {
        // Create and execute the background task.
        mTask = new ReconnectTask(mActivity.getMwController());
        mTask.execute(new BleScanner(mActivity, device));
        showDialog();
    }

    public void stopAsyncTask() {
        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
            mTask.cancel(true);
            mTask = null;
            mProgressDialog.dismiss();
        }
    }

    public boolean isRunning() {
        return ((mTask != null) &&
                (mTask.getStatus() == AsyncTask.Status.RUNNING ||
                        mTask.getStatus() == AsyncTask.Status.PENDING));
    }

    public void showDialog() {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setTitle(R.string.reconnectProgressTitle);
        mProgressDialog.setMessage(getResources().getString(R.string.reconnectProgressMsg));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false); // set to true and enable the following to make it cancelable
//        mProgressDialog.setCanceledOnTouchOutside(false);
//        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                stopAsyncTask();
//                if(mActivity.getMwController() != null)
//                    mActivity.unbindDevice();
//            }
//        });
        mProgressDialog.show();
    }

    public void dismissDialog() {
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
    }

}
