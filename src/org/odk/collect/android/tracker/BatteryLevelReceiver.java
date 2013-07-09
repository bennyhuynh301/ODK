package org.odk.collect.android.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BatteryLevelReceiver extends BroadcastReceiver {
	private static final String TAG = "BatteryLevelReceiver"; 
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "BatteryLevelReceiver starts");
		
		String action = intent.getAction();
		Intent mainService = new Intent(context, TrackerMainService.class);
		if (action.equals(Intent.ACTION_BATTERY_LOW)) {
			Log.d(TAG, "Battery is low");
			context.stopService(mainService);
			Toast.makeText(context, "MainService stops", Toast.LENGTH_SHORT).show();
		}
		else if (action.equals(Intent.ACTION_BATTERY_OKAY)) {
			Log.d(TAG, "Battery is okay");
			context.startService(mainService);
			Toast.makeText(context, "MainService starts", Toast.LENGTH_SHORT).show();
		}
	}
}
