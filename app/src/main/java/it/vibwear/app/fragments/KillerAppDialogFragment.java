package it.vibwear.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
        KillerAppDialogFragment.context = context;
        KillerAppDialogFragment fragment = new KillerAppDialogFragment();

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = setupView();

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_launcher)
                .setTitle(R.string.killer_app_dialog_title)
                .setView(dialogView)
                .setPositiveButton(R.string.pref_dialog_positive_button_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
            )
            .create();

    }

    private View setupView() {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_killer_app_dialog, null, false);

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


        return dialogView;
    }

    private void createKillerAppIcons(View view) {
        Bundle preference = getArguments();
        String[] killerAppPackages = preference.getStringArray("killer.apps");

        if(killerAppPackages != null) {

            List<String> killerAppList = Arrays.asList(killerAppPackages);

            if(!killerAppList.isEmpty()) {
                Iterator<String> iterator = killerAppList.iterator();

                String packageName = iterator.next();

                ImageView icon = (ImageView) view.findViewById(R.id.killerAppIcon1);
                icon.setImageDrawable((new AppManager(getActivity(), packageName)).getIconApp());

                //FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

                //for(String packageName : killerAppPackages) {
                //fragmentTransaction.add(R.id.killerAppIconsLayout, KillerAppIconFragment.newInstance(killerAppPackages[0]));

                //}

                //fragmentTransaction.commit();

            }
        }
    }

    private void savePreference(boolean state) {
        SharedPreferences.Editor editor;
        SharedPreferences settings = KillerAppDialogFragment.context.getSharedPreferences(HIDE_ME_PREF_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putBoolean(HIDE_ME_KEY, state);

        editor.commit();

    }

    public boolean isHideMe() {
        SharedPreferences settings = KillerAppDialogFragment.context.getSharedPreferences(HIDE_ME_PREF_NAME,
                Context.MODE_PRIVATE);
        return settings.getBoolean(HIDE_ME_KEY, false);

    }

    public boolean isFirstRun() {
        SharedPreferences settings = KillerAppDialogFragment.context.getSharedPreferences(ONE_TIME_DIALOG, Context.MODE_PRIVATE);
        return settings.getBoolean(ONE_TIME_DIALOG_KEY, true);

    }

    public void setFirstRun(boolean firsRun) {
        SharedPreferences.Editor editor;
        SharedPreferences settings = KillerAppDialogFragment.context.getSharedPreferences(ONE_TIME_DIALOG,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putBoolean(ONE_TIME_DIALOG_KEY, firsRun);

        editor.commit();

    }
}
