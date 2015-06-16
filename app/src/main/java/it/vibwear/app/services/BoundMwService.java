package it.vibwear.app.services;

import android.content.Intent;

import com.mbientlab.metawear.api.MetaWearBleService;

/**
 * Created by biospank on 16/06/15.
 */
public class BoundMwService extends MetaWearBleService {
    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }
}
