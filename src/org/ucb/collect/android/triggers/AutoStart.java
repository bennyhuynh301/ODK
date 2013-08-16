package org.ucb.collect.android.triggers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {
	private static final String TAG = "AutoStart";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG,"AutoStartReceived");
		context.startService(new Intent(context, MainService.class));
	}
}
