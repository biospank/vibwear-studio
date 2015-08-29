package it.vibwear.app.handlers;

import android.app.DialogFragment;
import android.os.Handler;

/**
 * Created by biospank on 29/08/15.
 */
public class StopDialogHandler extends Handler {
    private DialogFragment dialog;

    public StopDialogHandler(DialogFragment dialog) {
        this.dialog = dialog;
    }

    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case 0:
                dialog.dismiss();
                break;

            default:
                break;
        }
    }
}
