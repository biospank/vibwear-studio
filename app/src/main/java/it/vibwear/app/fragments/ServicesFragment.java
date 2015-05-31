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

    public AudioServiceItem getAudioService() {
        if(audioService != null)
            return audioService;
        else
            return new AudioServiceItem(getActivity());
    }

    public CallServiceItem getCallService() {
        if(callService != null)
            return callService;
        else
            return new CallServiceItem(getActivity());

    }

    public SmsServiceItem getSmsService() {
        if(smsService != null)
            return smsService;
        else
            return new SmsServiceItem(getActivity());
    }

    public ChatServiceItem getChatService() {
        if(chatService != null)
            return chatService;
        else
            return new ChatServiceItem(getActivity());
    }

    public AlarmServiceItem getAlarmService() {
        if(alarmService != null)
            return alarmService;
        else
            return new AlarmServiceItem(getActivity());
    }

    public SosServiceItem getSosService() {
        if(sosService != null)
            return sosService;
        else
            return new SosServiceItem(getActivity());
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ArrayList<ServiceItem> services = new ArrayList<ServiceItem>();
		Activity activity = getActivity();
		
		callService = getCallService();
		smsService = getSmsService();
		chatService = getChatService();
		sosService = getSosService();
		alarmService = getAlarmService();
        audioService = getAudioService();

		services.add(callService);
		services.add(smsService);
		services.add(chatService);
		services.add(sosService);
		services.add(alarmService);
        services.add(audioService);

		ServicesAdapter servicesAdapter = new ServicesAdapter(getActivity(), services);
		setListAdapter(servicesAdapter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if(getActivity().isFinishing())
			audioService.turnOffAudio();
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
			result = getCallService().consume(intent);
			break;

		case SMS_VIB_ACTION:
			result = getSmsService().consume(intent);
			break;
				
		case CHAT_VIB_ACTION:
			result = getChatService().consume(intent);
			break;
				
		case ALARM_VIB_ACTION:
			result = getAlarmService().consume(intent);
			break;

        case SOS_VIB_ACTION:
            result = getSosService().consume(intent);
            break;

        case AUDIO_VIB_ACTION:
            result = getAudioService().consume(intent);
            break;

        }
		
		return result;
	}

	public void update(Intent intent) {
		switch (intent.getAction()) {
		case CALL_VIB_ACTION:
            getCallService().update();
			break;

		case SMS_VIB_ACTION:
            getSmsService().update();
			break;
				
		case CHAT_VIB_ACTION:
            getChatService().update();
			break;
				
		case ALARM_VIB_ACTION:
			getAlarmService().update();
			break;

		case SOS_VIB_ACTION:
            getAlarmService().update();
			break;

        case AUDIO_VIB_ACTION:
            getAudioService().update();
            break;

		}
		
	}

	public void refresh() {
        getCallService().refresh();
        getSmsService().refresh();
        getChatService().refresh();
        getAlarmService().refresh();
		getSosService().refresh();
        getAudioService().refresh();
	}

}
