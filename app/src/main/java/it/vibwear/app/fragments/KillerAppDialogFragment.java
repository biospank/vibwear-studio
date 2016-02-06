package it.vibwear.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.utils.AppManager;

/**
 * Created by biospank on 06/02/16.
 */
public class KillerAppDialogFragment extends DialogFragment {

    public static KillerAppDialogFragment newInstance() {
        KillerAppDialogFragment fragment = new KillerAppDialogFragment();

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_killer_app_dialog, null, false);

        Bundle preference = getArguments();
        String[] killerAppPackages = preference.getStringArray("killer.apps");

        //FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        //for(String packageName : killerAppPackages) {
            //fragmentTransaction.add(R.id.killerAppIconsLayout, KillerAppIconFragment.newInstance(killerAppPackages[0]));

        //}

        //fragmentTransaction.commit();

        ImageView icon = (ImageView)dialogView.findViewById(R.id.killerAppIcon1);
        icon.setImageDrawable((new AppManager(getActivity(), killerAppPackages[0])).getIconApp());

        TextView warningText = (TextView)dialogView.findViewById(R.id.killerAppWarningText);
        warningText.setText(R.string.killer_app_dialog_warning_text);

        CheckBox chkShow = (CheckBox)dialogView.findViewById(R.id.chkShowDialog);
        TextView txtShow = (TextView)dialogView.findViewById(R.id.txtShowDialog);
        txtShow.setText(R.string.killer_app_dialog_hide_me_text);

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_launcher)
                .setTitle("Killer app dialog")
                    //.setMessage("Un teste molto molto ma molto lungo da visualizzare come messaggio della dialog")
                .setView(dialogView)
                .setPositiveButton(R.string.pref_dialog_positive_button_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
                )
//            .setNegativeButton(R.string.pref_dialog_negative_button_text,
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                            ((FragmentAlertDialog) getActivity()).doNegativeClick();
//                        }
//                    }
//            )
            .create();


    }

}
