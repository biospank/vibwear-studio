package it.vibwear.app.audio;

/**
 * Created by biospank on 13/04/15.
 */
public interface AudioClipListener {
    public boolean heard(short[] data, int sampleRate);
}
