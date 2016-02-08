package it.vibwear.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import it.lampwireless.vibwear.app.R;
import it.vibwear.app.utils.AppManager;

/**
 * Created by biospank on 06/02/16.
 */
public class KillerAppDialogFragment extends DialogFragment {
    private static final String ONE_TIME_DIALOG = "ONE_TIME_DIALOG";
    private static final String ONE_TIME_DIALOG_KEY = "is.first.run";
    private static final String HIDE_ME_PREF_NAME = "HIDE_ME_DIALOG";
    private static final String HIDE_ME_KEY = "hide.me.key";

    private static Context context;

    public static KillerAppDialogFragment newInstance(Context context) {
        KillerAppDialogFragment fragment = new KillerAppDialogFragment();

        return fragment;
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        View dialogView = setupView();
//
//        return new AlertDialog.Builder(getActivity())
//                .setIcon(R.drawable.ic_launcher)
//                .setTitle(R.string.killer_app_dialog_title)
//                .setView(dialogView)
//                .setPositiveButton(R.string.pref_dialog_positive_button_text,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//
//                            }
//                        }
//            )
//            .create();
//
//    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.fragment_killer_app_dialog,
                container, false);

        createKillerAppIcons(dialogView);

        TextView warningText = (TextView)dialogView.findViewById(R.id.killerAppWarningText);
        warningText.setText(R.string.killer_app_dialog_warning_text);

        final CheckBox chkShow = (CheckBox)dialogView.findViewById(R.id.chkShowDialog);
        chkShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreference(chkShow.isChecked());
            }
        });


        TextView txtShow = (TextView)dialogView.findViewById(R.id.txtShowDialog);
        txtShow.setText(R.string.killer_app_dialog_hide_me_text);
        txtShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkShow.setChecked(!chkShow.isChecked());
                savePreference(chkShow.isChecked());
            }
        });

        Dialog dialog = getDialog();

        dialog.setTitle(getString(R.string.killer_app_dialog_title));

        return dialogView;
    }

    private void createKillerAppIcons(View view) {
        Bundle preference = getArguments();
        String[] killerAppPackages = preference.getStringArray("killer.apps");

        if(killerAppPackages != null) {

            List<String> killerAppList = Arrays.asList(killerAppPackages);

            if(!killerAppList.isEmpty()) {
                Iterator<String> iterator = killerAppList.iterator();

                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

                while (iterator.hasNext())
                    fragmentTransaction.add(R.id.killerAppIconsLayout, KillerAppIconFragment.newInstance(iterator.next()));

                fragmentTransaction.commit();

            }
        }
    }

    private void savePreference(boolean state) {
        SharedPreferences.Editor editor;
        SharedPreferences settings = context.getSharedPreferences(HIDE_ME_PREF_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putBoolean(HIDE_ME_KEY, state);

        editor.commit();

    }

    public boolean isHideMe(Context context) {
        SharedPreferences settings = context.getSharedPreferences(HIDE_ME_PREF_NAME,
                Context.MODE_PRIVATE);
        return settings.getBoolean(HIDE_ME_KEY, false);

    }

    public boolean isFirstRun(Context context) {
        SharedPreferences settings = context.getSharedPreferences(ONE_TIME_DIALOG, Context.MODE_PRIVATE);
        return settings.getBoolean(ONE_TIME_DIALOG_KEY, true);

    }

    public void setFirstRun(Context context, boolean firsRun) {
        SharedPreferences.Editor editor;
        SharedPreferences settings = context.getSharedPreferences(ONE_TIME_DIALOG,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putBoolean(ONE_TIME_DIALOG_KEY, firsRun);

        editor.commit();

    }
}
