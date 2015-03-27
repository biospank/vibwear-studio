package it.vibwear.app;

import it.vibwear.app.fragments.ServicesFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
//		String sms = "Message received!!";
//		Toast.makeText(context, sms, Toast.LENGTH_SHORT).show();
		
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(ServicesFragment.SMS_VIB_ACTION);
//		broadcastIntent.putExtra("sms", sms);
		context.sendBroadcast(broadcastIntent);
		
	}
}