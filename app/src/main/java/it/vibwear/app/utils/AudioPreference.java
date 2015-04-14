package it.vibwear.app.utils;

import android.app.Activity;
import android.content.Context;

/**
 * Created by biospank on 13/04/15.
 */
public class AudioPreference implements SwitchPreference {

    protected Context context;

    public AudioPreference(Context context) {
        super();
        this.context = context;
    }

    @Override
    public boolean switchState() {
        return false;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public boolean getState() {
        return false;
    }

    @Override
    public int getImage() {
        return 0;
    }
}
