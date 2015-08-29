package it.vibwear.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import it.lampwireless.vibwear.app.R;

/**
 * Created by biospank on 29/08/15.
 */
public class StopNotificationDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle extraInfo = getArguments();

        String sourcePackageName = extraInfo.getString("sourcePackageName");

        Drawable sourcePackageIcon = null;

        try {
            sourcePackageIcon = getActivity().getPackageManager().getApplicationIcon(sourcePackageName);
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(sourcePackageIcon).setTitle(sourcePackageName)
                .setMessage(R.string.stop_notification_dialog_msg)
                .setPositiveButton(R.string.stop_notification_dialog_btn_stop, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton(R.string.stop_notification_dialog_btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }
}
