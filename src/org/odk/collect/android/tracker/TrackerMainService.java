package org.odk.collect.android.tracker;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TrackerMainService extends Service {
	private static final String TAG = "MAINSERVICE";
	
	private final int randomMin = (int) (Math.random()*44 + 1);
	private final int randomSecond = (int) (Math.random()*59 + 1);
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Start main service");
		Utils.log(new Date(), TAG, "Start main service");
		Intent updateIntent = new Intent(this, UpdateReceiver.class);
		this.sendBroadcast(updateIntent);
		return Service.START_STICKY;
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "Create main service");
		Utils.log(new Date(), TAG, "Create main service");
		super.onCreate();

		Calendar onUploadTime = Calendar.getInstance();
		onUploadTime.set(Calendar.HOUR_OF_DAY, 0);
		onUploadTime.set(Calendar.MINUTE, randomMin);
		onUploadTime.set(Calendar.SECOND, randomSecond);
		Log.d(TAG, "Upload Time: " + onUploadTime.getTime());
		Utils.log(new Date(), TAG, "Upload Time: " + onUploadTime.getTime());
		
		AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		Intent uploadIntent = new Intent(this, UploadReceiver.class);
		uploadIntent.putExtra("RESP", "UPLOAD");
		PendingIntent uploadSender = PendingIntent.getBroadcast(this,(int) System.currentTimeMillis(),uploadIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, onUploadTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, uploadSender);
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "Destroy main service");
		Utils.log(new Date(), TAG, "Destroy main service");
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
