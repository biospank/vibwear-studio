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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View layout = inflater.inflate(R.layout.fragment_detail, container, false);
		
		AlarmPreference alarmPref = new AlarmPreference(getActivity());
		Fragment alarmFragment = new AlarmFragment(alarmPref);
		Fragment vibFragment = new VibSliderFragment(alarmPref);
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.add(R.id.rootDetailLayout, alarmFragment);
		transaction.add(R.id.rootDetailLayout, vibFragment);
		transaction.commit();

		
		return layout;
	}

}
