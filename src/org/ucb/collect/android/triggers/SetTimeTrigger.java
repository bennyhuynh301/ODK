package org.ucb.collect.android.triggers;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


//Set alarm notification for today only
public class SetTimeTrigger extends BroadcastReceiver{
	private static final String TAG = "SetTimeTrigger";
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG,"SetTimeReceived");
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean isTrigger = pref.getBoolean("IsTrigger", false);
	    long lastTrigger = pref.getLong("LastTriggerTime", 0);
	    if(!isTrigger || ((new Date()).getTime() - lastTrigger) > 24*60*60*1000) {
	    	context.startService(new Intent(context,SetTimeTriggerService.class));
	    }
	}
}
