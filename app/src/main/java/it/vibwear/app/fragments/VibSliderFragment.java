package it.vibwear.app.fragments;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.utils.VibrationPreference;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VibSliderFragment extends Fragment {

	protected VibrationPreference vibPreference;
	protected SeekBar sbVib;
	
	public VibSliderFragment(VibrationPreference vibPreference) {
		this.vibPreference = vibPreference;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.fragment_vib_slider, container, false);
		
		sbVib = (SeekBar)layout.findViewById(R.id.sbVib);
		
		sbVib.setProgress(vibPreference.getVibrationTime());
		
		sbVib.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
				vibPreference.setVibrationTime(progress);
			}
		});
		
		return layout;
	}

}
