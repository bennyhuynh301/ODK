package org.ucb.collect.android.tracker;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class RequestTripsReceiver extends BroadcastReceiver {
	private static final String TAG = "RequestTripsReciever";
	private static final boolean DEBUG = false;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "RequestTripsReciever starts");
		Utils.log(new Date(), TAG, "RequestTripsReciever starts");
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = pref.edit();
	    long lastRequestTripTime = pref.getLong("LastRequestTripTime", 0);
	    int interval;
	    if (DEBUG) {
	    	interval = 11*60*1000;
	    }
	    else {
	    	interval = 12*60*60*1000;
	    }
	    if ((new Date()).getTime() - lastRequestTripTime >= interval) {
	    	editor.putBoolean("IsRequestTrip", false);
	    	editor.putLong("LastRequestTripTime", (new Date()).getTime());
	    	editor.commit();
	    }
	    boolean isRequestTrip = pref.getBoolean("IsUploaded", false);
	    if (!isRequestTrip) {
	    	context.startService(new Intent(context, TripRequestService.class));
	    }
	}
}
