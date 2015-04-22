package it.vibwear.app.audio.util;

import java.io.IOException;
import java.io.PrintWriter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;


public class AudioUtil {
    private static final String TAG = "AudioUtil";
    
    /**
     * creates a media recorder, or throws a {@link IOException} if 
     * the path is not valid. 
     * @param sdCardPath should contain a .3gp extension
     */
    public static MediaRecorder prepareRecorder(String sdCardPath) throws IOException {

        if (!isStorageReady()) {
            throw new IOException("SD card is not available");
        }

        MediaRecorder recorder = new MediaRecorder();
        //set a custom listener that just logs any messages
        RecorderErrorLoggerListener recorderListener =
                new RecorderErrorLoggerListener();
        recorder.setOnErrorListener(recorderListener);
        recorder.setOnInfoListener(recorderListener);

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        Log.d(TAG, "recording to: " + sdCardPath);
        recorder.setOutputFile(sdCardPath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.prepare();

        return recorder;
    }
    
    private static boolean isStorageReady() {
            String cardstatus = Environment.getExternalStorageState();
            if (cardstatus.equals(Environment.MEDIA_REMOVED)
                || cardstatus.equals(Environment.MEDIA_UNMOUNTED)
                || cardstatus.equals(Environment.MEDIA_UNMOUNTABLE)
                || cardstatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
                return false;
            } else {
                if (cardstatus.equals(Environment.MEDIA_MOUNTED))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
    }
    
    public static boolean hasMicrophone(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
    }
    
    public static boolean isSilence(short [] data) {
        boolean silence = false;
        int RMS_SILENCE_THRESHOLD = 2000;

        if (rootMeanSquared(data) < RMS_SILENCE_THRESHOLD) {
            silence = true;
        }

        return silence;
    }
    
    public static double rootMeanSquared(short[] nums) {
        double ms = 0;

        for (int i = 0; i < nums.length; i++) {
            ms += nums[i] * nums[i];
        }

        ms /= nums.length;

        return Math.sqrt(ms);
    }
    
    public static int countZeros(short [] audioData) {
        int numZeros = 0;
        
        for (int i = 0; i < audioData.length; i++)
        {
            if (audioData[i] == 0)
            {
                numZeros++;
            }
        }
        
        return numZeros;
    }

    public static double secondsPerSample(int sampleRate)
    {
        return 1.0/(double)sampleRate;
    }
    
    public static int numSamplesInTime(int sampleRate, float seconds) {
        return (int)((float)sampleRate * (float)seconds);
    }
    
    public static void outputData(short [] data, PrintWriter writer) {
        for (int i = 0; i < data.length; i++) {
            writer.println(String.valueOf(data[i]));
        }

        if (writer.checkError()) {
            Log.w(TAG, "Error writing sensor event data");
        }
    }
}
