package it.vibwear.app.adapters;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.VibWearUtil;
import it.vibwear.app.fragments.CallDetailFragment;
import it.vibwear.app.utils.CallPreference;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CallServiceItem extends ServiceItem {
	
	public CallServiceItem(Activity activity) {
		super(activity);
		this.switchPref = new CallPreference(activity);
	}

	
	public void setIconView(ImageView icon) {
		this.iconWidget = icon;
		
		if(isHardwareSupported(PackageManager.FEATURE_TELEPHONY)) {
			this.iconWidget.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					switchPref.switchState();
					
					iconWidget.setImageResource(switchPref.getImage());
	
					textWidget.setText(VibWearUtil.getCallSummarySpanText(switchPref.getLabel()));
	
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
					CallDetailFragment callFrag = new CallDetailFragment();
					ft.replace(R.id.servicesLayout, callFrag, "callDetail");
					ft.addToBackStack(null);
					ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
					ft.commit();
					
				}
			});
		}
		
		showUserTextSettings();
	}


	public void showUserTextSettings() {
        
		textWidget.setText(VibWearUtil.getCallSummarySpanText(switchPref.getLabel()));
	
	}
	
}
