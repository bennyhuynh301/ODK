package org.ucb.collect.android.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MotionTrackerStopReceiver extends BroadcastReceiver {
	private static final String TAG = "MotionTrackerStopReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "MotionTrackerStopReceiver starts");
		Intent stopService = new Intent(context, MotionService.class);
		context.stopService(stopService);
	}
}
