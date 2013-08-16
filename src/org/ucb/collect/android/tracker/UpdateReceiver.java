package org.ucb.collect.android.tracker;

import java.util.Date;

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
		
		DetectionRemover mDetectionRemover = DetectionRemover.getInstance();
		mDetectionRemover.setContext(context);
		LocationUpdateRemover mLocationUpdateRemover = LocationUpdateRemover.getInstance();
		mLocationUpdateRemover.setContext(context);
		
		try {
			if (Utils.servicesConnected(context)) {
				if (intent.getAction().equals("START_UPDATE")) {
					Log.d(TAG, "Start updating location and activity");
					Utils.log(new Date(), TAG, "Start updating location and activity");
					mDetectionRequester.setUpdateTimeInterval(ActivityUtils.DAYTIME_DETECTION_INTERVAL_MILLISECONDS);
					mDetectionRequester.requestUpdates();
					mLocationUpdateRequester.setUpdateTimeInterval(LocationUtils.DAYTIME_UPDATE_INTERVAL_IN_MILLISECONDS);
					mLocationUpdateRequester.requestUpdates();
				}
				else if (intent.getAction().equals("STOP_UPDATE")) {
					Log.d(TAG, "Stop updating location and activity");
					Utils.log(new Date(), TAG, "Stop updating location and activity");
					mDetectionRemover.removeUpdates(mDetectionRequester.getRequestPendingIntent());
					mLocationUpdateRemover.removeUpdates(mLocationUpdateRequester.getRequestPendingIntent());
				}
			}
		} catch (Exception e) {
			Utils.log(new Date(), TAG, e.getMessage());
			e.printStackTrace();
		}
	}
}
