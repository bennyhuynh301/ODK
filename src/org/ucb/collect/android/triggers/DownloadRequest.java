package org.ucb.collect.android.triggers;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class DownloadRequest extends BroadcastReceiver {
	private boolean DEBUG = true;
	private static final String TAG = "DownloadRequest";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG,"DownloadRequestReceived");
	    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
	    Editor editor = pref.edit();
	    boolean isDownloaded = pref.getBoolean("IsDownloaded", false);
	    long lastDownload = pref.getLong("LastDownloadTime", 0);
	    int interval;
	    if (DEBUG) {
	    	interval = 14*60*1000;
	    }
	    else {
	    	interval = 23*60*60*1000;
	    }
	    if ((new Date()).getTime() - lastDownload >= interval) {
	    	editor.putBoolean("IsDownloaded", false);
	    	editor.putLong("LastDownloadTime", (new Date()).getTime());
	    	editor.commit();
	    }
	    if(!isDownloaded) {
			context.startService(new Intent("downloadservice"));
	    }	    
	}
}
