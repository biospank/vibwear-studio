package it.vibwear.app.audio;

import java.util.LinkedList;

import android.util.Log;

import it.vibwear.app.audio.processing.ZeroCrossing;
import it.vibwear.app.audio.util.AudioUtil;

/**
 * track a history of frequencies, and determine if a new frequency is within
 * the range of the ones in the history
 *
 */
public class ConsistentFrequencyDetector implements AudioClipListener
{
    private static final String TAG = "ConsistentFreqDetector";

    private LinkedList<Integer> frequencyHistory;

    private int rangeThreshold;
    private int silenceThreshold;

    public static final int DEFAULT_SILENCE_THRESHOLD = 2000;

    private static final boolean DEBUG = false;

    public ConsistentFrequencyDetector(int historySize, int rangeThreshold,
                                       int silenceThreshold)
    {
        frequencyHistory = new LinkedList<Integer>();
        // pre-fill so modification is easy
        for (int i = 0; i < historySize; i++)
        {
            frequencyHistory.add(Integer.MAX_VALUE);
        }
        this.rangeThreshold = rangeThreshold;
        this.silenceThreshold = silenceThreshold;
    }

    @Override
    public boolean heard(short[] audioData, int sampleRate)
    {
        int frequency = ZeroCrossing.calculate(sampleRate, audioData);
        frequencyHistory.addFirst(frequency);
        // since history is always full, just remove the last
        frequencyHistory.removeLast();
        int range = calculateRange();

        if (DEBUG)
        {
            Log.d(TAG, "range: " + range + " threshold " + rangeThreshold
                    + " loud: " + AudioUtil.rootMeanSquared(audioData));
        }

        boolean heard = false;
        if (range < rangeThreshold)
        {
            // only trigger it isn't silence
            if (AudioUtil.rootMeanSquared(audioData) > silenceThreshold)
            {
                Log.d(TAG, "heard");
                heard = true;
            }
            else
            {
                Log.d(TAG, "not loud enough");
            }
        }
        return heard;
    }

    private int calculateRange()
    {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Integer val : frequencyHistory)
        {
            if (val >= max)
            {
                max = val;
            }

            if (val < min)
            {
                min = val;
            }
        }

        if (DEBUG)
        {
            StringBuilder sb = new StringBuilder();
            for (Integer val : frequencyHistory)
            {
                sb.append(val).append(" ");
            }
            Log.d(TAG, sb.toString() + " [" + (max - min) + "]");
        }
        return max - min;
    }
}
