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
		DetectionRequester mDetectionRequester = new DetectionRequester(context);
		LocationUpdateRequester mLocationUpdateRequester = new LocationUpdateRequester(context);
		DetectionRemover mDetectionRemover = new DetectionRemover(context);
		LocationUpdateRemover mLocationUpdateRemover = new LocationUpdateRemover(context);
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
					if (mDetectionRequester.getRequestPendingIntent() != null) {
						mDetectionRemover.removeUpdates(mDetectionRequester.getRequestPendingIntent());
						mDetectionRequester.getRequestPendingIntent().cancel();
					}
					if (mLocationUpdateRequester.getRequestPendingIntent() != null) {
						mLocationUpdateRemover.removeUpdates(mLocationUpdateRequester.getRequestPendingIntent());
						mLocationUpdateRequester.getRequestPendingIntent().cancel();
					}
				}
			}
		} catch (Exception e) {
			Utils.log(new Date(), TAG, e.getMessage());
			e.printStackTrace();
		}
	}
}
