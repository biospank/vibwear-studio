package it.vibwear.app.audio;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import it.vibwear.app.ModuleActivity;
import it.vibwear.app.VibWearActivity;
import it.vibwear.app.fragments.ServicesFragment;

/**
 * Created by biospank on 28/04/15.
 */
public class AudioClipLoudNoiseTask extends AsyncTask<LoudNoiseDetector, Void, Void> {
    private static final String TAG = "AudioClipConFreqTask";

    private Context context;
    private String taskName;
    private AudioClipRecorder recorder;
    private Intent broadcastIntent;


    public AudioClipLoudNoiseTask(Context context, String taskName) {
        this.context = context;
        this.taskName = taskName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        broadcastIntent = new Intent();
        broadcastIntent.setAction(ServicesFragment.AUDIO_VIB_ACTION);

    }

    @Override
    protected Void doInBackground(LoudNoiseDetector... listeners) {

        Log.d(TAG, "recording consistent frequency");

        AudioClipListener listener = listeners[0];

        recorder =  new AudioClipRecorder(listener, this);

        try {
            // start recording
            recorder.startRecording();
        } catch (IllegalStateException se) {
            Log.e(TAG, "failed to record, recorder not setup properly", se);
        } catch (RuntimeException se) {
            Log.e(TAG, "failed to record, recorder already being used", se);
        }

        return null;

    }


    public void notifyHeard() {
        //broadcastIntent.putExtra("audioLevel", audioLevel);
        context.getApplicationContext().sendBroadcast(broadcastIntent);
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
