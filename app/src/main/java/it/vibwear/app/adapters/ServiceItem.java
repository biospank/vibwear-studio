package it.vibwear.app.adapters;

import it.vibwear.app.utils.SwitchPreference;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class ServiceItem {
	protected Activity activity;
	protected SwitchPreference switchPref; 
	protected ImageView iconWidget;
	protected TextView textWidget;

	
	public ServiceItem(Activity activity) {
		super();
		this.activity = activity;
	}

	public void setTextView(TextView text) {
		
	}

	public void setIconView(ImageView icon) {
		
	}

	public boolean consume(Intent intent) {
		Bundle extraInfo = intent.getExtras();
		
		Boolean isStandBy = extraInfo.getBoolean("standBy");

		return isStandBy && switchPref.getState();
	}
	
	public void showUserIconSettings() {
		
		iconWidget.setImageResource(switchPref.getImage());
        
	}
	
	public void update() {}

	public void refresh() {}
	
	public boolean isHardwareSupported(String feature) {
		if (activity.getPackageManager().hasSystemFeature(feature)) {
			return true;
		} else {
			return false;
		}
	}

}
