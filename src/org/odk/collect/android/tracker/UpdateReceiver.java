package org.odk.collect.android.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateReceiver extends BroadcastReceiver {
	private static final String TAG = "UpdateReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		DetectionRequester mDetectionRequester = DetectionRequester.getInstance();
		mDetectionRequester.setContext(context);
		LocationUpdateRequester mLocationUpdateRequester = LocationUpdateRequester.getInstance();
		mLocationUpdateRequester.setContext(context);
		try {
			if (Utils.servicesConnected(context)) {
				Log.d(TAG, "Start updating location and activity");
				mDetectionRequester.setUpdateTimeInterval(ActivityUtils.DAYTIME_DETECTION_INTERVAL_MILLISECONDS);
				mDetectionRequester.requestUpdates();
				mLocationUpdateRequester.setUpdateTimeInterval(LocationUtils.DAYTIME_UPDATE_INTERVAL_IN_MILLISECONDS);
				mLocationUpdateRequester.requestUpdates();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
