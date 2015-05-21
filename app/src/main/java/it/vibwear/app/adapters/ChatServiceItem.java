package it.vibwear.app.adapters;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.ChatNotificationService;
import it.vibwear.app.VibWearUtil;
import it.vibwear.app.fragments.ChatDetailFragment;
import it.vibwear.app.utils.ChatPreference;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatServiceItem extends ServiceItem {
	public final String IMO_PACKAGE_NAME = "com.imo.android.imoim";
	public final String SETTINGS_PACKAGE_NAME = "com.android.settings";

	public ChatServiceItem(Activity activity) {
		super(activity);
		this.switchPref = new ChatPreference(activity);
	}
	
	public void setIconView(ImageView icon) {
		this.iconWidget = icon;
		this.iconWidget.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				switchPref.switchState();
				
				iconWidget.setImageResource(switchPref.getImage());

				textWidget.setText(VibWearUtil.getChatSummarySpanText(switchPref.getLabel()));

//				Toast.makeText(activity, formattedText, Toast.LENGTH_SHORT).show();
				
			}
		});
		
		showUserIconSettings();
	}
	
	public void setTextView(TextView text) {
		this.textWidget = text;
		this.textWidget.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fm = activity.getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ChatDetailFragment chatFrag = new ChatDetailFragment();
				ft.replace(R.id.servicesLayout, chatFrag, "chatDetail");
				ft.addToBackStack(null);
				ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
				ft.commit();
				
			}
		});

		showUserTextSettings();
	}

	public void showUserTextSettings() {
		if (! ChatNotificationService.isAccessibilitySettingsOn(activity)) {
			textWidget.setText(VibWearUtil.getChatSummarySpanText(activity.getResources().getString(R.string.chatServiceDesc)));
			if(switchPref.getState()) { switchPref.switchState(); }
		} else {
			textWidget.setText(VibWearUtil.getChatSummarySpanText(switchPref.getLabel()));
		}
	}
	
	@Override
	public boolean consume(Intent intent) {
		Bundle extraInfo = intent.getExtras();
		
		String sourcePackageName = extraInfo.getString("sourcePackageName");
		
		if(sourcePackageName.equals(IMO_PACKAGE_NAME)) {
			return switchPref.getState();
		} else {
			if(sourcePackageName.equals(SETTINGS_PACKAGE_NAME)) {
				return false;
			} else {
				return super.consume(intent);
			}
		}
		
	}
	
}
