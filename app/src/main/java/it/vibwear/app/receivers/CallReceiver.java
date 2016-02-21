package it.vibwear.app.receivers;

import it.vibwear.app.fragments.ServicesFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallReceiver extends BroadcastReceiver {

//	@Override
//	public void onReceive(Context context, Intent intent) {
//		Intent broadcastIntent = new Intent();
//		broadcastIntent.setAction(ServicesFragment.CALL_VIB_ACTION);
//		context.sendBroadcast(broadcastIntent);
//	}

	private Context mContext;
    private Intent mIntent;
 
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int events = PhoneStateListener.LISTEN_CALL_STATE;
        tm.listen(phoneStateListener, events);
    }
 
    private final PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            String callState;
            switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                callState = "IDLE";
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                callState = "RINGING";
        		Intent broadcastIntent = new Intent();
        		broadcastIntent.setAction(ServicesFragment.CALL_VIB_ACTION);
        		mContext.sendBroadcast(broadcastIntent);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                callState = "OFFHOOK";
                break;
            default:
            	callState = "UNKNOWN";
            	break;
                	
            }
//            Log.i(">>>Broadcast", "onCallStateChanged " + callState);
        }
    };
}
