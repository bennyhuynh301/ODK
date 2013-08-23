package org.ucb.collect.android.tracker;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class UploadReceiver extends BroadcastReceiver {
	private static final String TAG = "UploadReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "UploadReceiver starts");
		Utils.log(new Date(), TAG, "UploadReceiver starts");
		Bundle b = intent.getExtras();
		if (b != null && b.getString("RESP").equals("UPLOAD")) {
			Log.d(TAG, b.getString("RESP"));
			Utils.log(new Date(), TAG, b.getString("RESP"));
			try {
				if (Utils.servicesConnected(context)) {
					Log.d(TAG, "Start uploading the logfile");
					context.startService(new Intent(context, UploadDataService.class));
					Intent stopUpdate = new Intent(context, UpdateReceiver.class);
					stopUpdate.setAction("STOP_UPDATE");
					context.sendBroadcast(stopUpdate);
				}
			} catch (Exception e) {
				Utils.log(new Date(), TAG, e.getMessage());
				e.printStackTrace();
			}
		}

		else if (b != null && b.getString("RESP").equals("SUCCESS")) {
			Log.d(TAG, b.getString("RESP"));
			Utils.log(new Date(), TAG, b.getString("RESP"));
			Intent startUpdate = new Intent(context, UpdateReceiver.class);
			startUpdate.setAction("START_UPDATE");
			context.sendBroadcast(startUpdate);
		}
	}
}
