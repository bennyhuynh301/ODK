package org.ucb.collect.android.tracker;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class UploadReceiver extends BroadcastReceiver {
	private static final String TAG = "UploadReceiver";
	private boolean DEBUG = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "UploadReceiver starts");
		Utils.log(new Date(), TAG, "UploadReceiver starts");
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = pref.edit();
	    long lastUploadTime = pref.getLong("LastUploadTime", 0);
	    int interval;
	    if (DEBUG) {
	    	interval = 11*60*1000;
	    }
	    else {
	    	interval = 12*60*60*1000;
	    }
	    if ((new Date()).getTime() - lastUploadTime >= interval) {
	    	editor.putBoolean("IsUploaded", false);
	    	editor.putLong("LastUploadTime", (new Date()).getTime());
	    	editor.commit();
	    }
		Bundle b = intent.getExtras();
		if (b != null && b.getString("RESP").equals("UPLOAD")) {
			Log.d(TAG, b.getString("RESP"));
			Utils.log(new Date(), TAG, b.getString("RESP"));
			try {
				if (Utils.servicesConnected(context)) {
					Log.d(TAG, "Start uploading the logfile");
					boolean isUploaded = pref.getBoolean("IsUploaded", false);
					if (!isUploaded) {
						Intent stopUpdate = new Intent(context, UpdateReceiver.class);
						stopUpdate.setAction("STOP_UPDATE");
						context.sendBroadcast(stopUpdate);
						context.startService(new Intent(context, UploadDataService.class));
					}
				}
			} catch (Exception e) {
				Utils.log(new Date(), TAG, e.getMessage());
				e.printStackTrace();
			}
		}

		else if (b != null && (b.getString("RESP").equals("SUCCESS") || b.getString("RESP").equals("FAILURE"))) {
			Log.d(TAG, b.getString("RESP"));
			Utils.log(new Date(), TAG, b.getString("RESP")); 
			if (b.getString("RESP").equals("SUCCESS")) {
				editor.putBoolean("IsUploaded", true);
				editor.commit();
			}
			Intent startUpdate = new Intent(context, UpdateReceiver.class);
			startUpdate.setAction("START_UPDATE");
			context.sendBroadcast(startUpdate);
		}
	}
}
