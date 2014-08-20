package com.coromandel.poink;

import java.util.Date;
import java.util.concurrent.TimeUnit;





import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class MainFragment extends Fragment implements SensorEventListener,IFragmentWithClickEvents {

	@Override
	public void handleButtonClick(View v) {
		switch(v.getId())
		{
		case R.id.tv_outercircle:
			this.mainClicked(v);
			break;
		
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.main_fragment, container, false);
	}

	enum AppStates {
		On, Off, TransitingToOn, TransitingToOff,Paused
	}

	private enum AlertStates {
		Maximum, Off
	};

	private Activity parentActivity;
	private SensorManager mSensorManager;
	private Sensor mGSensor;
	private Sensor mASensor;
	public static float[] mAccelerometer = null;
	public static float[] mGeomagnetic = null;

	AppStates isOn = AppStates.Off;
	AlertStates currentAlertState = AlertStates.Off;
	Date alertStateSince = null;
	

	ImageView middle;
	ImageView main;
	TextView outer;
	TextView timer;
	TextView textDisplay;
	ImageView hidden;

	Animation end_to_middle_anim;
	Animation middle_to_origin_anim;
	Animation middle_to_end_anim;
	Animation origin_to_middle_anim;
	Animation origin_to_middle_anim2;
	Animation breatheAnimation;

	// StopWatch Variables
	private Handler mHandler = new Handler();
	private long startTime;
	private long elapsedTime;
	private final int REFRESH_RATE = 100;
	private String hours, minutes, seconds;
	private long secs, mins, hrs;
	private boolean stopped = false;

	private double lastPitch;
	private double pitchAtInterval1;
	private double pitchAtInterval2;
	private double pitchAtInterval3;
	private Handler pitchRecordHandler = new Handler();

	private final int PITCH_RECORD_INTERVAL = 300;
	private final double MINIMUM_PITCH_WHEN_OFF = -50;
	private final double MAXIMUM_PITCH_WHEN_OFF = -90;

	
	//private final double MIDDLE_PITCH_WHEN_ON = -60;
	private final double MAXIMUM_PITCH_WHEN_ON = -90;

	private Vibrator vibrator;
	
	private long[] vibratePattern = { 0, 600, 100, 300, 100, 600, 200, 600, 100 };
	private boolean isVibratorOn = false;
	
	private Ringtone ringtone;
	private AudioManager audioManager;
	private int originalAlarmVolume;
	
	NotificationManager mNotificationManager;
	Notification notif;
	PowerManager powerManager;
	private final int SCREEN_OFF_RECEIVER_DELAY = 800;
	
	
	
	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		

		parentActivity = getActivity();
		
		// for pre api 11 devices check vibrator exists like this
		if (parentActivity.getSystemService(Context.VIBRATOR_SERVICE) != null) {
			vibrator = (Vibrator) parentActivity.getSystemService(Context.VIBRATOR_SERVICE);
			// For post api 11 check for vibrator like this
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
					&& !vibrator.hasVibrator()) {
				Globals.setVibrateFor(parentActivity, 0);//set vibrate to 0 seconds
			}
		} else {
			Globals.setVibrateFor(parentActivity, 0);//set vibrate to 0 seconds
		}

		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		
		
		ringtone = RingtoneManager.getRingtone(parentActivity.getApplicationContext(),notification);
		audioManager = (AudioManager)parentActivity.getSystemService(Context.AUDIO_SERVICE);
		originalAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
		
		middle = (ImageView) parentActivity.findViewById(R.id.imgv_middlecircle);
		outer = (TextView) parentActivity.findViewById(R.id.tv_outercircle);
		main = (ImageView) parentActivity.findViewById(R.id.imgv_maincircle);
		hidden = (ImageView) parentActivity.findViewById(R.id.imgv_hiddencircle);
		timer = (TextView) parentActivity.findViewById(R.id.tv_timedisplay);
		textDisplay = (TextView) parentActivity.findViewById(R.id.tv_textdisplay);

		Typeface tf = Typeface.createFromAsset(parentActivity.getAssets(),
				"fonts/AlteHaasGroteskRegular.ttf");
		timer.setTypeface(tf, Typeface.NORMAL);
		textDisplay.setTypeface(tf, Typeface.NORMAL);

		
		
		middle.setVisibility(View.INVISIBLE);
		main.setVisibility(View.INVISIBLE);
		hidden.setVisibility(View.INVISIBLE);

		mSensorManager = (SensorManager) parentActivity.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
			mGSensor = mSensorManager
					.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		}
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
			mASensor = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}

		middle_to_end_anim = AnimationUtils.loadAnimation(parentActivity,
				R.anim.middle_to_end);
		end_to_middle_anim = AnimationUtils.loadAnimation(parentActivity,
				R.anim.end_to_middle);
		middle_to_origin_anim = AnimationUtils.loadAnimation(parentActivity,
				R.anim.middle_to_origin);
		origin_to_middle_anim = AnimationUtils.loadAnimation(parentActivity,
				R.anim.origin_to_middle);
		origin_to_middle_anim2 = AnimationUtils.loadAnimation(parentActivity,
				R.anim.origin_to_middle);
		breatheAnimation = AnimationUtils.loadAnimation(parentActivity, R.anim.breathing);
		this.prepareAnimations();

		firstAnimation();
		pitchRecordHandler.postDelayed(pitchIntervalTimer, 0);
		
		RegisterSensorListeners();
		
	
		mNotificationManager = (NotificationManager) parentActivity.getSystemService(Context.NOTIFICATION_SERVICE);
		Context context = parentActivity.getApplicationContext();
		NotificationCompat.Builder builder = new NotificationCompat.Builder(parentActivity)
		.setSmallIcon(R.drawable.ic_notif);
		

		Intent intent = new Intent(context, BaseFragmentActivity.class);
		intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
       /* intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);*/

		PendingIntent pIntent = PendingIntent.getActivity(context, 7 , intent, Notification.FLAG_ONGOING_EVENT);
		builder.setContentIntent(pIntent);
		builder.setOngoing(true);
		builder.setContentTitle(parentActivity.getString(R.string.poinkisrunning));
		builder.setContentText(parentActivity.getString(R.string.keepupright));
		notif = builder.build();

		
		//// Telephony Detection
		 TelephonyManager TelephonyMgr = (TelephonyManager) parentActivity.getSystemService(Context.TELEPHONY_SERVICE);
		 TelephonyMgr.listen(mPhoneListener,PhoneStateListener.LISTEN_CALL_STATE);
		 
		 powerManager = (PowerManager) parentActivity.getSystemService(Context.POWER_SERVICE);
		 
		 parentActivity.registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		 
		 //TESTING-REmove later
		 lastPitch = -89;
		 
		 
		//TODO:REMOVE THIS
			//timer.setVisibility(View.INVISIBLE);
			//textDisplay.setVisibility(View.INVISIBLE);
			
	}


	private void fireAnOngoingNotitification()
	{
		ServiceConnection mConnection = new ServiceConnection() {
		    public void onServiceConnected(ComponentName className,android.os.IBinder binder) {
		        ((PoinkNotificationManager.KillBinder) binder).service.startService(new Intent(
		                parentActivity, PoinkNotificationManager.class));
		       
		        mNotificationManager.notify(PoinkNotificationManager.NOTIFICATION_ID,notif);
		    }

		    public void onServiceDisconnected(ComponentName className) {
		    }

		};
		parentActivity.bindService(new Intent(parentActivity,PoinkNotificationManager.class), mConnection,Context.BIND_AUTO_CREATE);
		
	}
	
	private void firstAnimation() {
		Animation firstAnimation = AnimationUtils.loadAnimation(parentActivity,
				R.anim.first_position);
		firstAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				outer.clearAnimation();
				View parent = (View) outer.getParent();
				int height = parent.getHeight();
				int moveBy = height * 20 / 100;

				int outerheight = outer.getHeight();
				int outerwidth = outer.getWidth();
				int outertop = outer.getTop();
				LayoutParams params = new LayoutParams(outerwidth, outerheight);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL, -1);
				params.setMargins(0, outertop - moveBy, 0, 0);
				outer.setLayoutParams(params);

				int mainheight = main.getHeight();
				int mainwidth = main.getWidth();
				int maintop = main.getTop();
				LayoutParams params1 = new LayoutParams(mainwidth, mainheight);
				params1.addRule(RelativeLayout.CENTER_HORIZONTAL, -1);

				params1.setMargins(0, maintop - moveBy, 0, 0);

				middle.setLayoutParams(params1);
				main.setLayoutParams(params1);
				hidden.setLayoutParams(params1);
				hidden.setVisibility(View.VISIBLE);
				main.setVisibility(View.VISIBLE);

			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});

		outer.startAnimation(firstAnimation);
	}

	
	@SuppressLint("NewApi")
	public void mainClicked(View v) {

		if (isOn == AppStates.On) {
			isOn = AppStates.TransitingToOff;
			this.StopTimer();
			this.closeAnimationForPreApi11();

		} else {
			isOn = AppStates.TransitingToOn;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// main.setOnDragListener(new myDragEventListener());
			}

			this.ResetTimer();
			this.StartTimer();
			main.startAnimation(origin_to_middle_anim);
			middle.startAnimation(origin_to_middle_anim2);

		}

	}

	private void blinkAnimations(boolean bothCircles) {
		final Animation blinkAnimation = AnimationUtils.loadAnimation(parentActivity,
				R.anim.blinking);
		final Animation blinkAnimation2 = AnimationUtils.loadAnimation(parentActivity,
				R.anim.blinking);
		middle.startAnimation(blinkAnimation);

		if (bothCircles) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					main.startAnimation(blinkAnimation2);
				}
			}, 150);
		}

	}

	private void prepareAnimations() {

		// ///////////// OPEN Animations for All versions

		middle_to_end_anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				//main.clearAnimation();
				View parent = (View) main.getParent();
				int height = parent.getHeight();
				int moveBy = height * 40 / 100;

				int mainheight = main.getHeight();
				int mainwidth = main.getWidth();
				int maintop = main.getTop();
				LayoutParams params = new LayoutParams(mainwidth, mainheight);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL, -1);

				params.setMargins(0, maintop + moveBy, 0, 0);
				main.setLayoutParams(params);
				hidden.startAnimation(breatheAnimation);
				isOn = AppStates.On;
				Toast.makeText(parentActivity, getText(R.string.poinkactivated), Toast.LENGTH_SHORT).show();
				fireAnOngoingNotitification();
				if(Globals.wakelock ==null)
				{
					Globals.wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"Poink");
				}
				if(!Globals.wakelock.isHeld())
				{
					Globals.wakelock.acquire();
				}
				textDisplay.setText(R.string.itsallgood);

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		origin_to_middle_anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {

				middle.clearAnimation();
				View parent = (View) middle.getParent();
				int height = parent.getHeight();
				int moveBy = height * 20 / 100;

				int mainheight = middle.getHeight();
				int mainwidth = middle.getWidth();
				int maintop = middle.getTop();
				LayoutParams params = new LayoutParams(mainwidth, mainheight);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL, -1);

				params.setMargins(0, maintop + moveBy, 0, 0);
				middle.setLayoutParams(params);
				middle.setVisibility(View.VISIBLE);

				main.startAnimation(middle_to_end_anim);
			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		// ///////////// CLOSE ANIMATIONS

		end_to_middle_anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				middle.clearAnimation();
				middle.setVisibility(View.INVISIBLE);
				main.startAnimation(middle_to_origin_anim);
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});

		middle_to_origin_anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				//main.clearAnimation();
				View parent = (View) main.getParent();
				int height = parent.getHeight();
				int moveBy = height * 40 / 100;

				int mainheight = main.getHeight();
				int mainwidth = main.getWidth();
				int maintop = main.getTop();
				LayoutParams params = new LayoutParams(mainwidth, mainheight);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL, -1);

				params.setMargins(0, maintop - moveBy, 0, 0);
				main.setLayoutParams(params);

				// now move the middle circle, that is invisible now, back to
				// origin - since the remaining params are the same just change
				// needed ones
				// and apply
				int middletop = middle.getTop();
				int middlemoveBy = height * 20 / 100;
				params.setMargins(0, middletop - middlemoveBy, 0, 0);
				middle.setLayoutParams(params);
				hidden.clearAnimation();
				isOn = AppStates.Off;
				Toast.makeText(parentActivity, getText(R.string.poinkstopped), Toast.LENGTH_SHORT).show();
				mNotificationManager.cancelAll();
				
				if(Globals.wakelock !=null && Globals.wakelock.isHeld())
				{
					Globals.wakelock.release();
					Globals.wakelock = null;
				}

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}

	private void closeAnimationForPreApi11() {
		main.startAnimation(end_to_middle_anim);
	}

	
	public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            

            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }
             
            Runnable runnable = new Runnable() {
                public void run() {
                    UnregisterSensorListeners();
                    RegisterSensorListeners();
                	if (isVibratorOn)// HACKY to get vibrator to switch back on, if screen had turned off while phone was vibrating which causes vibrator to turn off
        				StartVibrate();
                }
            };

            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };
	
	
	
	// StopWatch Functions ********************************
	private Runnable runningTimer = new Runnable() {
		public void run() {
			elapsedTime = System.currentTimeMillis() - startTime;
			updateTimer(elapsedTime);
			mHandler.postDelayed(this, REFRESH_RATE);
		}
	};

	private void StartTimer() {

		if (stopped) {
			startTime = System.currentTimeMillis() - elapsedTime;
		} else {
			startTime = System.currentTimeMillis();
		}

		mHandler.removeCallbacks(runningTimer);
		mHandler.postDelayed(runningTimer, 0);

	}

	private void StopTimer() {
		mHandler.removeCallbacks(runningTimer);
		stopped = true;
	}

	private void ResetTimer() {
		stopped = false;
		timer.setText("00:00:00");
	}

	private void updateTimer(float time) {
		secs = (long) (time / 1000);
		mins = (long) ((time / 1000) / 60);
		hrs = (long) (((time / 1000) / 60) / 60); /*
												 * Convert the seconds to String
												 * * and format to ensure it has
												 * * a leading zero when
												 * required
												 */
		secs = secs % 60;
		seconds = String.valueOf(secs);
		if (secs == 0) {
			seconds = "00";
		}

		if (secs < 10 && secs > 0) {
			seconds = "0" + seconds;
		}
		/* Convert the minutes to String and format the String */
		mins = mins % 60;
		minutes = String.valueOf(mins);
		if (mins == 0) {
			minutes = "00";
		}

		if (mins < 10 && mins > 0) {
			minutes = "0" + minutes;
		}

		/* Convert the hours to String and format the String */
		hours = String.valueOf(hrs);
		if (hrs == 0) {
			hours = "00";
		}

		if (hrs < 10 && hrs > 0) {
			hours = "0" + hours;
		}

		/* Although we are not using milliseconds on the timer in this example */

		/*
		 * milliseconds = String.valueOf((long)time);
		 * 
		 * if(milliseconds.length()==2) { milliseconds = "0"+milliseconds; }
		 * 
		 * if(milliseconds.length()<=1) { milliseconds = "00"; }
		 * 
		 * milliseconds = milliseconds.substring(milliseconds.length()-3,
		 * milliseconds.length()-2);
		 */

		/* Setting the timer text to the elapsed time */
		timer.setText(hours + ":" + minutes + ":" + seconds);
	}

	// *********** SEnsor functions

	// StopWatch Functions ********************************
	private Runnable pitchIntervalTimer = new Runnable() {
		public void run() {

			pitchAtInterval1 = pitchAtInterval2;
			pitchAtInterval2 = pitchAtInterval3;
			pitchAtInterval3 = lastPitch;
			double MINIMUM_PITCH_WHEN_ON = -90 + Globals.getTiltAngle(parentActivity);
			if (isOn == AppStates.Off) {
				if ((pitchAtInterval1 <= MINIMUM_PITCH_WHEN_OFF && pitchAtInterval1 >= MAXIMUM_PITCH_WHEN_OFF)
						&& (pitchAtInterval2 <= MINIMUM_PITCH_WHEN_OFF && pitchAtInterval2 >= MAXIMUM_PITCH_WHEN_OFF)
						&& (pitchAtInterval3 <= MINIMUM_PITCH_WHEN_OFF && pitchAtInterval3 >= MAXIMUM_PITCH_WHEN_OFF)) {
					EnableClick();
				} else if (pitchAtInterval1 > MINIMUM_PITCH_WHEN_OFF
						&& pitchAtInterval2 > MINIMUM_PITCH_WHEN_OFF
						&& pitchAtInterval3 > MINIMUM_PITCH_WHEN_OFF) {
					DisableClick();
				}
			} else if (isOn == AppStates.On) {
				if (pitchAtInterval1 > MINIMUM_PITCH_WHEN_ON
						&& pitchAtInterval2 > MINIMUM_PITCH_WHEN_ON
						&& pitchAtInterval3 > MINIMUM_PITCH_WHEN_ON) {
					MaximumAlertUser();
				} else if ((pitchAtInterval1 <= MINIMUM_PITCH_WHEN_ON && pitchAtInterval1 >= MAXIMUM_PITCH_WHEN_ON)
						&& (pitchAtInterval2 <= MINIMUM_PITCH_WHEN_ON && pitchAtInterval2 >= MAXIMUM_PITCH_WHEN_ON)
						&& (pitchAtInterval3 <= MINIMUM_PITCH_WHEN_ON && pitchAtInterval3 >= MAXIMUM_PITCH_WHEN_ON)) {
					NoAlertsUser();
				}

			}

			pitchRecordHandler.postDelayed(this, PITCH_RECORD_INTERVAL);
		}
	};

	private void EnableClick() {
		outer.setEnabled(true);
		textDisplay.setText(R.string.clicktostart);

	}

	private void DisableClick() {
		outer.setEnabled(false);
		textDisplay.setText(R.string.holdupright);

	}

	private void MaximumAlertUser() {
		if (currentAlertState == AlertStates.Maximum) {
			if (alertStateSince != null) {

				long timediff = new Date().getTime()
						- alertStateSince.getTime();
				long timediffseconds = TimeUnit.MILLISECONDS
						.toSeconds(timediff);

				if (timediffseconds > Globals.getVibrateFor(parentActivity)) {
					// Start ringing
					if (!ringtone.isPlaying())
					{
						originalAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
						audioManager.setStreamVolume (AudioManager.STREAM_ALARM,1,0);
						ringtone.setStreamType(AudioManager.STREAM_ALARM);
						ringtone.play();
					}else
					{
						int currentVolume =audioManager.getStreamVolume(AudioManager.STREAM_ALARM); 
						if(currentVolume <audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM))
						audioManager.setStreamVolume (AudioManager.STREAM_ALARM,currentVolume+1,0);
					}
				}
			}

		} else {
			outer.setEnabled(false);
			this.blinkAnimations(true);
			textDisplay.setText(R.string.wakeup);
			alertStateSince = new Date();
			currentAlertState = AlertStates.Maximum;
			if (!isVibratorOn)
				StartVibrate();

		}

	}

/*	private void MediumAlertUser() {
		if (currentAlertState != AlertStates.Medium) {
			alertStateSince = null;
			main.clearAnimation();
			outer.setEnabled(false);
			this.blinkAnimations(false);
			textDisplay.setText(R.string.tilting);
			currentAlertState = AlertStates.Medium;
			if (isVibratorOn)
				StopVibrate();
			
			
		}
	}
*/
	private void NoAlertsUser() {
		if (currentAlertState != AlertStates.Off) {
			alertStateSince = null;
			outer.setEnabled(true);
			main.clearAnimation();
			middle.clearAnimation();
			textDisplay.setText(R.string.itsallgood);
			currentAlertState = AlertStates.Off;
			if (isVibratorOn)
				StopVibrate();
			if (ringtone.isPlaying())
				ringtone.stop();
			
			audioManager.setStreamVolume (AudioManager.STREAM_ALARM,originalAlarmVolume,0);
		}
	}

	private void StartVibrate() {
		vibrator.vibrate(vibratePattern, 0);
		isVibratorOn = true;
	}

	private void StopVibrate() {
		vibrator.cancel();
		isVibratorOn = false;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// onSensorChanged gets called for each sensor so we have to remember
		// the values
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			mAccelerometer = event.values;

		}

		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			mGeomagnetic = event.values;
		}

		if (mAccelerometer != null && mGeomagnetic != null) {

			/*
			 * t3.setText(Math.round(mAccelerometer[0] * 100) / 100 + "");
			 * 
			 * t9.setText(Math.round(mGeomagnetic[2] * 100) / 100 + "");
			 */

			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I,
					mAccelerometer, mGeomagnetic);

			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				// at this point, orientation contains the azimuth(direction),
				// pitch and roll values.
				//double azimuth = 180 * orientation[0] / Math.PI;
				double pitch = 180 * orientation[1] / Math.PI;
				double roll = 180 * orientation[2] / Math.PI;

				pitch = Math.round(pitch * 100) / 100;
				roll = Math.round(roll * 100) / 100;

				/*
				 * t10.setText(Math.round(azimuth * 100) / 100 + "");
				 * t11.setText(pitch + ""); t12.setText(roll + "");
				 */
				lastPitch = pitch;

				// boolean b1e = b1.isEnabled();
				/*
				 * double abspitch = Math.abs(pitch); double absroll =
				 * Math.abs(roll);
				 *//*
					 * if ((pitch <= -50 && pitch >= -90)) { if (!b1e) {
					 * b1.setEnabled(true); } }else { if(b1e) {
					 * b1.setEnabled(false); } }
					 */

			}
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
	
	
	private PhoneStateListener mPhoneListener = new PhoneStateListener() {
		  public void onCallStateChanged(int state, String incomingNumber) {
		   try {
		    switch (state) {
		    case TelephonyManager.CALL_STATE_RINGING:
		    	if(isOn==AppStates.On || isOn==AppStates.TransitingToOn)
		    	{
		    		isOn = AppStates.Paused;
		    		Toast.makeText(parentActivity, getText(R.string.poinkpaused), Toast.LENGTH_LONG).show();
		    	}
		     break;
		    case TelephonyManager.CALL_STATE_OFFHOOK:
		    	if(isOn==AppStates.On || isOn==AppStates.TransitingToOn)
		    	{
		    		isOn = AppStates.Paused;
		    		Toast.makeText(parentActivity, getText(R.string.poinkpaused), Toast.LENGTH_LONG).show();
		    	}
		     break;
		    case TelephonyManager.CALL_STATE_IDLE:
		    	if(isOn==AppStates.Paused)
		    	{
		    		isOn = AppStates.On;
		    		Toast.makeText(parentActivity, getText(R.string.poinkactivatedbackagain), Toast.LENGTH_LONG).show();
		    	}
		     break;
		    
		    }
		   } catch (Exception e) {
		    Log.i("Exception", "PhoneStateListener() e = " + e);
		   }
		  }
		 };
		

	private void UnregisterSensorListeners()
	{
		mSensorManager.unregisterListener(this, mGSensor);
		mSensorManager.unregisterListener(this, mASensor);
	}
	
	private void RegisterSensorListeners()
	{
		mSensorManager.registerListener(this, mGSensor,
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mASensor,
				SensorManager.SENSOR_DELAY_GAME);
	}
		 
	@Override
	public void onResume() {
		super.onResume();
		
	}

	@Override
	public void onPause() {
		super.onPause();
		
	}

	@Override
	public void onStop() {
	 super.onStop();
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mNotificationManager.cancelAll();
		audioManager.setStreamVolume (AudioManager.STREAM_ALARM,originalAlarmVolume,0);
		mHandler.removeCallbacks(runningTimer);
		UnregisterSensorListeners();
		pitchRecordHandler.removeCallbacks(pitchIntervalTimer);
		
		
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		

	}
	
	
	
	
	

}
