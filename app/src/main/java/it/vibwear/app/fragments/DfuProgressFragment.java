package it.vibwear.app.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

import it.lampwireless.vibwear.app.R;

/**
 * Created by biospank on 19/02/16.
 */
public class DfuProgressFragment extends DialogFragment {
    private ProgressDialog dfuProgress= null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dfuProgress= new ProgressDialog(getActivity());
        dfuProgress.setTitle(getString(R.string.title_firmware_update));
        dfuProgress.setCancelable(false);
        dfuProgress.setCanceledOnTouchOutside(false);
        dfuProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dfuProgress.setProgress(0);
        dfuProgress.setMax(100);
        dfuProgress.setMessage(getString(R.string.message_dfu));
        return dfuProgress;
    }

    public void updateProgress(int newProgress) {
        if (dfuProgress != null) {
            dfuProgress.setProgress(newProgress);
        }
    }
}

