package it.vibwear.app.utils;

public interface VibrationPreference {
    public int DEFAULT_VIB_TIME = 1;

	public int getVibrationTime();
	public void setVibrationTime(int progress);
}
