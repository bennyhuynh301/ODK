package org.ucb.collect.android.tracker;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
	
public class BatteryLevelReceiver extends BroadcastReceiver {
	private static final String TAG = "BatteryLevelReceiver"; 
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "BatteryLevelReceiver starts");
		Utils.log(new Date(), TAG, "BatteryLevelReceiver starts");
		
		String action = intent.getAction();
		Intent mainService = new Intent(context, TrackerMainService.class);
		if (action.equals(Intent.ACTION_BATTERY_LOW)) {
			Log.d(TAG, "Battery is low");
			Utils.log(new Date(), TAG, "Battery is low");
			context.stopService(mainService);
		}
		else if (action.equals(Intent.ACTION_BATTERY_OKAY)) {
			Log.d(TAG, "Battery is okay");
			Utils.log(new Date(), TAG, "Battery is okay");
			context.startService(mainService);
		}
	}
}
