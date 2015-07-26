package it.vibwear.app.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.VibWearUtil;
import it.vibwear.app.fragments.AudioDetailFragment;
import it.vibwear.app.fragments.AudioTaskFragment;
import it.vibwear.app.utils.AudioPreference;

public class AudioServiceItem extends ServiceItem {

    private AudioTaskFragment mTaskFragment;
    private static final String TAG_TASK_FRAGMENT = "audio_task_fragment";

    //protected AudioClipLoudNoiseTask audioTask;

	public AudioServiceItem(Activity activity) {
		super(activity);
		this.switchPref = new AudioPreference(activity);

        FragmentManager fm = activity.getFragmentManager();
        mTaskFragment = (AudioTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mTaskFragment == null) {
            mTaskFragment = new AudioTaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }
	}
	
	public void setIconView(ImageView icon) {
		this.iconWidget = icon;
        if(isHardwareSupported(PackageManager.FEATURE_MICROPHONE)) {
            this.iconWidget.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (switchPref.switchState()) {
                        mTaskFragment.startNewAsyncTask();
                    } else {
                        mTaskFragment.stopAsyncTask();
                    }

                    iconWidget.setImageResource(switchPref.getImage());

                    setLocalizedText();

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

        setLocalizedText();
	
	}

    @Override
    public boolean consume(Intent intent) {
        return switchPref.getState();
    }

    public void turnOffAudio() {
        if (switchPref.getState()) {
            mTaskFragment.stopAsyncTask();
            switchPref.switchState();
        }
    }

    private void setLocalizedText() {
        String lang = Locale.getDefault().getLanguage();

        if(lang == "en")
            textWidget.setText(VibWearUtil.getAudioSummarySpanText(switchPref.getLabel()));
        else
            textWidget.setText(switchPref.getLabel());
    }

}
