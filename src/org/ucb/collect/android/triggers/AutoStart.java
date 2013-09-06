package org.ucb.collect.android.triggers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {
	private static final String TAG = "AutoStart";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction()) || Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ) {
			Log.d(TAG,"AutoStartReceived");
			Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
			editor.putBoolean("IsTrigger", false);
			editor.putBoolean("IsDownloaded", false);
			editor.commit();
			context.startService(new Intent(context, MainService.class));
		}
	}
}
