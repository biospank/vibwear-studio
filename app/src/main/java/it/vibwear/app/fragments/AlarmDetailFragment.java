package it.vibwear.app.fragments;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.utils.AlarmPreference;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AlarmDetailFragment extends Fragment {
	private Fragment vibSliderFragment;
	private Fragment alarmFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View layout = inflater.inflate(R.layout.fragment_detail, container, false);
		
		if(savedInstanceState != null) {
			vibSliderFragment = getChildFragmentManager().getFragment(savedInstanceState, "VibFrag");
			alarmFragment = getChildFragmentManager().getFragment(savedInstanceState, "AlarmFrag");
		} else {
			alarmFragment = new AlarmFragment();
            vibSliderFragment = new VibSliderFragment();
            Bundle preference = new Bundle();
            preference.putString("preference", "AlarmPreference");
            vibSliderFragment.setArguments(preference);
			FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
			transaction.add(R.id.rootDetailLayout, alarmFragment);
			transaction.add(R.id.rootDetailLayout, vibSliderFragment);
			transaction.commit();
		}
		
		return layout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		getChildFragmentManager().putFragment(outState, "VibFrag", vibSliderFragment);
		getChildFragmentManager().putFragment(outState, "AlarmFrag", alarmFragment);
	}

}
