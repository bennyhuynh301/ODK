package org.odk.collect.android.tracker;

import java.util.Date;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionClient;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

public class DetectionRequester
implements ConnectionCallbacks, OnConnectionFailedListener {

	private long timeUpdateInterval;
	private Context mContext;
	private PendingIntent mActivityRecognitionPendingIntent;
	private ActivityRecognitionClient mActivityRecognitionClient;

	private static DetectionRequester instance = null;

	public static DetectionRequester getInstance() {
		if (instance == null) {
			instance = new DetectionRequester();
		}
		return instance;
	}

	private DetectionRequester() {
		mActivityRecognitionPendingIntent = null;
		mActivityRecognitionClient = null;
		timeUpdateInterval = ActivityUtils.NIGHTTIME_DETECTION_INTERVAL_MILLISECONDS;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}

	public PendingIntent getRequestPendingIntent() {
		return mActivityRecognitionPendingIntent;
	}

	public long getUpdateTimeInterval() {
		return timeUpdateInterval;
	}

	public void setUpdateTimeInterval(long timeInterval) {
		timeUpdateInterval = timeInterval;
	}

	public void setRequestPendingIntent(PendingIntent intent) {
		mActivityRecognitionPendingIntent = intent;
	}

	public void requestUpdates() {
		requestConnection();
	}

	private void continueRequestActivityUpdates() {
		getActivityRecognitionClient().requestActivityUpdates(timeUpdateInterval, createRequestPendingIntent());
		requestDisconnection();
	}

	private void requestConnection() {
		getActivityRecognitionClient().connect();
	}


	private ActivityRecognitionClient getActivityRecognitionClient() {
		if (mActivityRecognitionClient == null) {
			mActivityRecognitionClient =
					new ActivityRecognitionClient(mContext, this, this);
		}
		return mActivityRecognitionClient;
	}


	private void requestDisconnection() {
		getActivityRecognitionClient().disconnect();
	}


	@Override
	public void onConnected(Bundle arg0) {
		Log.d(ActivityUtils.APPTAG, "Detection client connected");
		Utils.log(new Date(), ActivityUtils.APPTAG, "Dectection client connected");
		continueRequestActivityUpdates();
	}


	@Override
	public void onDisconnected() {
		Log.d(ActivityUtils.APPTAG, "Detection client disconnected");
		Utils.log(new Date(), ActivityUtils.APPTAG, "Detection client disconnected");
		mActivityRecognitionClient = null;
	}

	private PendingIntent createRequestPendingIntent() {
		if (null != getRequestPendingIntent()) {
			return mActivityRecognitionPendingIntent;
		} else {
			Intent intent = new Intent(mContext, ActivityRecognitionIntentService.class);
			PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			setRequestPendingIntent(pendingIntent);
			return pendingIntent;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult((Activity) mContext,
						ActivityUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (SendIntentException e) {
				Utils.log(new Date(), ActivityUtils.APPTAG, e.getMessage());
			}
		} 
		else {
			Log.d(ActivityUtils.APPTAG, "Connection fails: " + connectionResult.getErrorCode());
			Utils.log(new Date(), ActivityUtils.APPTAG, "Connection fails: " + connectionResult.getErrorCode());
		}
	}
}
