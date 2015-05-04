package it.vibwear.app.fragments;

import java.util.ArrayList;

import it.vibwear.app.adapters.AlarmServiceItem;
import it.vibwear.app.adapters.AudioServiceItem;
import it.vibwear.app.adapters.CallServiceItem;
import it.vibwear.app.adapters.ChatServiceItem;
import it.vibwear.app.adapters.ServiceItem;
import it.vibwear.app.adapters.ServicesAdapter;
import it.vibwear.app.adapters.SmsServiceItem;
import it.vibwear.app.adapters.SosServiceItem;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;

public class ServicesFragment extends ListFragment {

	public static final String CALL_VIB_ACTION = "CALL_VIB_ACTION";
	public static final String SMS_VIB_ACTION = "SMS_VIB_ACTION";
	public static final String CHAT_VIB_ACTION = "CHAT_VIB_ACTION";
	public static final String ALARM_VIB_ACTION = "ALARM_VIB_ACTION";
    public static final String SOS_VIB_ACTION = "SOS_VIB_ACTION";
    public static final String AUDIO_VIB_ACTION = "AUDIO_VIB_ACTION";

	protected CallServiceItem callService;
	protected SmsServiceItem smsService;
	protected ChatServiceItem chatService;
	protected AlarmServiceItem alarmService;
	protected SosServiceItem sosService;
    protected AudioServiceItem audioService;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ArrayList<ServiceItem> services = new ArrayList<ServiceItem>();
		Activity activity = getActivity();
		
		callService = new CallServiceItem(activity);
		smsService = new SmsServiceItem(activity);
		chatService = new ChatServiceItem(activity);
		sosService = new SosServiceItem(activity);
		alarmService = new AlarmServiceItem(activity);
        audioService = new AudioServiceItem(activity);

		services.add(callService);
		services.add(smsService);
		services.add(chatService);
		services.add(sosService);
		services.add(alarmService);
        services.add(audioService);

		ServicesAdapter servicesAdapter = new ServicesAdapter(getActivity(), services);
		setListAdapter(servicesAdapter);
	}
	
//	@Override
//	public void onListItemClick(ListView l, View v, int position, long id) {
//		// TODO Auto-generated method stub
//		super.onListItemClick(l, v, position, id);
//		Log.d("onListItemClick", "Item position " + position);
//		Log.d("onListItemClick", "Item id " + id);
//		Log.d("onListItemClick", "Item view " + v);
//	}

	public boolean consumeIntent(Intent intent) {
		boolean result = false;
		
		switch (intent.getAction()) {
		case CALL_VIB_ACTION:
			result = callService.consume(intent);
			break;

		case SMS_VIB_ACTION:
			result = smsService.consume(intent);
			break;
				
		case CHAT_VIB_ACTION:
			result = chatService.consume(intent);
			break;
				
		case ALARM_VIB_ACTION:
			result = alarmService.consume(intent);
			break;

        case SOS_VIB_ACTION:
            result = sosService.consume(intent);
            break;

        case AUDIO_VIB_ACTION:
            result = audioService.consume(intent);
            break;

        }
		
		return result;
	}

	public void update(Intent intent) {
		switch (intent.getAction()) {
		case CALL_VIB_ACTION:
			callService.update();
			break;

		case SMS_VIB_ACTION:
			smsService.update();
			break;
				
		case CHAT_VIB_ACTION:
			chatService.update();
			break;
				
		case ALARM_VIB_ACTION:
			alarmService.update();
			break;

		case SOS_VIB_ACTION:
			sosService.update();
			break;

        case AUDIO_VIB_ACTION:
            audioService.update();
            break;

		}
		
	}

	public void refresh() {
		callService.refresh();
		smsService.refresh();
		alarmService.refresh();
		sosService.refresh();
        audioService.refresh();
	}

}
