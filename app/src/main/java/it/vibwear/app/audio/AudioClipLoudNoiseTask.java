package it.vibwear.app.audio;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import it.vibwear.app.ModuleActivity;
import it.vibwear.app.VibWearActivity;

/**
 * Created by biospank on 28/04/15.
 */
public class AudioClipLoudNoiseTask extends AsyncTask<LoudNoiseDetector, Void, Boolean> {
    private static final String TAG = "AudioClipConFreqTask";

    private Context context;
    private String taskName;
    private AudioClipRecorder recorder;


    public AudioClipLoudNoiseTask(Context context, String taskName) {
        this.context = context;
        this.taskName = taskName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(LoudNoiseDetector... listeners) {
        if (listeners.length == 0) {
            return false;
        }

        Log.d(TAG, "recording consistent frequency");

        AudioClipListener listener = listeners[0];

        recorder =  new AudioClipRecorder(listener, this);

        // set to true if the recorder successfully detected something
        // false if it was canceled or otherwise stopped
        boolean heard = false;

        try {
            // start recording
            heard = recorder.startRecording();
        } catch (IllegalStateException se) {
            Log.e(TAG, "failed to record, recorder not setup properly", se);
            heard = false;
        } catch (RuntimeException se) {
            Log.e(TAG, "failed to record, recorder already being used", se);
            heard = false;
        }

        return heard;
    }


    public void notifyHeard() {
        ((VibWearActivity)context).onHeard();
    }

    @Override
    protected void onPostExecute(Boolean heard) {
        super.onPostExecute(heard);

        if(heard) {
            ((VibWearActivity)context).onHeard();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        if(recorder.isRecording()) {
            recorder.stopRecording();
            recorder.done();
        }
    }

}
