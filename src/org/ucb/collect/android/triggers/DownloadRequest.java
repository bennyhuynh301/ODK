package org.ucb.collect.android.triggers;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class DownloadRequest extends BroadcastReceiver {
	private static final String TAG = "DownloadRequest";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG,"DownloadRequestReceived");
	    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
	    boolean isTrigger = pref.getBoolean("IsTrigger", false);
	    long lastTrigger = pref.getLong("LastTriggerTime", 0);
	    if(!isTrigger || ((new Date()).getTime() - lastTrigger) > 24*60*60*1000) {
			context.startService(new Intent("downloadservice"));
	    }	    
	}
}
