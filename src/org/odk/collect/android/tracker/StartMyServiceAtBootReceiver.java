package org.odk.collect.android.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {
	private static final String TAG = "StartMyServiceAtBootReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "StartMyServiceAtBootReceiver starts");
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Intent mainService = new Intent(context, TrackerMainService.class);
			context.startService(mainService);
			Toast.makeText(context, "MainService starts", Toast.LENGTH_SHORT).show();
		}
	}
}
