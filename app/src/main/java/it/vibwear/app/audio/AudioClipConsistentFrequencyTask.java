package it.vibwear.app.audio;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by biospank on 22/04/15.
 */
public class AudioClipConsistentFrequencyTask extends AsyncTask<ConsistentFrequencyDetector, Void, Boolean> {
    private static final String TAG = "AudioClipConFreqTask";

    private Context context;
    private String taskName;


    public AudioClipConsistentFrequencyTask(Context context, String taskName) {
        this.context = context;
        this.taskName = taskName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(ConsistentFrequencyDetector... listeners) {
        if (listeners.length == 0) {
            return false;
        }

        Log.d(TAG, "recording consistent frequency");

        AudioClipListener listener = listeners[0];

        AudioClipRecorder recorder =  new AudioClipRecorder(listener);

        //set to true if the recorder successfully detected something //false if it was canceled or otherwise stopped
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


    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

}
