package it.vibwear.app.utils;

import android.util.Log;

import java.util.LinkedList;

/**
 * Created by biospank on 13/04/15.
 */
public class ConsistentFrequencyDetector implements AudioClipListener {
    private static final String TAG = "ConsistentFrequencyDetector";
    private LinkedList<Integer> frequencyHistory;
    private int rangeThreshold; private int silenceThreshold;
    public static final int DEFAULT_SILENCE_THRESHOLD = 2000;

    public ConsistentFrequencyDetector(int historySize, int rangeThreshold, int silenceThreshold) {
        frequencyHistory = new LinkedList<Integer>();

        // pre-fill so modification is easy
        for (int i = 0; i < historySize; i++) {
            frequencyHistory.add(Integer.MAX_VALUE);
        }

        this.rangeThreshold = rangeThreshold;
        this.silenceThreshold = silenceThreshold;
    }

    @Override
    public boolean heard(short[] audioData, int sampleRate) {
        int frequency = ZeroCrossing.calculate(sampleRate, audioData);

        frequencyHistory.addFirst(frequency);
        // since history is always full, just remove the last
        frequencyHistory.removeLast();

        int range = calculateRange();
        boolean heard = false;

        if (range < rangeThreshold) {
            // only trigger it isn't silence
            if (AudioUtil.rootMeanSquared(audioData) > silenceThreshold) {
                Log.d(TAG, "heard");
                heard = true;
            } else {
                Log.d(TAG, "not loud enough");
            }
        }

        return heard;
    }

    private int calculateRange() {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (Integer val : frequencyHistory) {
            if (val >= max) {
                max = val;
            }

            if (val < min) {
                min = val;
            }
        }

        return max - min;
    }

}
