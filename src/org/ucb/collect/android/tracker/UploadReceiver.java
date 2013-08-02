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
		
		DetectionRequester mDetectionRequester = DetectionRequester.getInstance();
		mDetectionRequester.setContext(context);
		LocationUpdateRequester mLocationUpdateRequester = LocationUpdateRequester.getInstance();
		mLocationUpdateRequester.setContext(context);
		DetectionRemover mDetectionRemover = DetectionRemover.getInstance();
		mDetectionRemover.setContext(context);
		LocationUpdateRemover mLocationUpdateRemover = LocationUpdateRemover.getInstance();
		mLocationUpdateRemover.setContext(context);

		Bundle b = intent.getExtras();
		if (b != null && b.getString("RESP").equals("UPLOAD")) {
			Log.d(TAG, b.getString("RESP"));
			Utils.log(new Date(), TAG, b.getString("RESP"));
			try {
				if (Utils.servicesConnected(context)) {
					Log.d(TAG, "Start uploading the logfile");
					context.startService(new Intent(context, UploadDataService.class));
				}
			} catch (Exception e) {
				Utils.log(new Date(), TAG, e.getMessage());
				e.printStackTrace();
			}
		}

		else if (b != null && b.getString("RESP").equals("SUCCESS")) {
			Log.d(TAG, b.getString("RESP"));
			Utils.log(new Date(), TAG, b.getString("RESP"));
		}
	}
}
