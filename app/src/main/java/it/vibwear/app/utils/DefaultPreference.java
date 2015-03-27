package it.vibwear.app.utils;

import android.content.Context;

public class DefaultPreference implements VibrationPreference {

	public DefaultPreference(Context applicationContext) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getVibrationTime() {
		return DEFAULT_VIB_TIME;
	}

	@Override
	public void setVibrationTime(int progress) {
		// TODO Auto-generated method stub

	}

}
