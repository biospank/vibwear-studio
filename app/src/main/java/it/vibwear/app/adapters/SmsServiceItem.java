package it.vibwear.app.adapters;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.utils.VibWearUtil;
import it.vibwear.app.fragments.SmsDetailFragment;
import it.vibwear.app.utils.SmsPreference;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class SmsServiceItem extends ServiceItem {
	
	public SmsServiceItem(Activity activity) {
		super(activity);
		this.switchPref = new SmsPreference(activity);
	}
	
	public void setIconView(ImageView icon) {
		this.iconWidget = icon;

		if(isHardwareSupported(PackageManager.FEATURE_TELEPHONY)) {
			this.iconWidget.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					switchPref.switchState();
					
					iconWidget.setImageResource(switchPref.getImage());

                    setLocalizedText();
	
				}
				
			});
		}

		showUserIconSettings();
	}
	
	public void setTextView(TextView text) {
		this.textWidget = text;

		if(isHardwareSupported(PackageManager.FEATURE_TELEPHONY)) {
			this.textWidget.setOnClickListener(new View.OnClickListener() {
			
				@Override
				public void onClick(View v) {
					FragmentManager fm = activity.getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					SmsDetailFragment smsFrag = new SmsDetailFragment();
					ft.replace(R.id.servicesLayout, smsFrag, "smsDetail");
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

    private void setLocalizedText() {
        String lang = Locale.getDefault().getLanguage();

        if(lang == "en")
            textWidget.setText(VibWearUtil.getSmsSummarySpanText(switchPref.getLabel()));
        else
            textWidget.setText(switchPref.getLabel());
    }

}
