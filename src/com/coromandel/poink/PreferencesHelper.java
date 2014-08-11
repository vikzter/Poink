package com.coromandel.poink;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

public class PreferencesHelper {

	public static double getTiltAngleSetting(Context c)
	{
		SharedPreferences sharedPref = c.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
		Resources res = c.getResources();
		double returnVal = sharedPref.getFloat(Constants.TILT_ANGLE_SETTING, res.getInteger(R.integer.tiltAngleDefault));
		return returnVal;
	}
	
	public static void setTiltAngleSetting(Context c, double value)
	{
		SharedPreferences sharedPref = c.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putFloat(Constants.TILT_ANGLE_SETTING, (float) value);
		editor.commit();
	}
	
	public static long getVibrateForSetting(Context c)
	{
		SharedPreferences sharedPref = c.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
		Resources res = c.getResources();
		long returnVal = sharedPref.getLong(Constants.VIBRATE_FOR_SETTING, res.getInteger(R.integer.vibrateSecondsDefault));
		return returnVal;
	}
	
	public static void setVibrateForSetting(Context c, long value)
	{
		SharedPreferences sharedPref = c.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putLong(Constants.VIBRATE_FOR_SETTING, value);
		editor.commit();
	}
	
}
