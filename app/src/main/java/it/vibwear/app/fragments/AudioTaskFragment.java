package it.vibwear.app.fragments;

/**
 * Created by biospank on 03/05/15.
 */

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

import it.vibwear.app.audio.AudioClipLoudNoiseTask;
import it.vibwear.app.audio.LoudNoiseDetector;
import it.vibwear.app.utils.AudioPreference;

/**
 * This Fragment manages a single background task and retains
 * itself across configuration changes.
 */
public class AudioTaskFragment extends Fragment {

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

    // private TaskCallbacks mCallbacks;
    private AudioClipLoudNoiseTask mTask;
    private Activity mActivity;

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity =  activity;
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

    public void startNewAsyncTask() {
        // Create and execute the background task.
        mTask = new AudioClipLoudNoiseTask(mActivity, "AudioClipLoudNoiseTask");
        mTask.execute(new LoudNoiseDetector(new AudioPreference(mActivity)));
    }

    public void stopAsyncTask() {
        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
            mTask.cancel(true);
            mTask = null;
        }
    }



}