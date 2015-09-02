package it.vibwear.app.fragments;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.adapters.NotificationListAdapter;
import it.vibwear.app.utils.ChatPreference;
import it.vibwear.app.utils.NotificationPreference;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChatDetailFragment extends Fragment {
	private Fragment vibSliderFragment;
    private Fragment notificationListFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View layout = inflater.inflate(R.layout.fragment_detail, container, false);
		
		if(savedInstanceState != null) {
            FragmentManager fm = getChildFragmentManager();
			vibSliderFragment = fm.getFragment(savedInstanceState, "VibFrag");
            notificationListFragment = fm.getFragment(savedInstanceState, "NotificationFrag");
		} else {
            vibSliderFragment = new VibSliderFragment();
            notificationListFragment = new NotificationListFragment();
            Bundle preference = new Bundle();
            preference.putString("preference", "ChatPreference");
            vibSliderFragment.setArguments(preference);
			FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
			transaction.add(R.id.rootDetailLayout, vibSliderFragment)
                .add(R.id.rootDetailLayout, notificationListFragment).commit();
		}
		
		return layout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		FragmentManager fm = getChildFragmentManager();

        fm.putFragment(outState, "VibFrag", vibSliderFragment);
        fm.putFragment(outState, "NotificationFrag", notificationListFragment);
	}

}
