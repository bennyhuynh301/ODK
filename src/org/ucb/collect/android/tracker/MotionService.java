package org.ucb.collect.android.tracker;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class MotionService extends Service implements SensorEventListener {
	private static final String TAG = "MotionService";
	private SensorManager sensorManager;
	private Sensor mSensor;
	private long lastUpdate;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "MotionService creates");
		super.onCreate();
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
		lastUpdate = System.currentTimeMillis();
		
		Calendar now = Calendar.getInstance();
		long timeout = now.getTimeInMillis() + 1*60*1000;
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent selfStop = new Intent(this, MotionTrackerStopReceiver.class);
		PendingIntent selfStopSender = PendingIntent.getBroadcast(this,(int) System.currentTimeMillis(),selfStop,PendingIntent.FLAG_UPDATE_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, timeout, selfStopSender);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		sensorManager.unregisterListener(this, mSensor);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float x,y,z;
			x = event.values[0];
			y = event.values[1];
			z = event.values[2];
			long current = System.currentTimeMillis();
			if (current - lastUpdate > 2) {
				logMotionResults(x,y,z);
			}
			lastUpdate = current;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void logMotionResults(float x, float y, float z) {
		long timeStamp = (new Date()).getTime();
		StringBuilder sb = new StringBuilder("{\"accel\":{");
		sb.append("\"time\":").append(timeStamp).append(",");
		sb.append("\"x\":").append(x).append(",");
		sb.append("\"y\":").append(y).append(",");
		sb.append("\"z\":").append(z);
		sb.append("}}");
		String log = sb.toString();
		Log.d(TAG, "Accel: " + log);
		File f = new File(Environment.getExternalStorageDirectory(),"Travel_Study/data_accel.txt");
		try{ 
			if (!f.exists()) {
				f.createNewFile();
			}
			FileWriter out = new FileWriter(f,true);
			out.write(log +"\n");
			out.close();
		} catch (Exception e) {
			return;
		}
	}
}
