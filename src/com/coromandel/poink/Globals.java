package com.coromandel.poink;



import android.content.Context;
import android.os.PowerManager.WakeLock;

public class Globals {
	
	public static WakeLock wakelock;

	private static Double tiltAngle;
	private static Long vibrateFor;
	
	public static double getTiltAngle(Context c)
	{
		if (tiltAngle==null)
		{
			tiltAngle = PreferencesHelper.getTiltAngleSetting(c);
		}
		
		return tiltAngle;
	}
	
	public static void setTiltAngle(Context c,double tiltAngleVal)
	{
		tiltAngle = tiltAngleVal;
		PreferencesHelper.setTiltAngleSetting(c,tiltAngleVal);
	}
	
	public static long getVibrateFor(Context c)
	{
		if (vibrateFor==null)
		{
			vibrateFor = PreferencesHelper.getVibrateForSetting(c);
		}
		
		return vibrateFor;
	}
	
	public static void setVibrateFor(Context c,long vibrateForVal)
	{
		vibrateFor = vibrateForVal;
		PreferencesHelper.setVibrateForSetting(c,vibrateForVal);
	}
}
