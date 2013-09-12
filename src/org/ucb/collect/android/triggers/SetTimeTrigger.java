package org.ucb.collect.android.triggers;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;


//Set alarm notification for today only
public class SetTimeTrigger extends BroadcastReceiver{
	private boolean DEBUG = false;
	private static final String TAG = "SetTimeTrigger";
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.d(TAG,"SetTimeReceived");
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = pref.edit();
	    long lastTrigger = pref.getLong("LastTriggerTime", 0);
	    int interval;
	    if (DEBUG) {
	    	interval = 11*60*1000;
	    }
	    else {
	    	interval = 12*60*60*1000;
	    }
	    if ((new Date()).getTime() - lastTrigger >= interval) {
	    	editor.putBoolean("IsTrigger", false);
	    	editor.putLong("LastTriggerTime", (new Date()).getTime());
	    	editor.commit();
	    }

	    boolean isTrigger = pref.getBoolean("IsTrigger", false);
	    if(!isTrigger) {
	    	context.startService(new Intent(context,SetTimeTriggerService.class));
	    }
	}
}
