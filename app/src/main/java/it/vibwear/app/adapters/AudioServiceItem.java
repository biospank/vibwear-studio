package it.vibwear.app.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.VibWearUtil;
import it.vibwear.app.fragments.AudioDetailFragment;
import it.vibwear.app.fragments.TaskFragment;
import it.vibwear.app.utils.AudioPreference;

public class AudioServiceItem extends ServiceItem {

    private TaskFragment mTaskFragment;
    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    //protected AudioClipLoudNoiseTask audioTask;

	public AudioServiceItem(Activity activity) {
		super(activity);
		this.switchPref = new AudioPreference(activity);

        FragmentManager fm = activity.getFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }
	}
	
	public void setIconView(ImageView icon) {
		this.iconWidget = icon;
        if(isHardwareSupported(PackageManager.FEATURE_MICROPHONE)) {
            this.iconWidget.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(switchPref.switchState()) {
                        mTaskFragment.startNewAsyncTask();
                    } else {
                        mTaskFragment.stopAsyncTask();
                    }

                    iconWidget.setImageResource(switchPref.getImage());

                    textWidget.setText(VibWearUtil.getAudioSummarySpanText(switchPref.getLabel()));

                }
            });
        }

		showUserIconSettings();
	}

	public void setTextView(TextView text) {
		this.textWidget = text;
        if(isHardwareSupported(PackageManager.FEATURE_MICROPHONE)) {
            this.textWidget.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    AudioDetailFragment audioFrag = new AudioDetailFragment();
                    ft.replace(R.id.servicesLayout, audioFrag, "audioDetail");
                    ft.addToBackStack(null);
                    ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                    ft.commit();
                }
            });
        }

		showUserTextSettings();
	}

	public void showUserTextSettings() {
        
		textWidget.setText(VibWearUtil.getSosSummarySpanText(switchPref.getLabel()));
	
	}
	
}
