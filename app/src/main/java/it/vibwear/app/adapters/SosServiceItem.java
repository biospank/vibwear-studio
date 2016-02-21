package it.vibwear.app.adapters;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.utils.VibWearUtil;
import it.vibwear.app.fragments.SosDetailFragment;
import it.vibwear.app.utils.SosPreference;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class SosServiceItem extends ServiceItem {

	public SosServiceItem(Activity activity) {
		super(activity);
		this.switchPref = new SosPreference(activity);
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
                    SosDetailFragment sosFrag = new SosDetailFragment();
                    ft.replace(R.id.servicesLayout, sosFrag, "sosDetail");
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
            textWidget.setText(VibWearUtil.getSosSummarySpanText(switchPref.getLabel()));
        else
            textWidget.setText(switchPref.getLabel());
    }

}
