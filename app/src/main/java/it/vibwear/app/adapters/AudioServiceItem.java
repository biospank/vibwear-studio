package it.vibwear.app.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.VibWearUtil;
import it.vibwear.app.audio.AudioClipConsistentFrequencyTask;
import it.vibwear.app.audio.AudioClipLoudNoiseTask;
import it.vibwear.app.audio.AudioClipRecorder;
import it.vibwear.app.audio.ConsistentFrequencyDetector;
import it.vibwear.app.audio.LoudNoiseDetector;
import it.vibwear.app.fragments.AudioDetailFragment;
import it.vibwear.app.fragments.SosDetailFragment;
import it.vibwear.app.utils.AudioPreference;
import it.vibwear.app.utils.SosPreference;

public class AudioServiceItem extends ServiceItem {

    protected AudioClipLoudNoiseTask audioTask;

	public AudioServiceItem(Activity activity) {
		super(activity);
		this.switchPref = new AudioPreference(activity);
	}
	
	public void setIconView(ImageView icon) {
		this.iconWidget = icon;
        if(isHardwareSupported(PackageManager.FEATURE_MICROPHONE)) {
            this.iconWidget.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(switchPref.switchState()) {
                        startNewAsyncTask();
                    } else {
                        stopAsyncTask(false);
                    }

                    iconWidget.setImageResource(switchPref.getImage());

                    textWidget.setText(VibWearUtil.getAudioSummarySpanText(switchPref.getLabel()));

                }
            });
        }

		showUserIconSettings();
	}

    public void startNewAsyncTask() {
        if(switchPref.getState()) {
            audioTask = new AudioClipLoudNoiseTask(activity, "AudioClipLoudNoiseTask");
            audioTask.execute(new LoudNoiseDetector());
        }
    }

    public void stopAsyncTask(boolean changeState) {
        if (audioTask != null && audioTask.getStatus() != AsyncTask.Status.FINISHED) {
            audioTask.cancel(true);
            audioTask = null;
            if(changeState)
                switchPref.switchState();
        }

        showUserIconSettings();
        showUserTextSettings();
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
