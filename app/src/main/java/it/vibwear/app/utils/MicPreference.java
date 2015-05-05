package it.vibwear.app.utils;

public interface MicPreference {
	public int DEFAULT_TRESHOLD = 1000;

	public int getTreshold();
	public void setTreshold(int timeAlarm);
}
