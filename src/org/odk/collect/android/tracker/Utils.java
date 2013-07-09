package org.odk.collect.android.tracker;

import java.util.Calendar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Utils {
	private static final String TAG = "Utils";
	public static final int NO_CONNECTION = 0;
	public static final int WAIT_FOR_WIFI = 1;
	public static final int HAS_CONNECTION = 2;
	
	static public void retryLater(Context context, Class<?> cls, int sec){
		Log.i(TAG,"Retry "+ cls.toString());
		
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.set(Calendar.HOUR_OF_DAY, 22);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		
		Calendar now = Calendar.getInstance();
		
		if (now.before(todayEnd)){
			AlarmManager nextAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent uploadRequest = new Intent(context, cls);
			PendingIntent nextCheckRequest = PendingIntent.getBroadcast(context, 6, uploadRequest, PendingIntent.FLAG_UPDATE_CURRENT);
			nextAlarm.set(AlarmManager.RTC_WAKEUP,
					now.getTimeInMillis()+ sec*1000,
					nextCheckRequest);
		}
	}
	
	static public int networkState(Context context){
		ConnectivityManager connectivityManager =
	            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
		
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    
		if (ni != null && ni.isConnected()){
			return HAS_CONNECTION;
		}
		
		if ((ni!= null && ni.isConnectedOrConnecting()) || (wm != null && wm.isWifiEnabled())){
			//wm.reconnect();
			return WAIT_FOR_WIFI;
		}
		
		return NO_CONNECTION;
	}
	
	static boolean servicesConnected(Context context) {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if (ConnectionResult.SUCCESS == resultCode) {
			Log.d(TAG, "Google Play Service is available");
			return true;
		} else {
			Log.e(TAG, "Google Play Service is not available: " + resultCode);
			return false;
		}
	}
}
