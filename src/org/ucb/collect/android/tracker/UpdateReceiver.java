package org.ucb.collect.android.tracker;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateReceiver extends BroadcastReceiver {
	private static final String TAG = "UpdateReceiver";
	
	private static class UpdateRequest {
		static DetectionRequester mDetectionRequester = new DetectionRequester();
		static LocationUpdateRequester mLocationUpdateRequester = new LocationUpdateRequester();
		static DetectionRemover mDetectionRemover = new DetectionRemover();
		static LocationUpdateRemover mLocationUpdateRemover = new LocationUpdateRemover();
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			UpdateRequest.mLocationUpdateRequester.setContext(context);
			UpdateRequest.mLocationUpdateRemover.setContext(context);
			UpdateRequest.mDetectionRequester.setContext(context);
			UpdateRequest.mDetectionRemover.setContext(context);
			if (Utils.servicesConnected(context)) {
				if (intent.getAction().equals("START_UPDATE")) {
					Log.d(TAG, "Start updating location and activity");
					Utils.log(new Date(), TAG, "Start updating location and activity");
					UpdateRequest.mDetectionRequester.setUpdateTimeInterval(ActivityUtils.DAYTIME_DETECTION_INTERVAL_MILLISECONDS);
					UpdateRequest.mDetectionRequester.requestUpdates();
					UpdateRequest.mLocationUpdateRequester.setUpdateTimeInterval(LocationUtils.DAYTIME_UPDATE_INTERVAL_IN_MILLISECONDS);
					UpdateRequest.mLocationUpdateRequester.requestUpdates();
				}
				else if (intent.getAction().equals("STOP_UPDATE")) {
					Log.d(TAG, "Stop updating location and activity");
					Utils.log(new Date(), TAG, "Stop updating location and activity");
					if (UpdateRequest.mDetectionRequester.getRequestPendingIntent() != null) {
						UpdateRequest.mDetectionRemover.removeUpdates(UpdateRequest.mDetectionRequester.getRequestPendingIntent());
						UpdateRequest.mDetectionRequester.getRequestPendingIntent().cancel();
						UpdateRequest.mDetectionRequester.setRequestPendingIntent(null);
					}
					if (UpdateRequest.mLocationUpdateRequester.getRequestPendingIntent() != null) {
						UpdateRequest.mLocationUpdateRemover.removeUpdates(UpdateRequest.mLocationUpdateRequester.getRequestPendingIntent());
						UpdateRequest.mLocationUpdateRequester.getRequestPendingIntent().cancel();
						UpdateRequest.mLocationUpdateRequester.setRequestPendingIntent(null);
					}
				}
			}
		} catch (Exception e) {
			Utils.log(new Date(), TAG, e.getMessage());
			e.printStackTrace();
		}
	}
}
