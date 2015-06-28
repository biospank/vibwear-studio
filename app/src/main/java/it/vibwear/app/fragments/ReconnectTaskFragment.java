package it.vibwear.app.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Bundle;

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
    }

    public void stopAsyncTask() {
        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    public boolean isRunning() {
        return ((mTask != null) &&
                (mTask.getStatus() == AsyncTask.Status.RUNNING ||
                        mTask.getStatus() == AsyncTask.Status.PENDING));
    }


}
