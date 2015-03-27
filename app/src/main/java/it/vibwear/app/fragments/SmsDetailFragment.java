package it.vibwear.app.fragments;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.utils.SmsPreference;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SmsDetailFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View layout = inflater.inflate(R.layout.fragment_detail, container, false);
		
		Fragment vibSliderFragment = new VibSliderFragment(new SmsPreference(getActivity()));
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.add(R.id.rootDetailLayout, vibSliderFragment).commit();

		return layout;
	}



}
