package org.odk.collect.android.tracker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionClient;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

public class DetectionRemover
implements ConnectionCallbacks, OnConnectionFailedListener {

	private Context mContext;
	private ActivityRecognitionClient mActivityRecognitionClient;
	private PendingIntent mCurrentIntent;

	private static DetectionRemover instance = null;

	public static DetectionRemover getInstance() {
		if (instance == null) {
			instance = new DetectionRemover();
		}
		return instance;
	}

	private DetectionRemover() {
		mActivityRecognitionClient = null;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}

	public void removeUpdates(PendingIntent requestIntent) {
		mCurrentIntent = requestIntent;
		requestConnection();
	}

	private void requestConnection() {
		getActivityRecognitionClient().connect();
	}

	public ActivityRecognitionClient getActivityRecognitionClient() {
		if (mActivityRecognitionClient == null) {
			setActivityRecognitionClient(new ActivityRecognitionClient(mContext, this, this));
		}
		return mActivityRecognitionClient;
	}

	private void requestDisconnection() {
		getActivityRecognitionClient().disconnect();
		setActivityRecognitionClient(null);
	}

	public void setActivityRecognitionClient(ActivityRecognitionClient client) {
		mActivityRecognitionClient = client;
	}

	@Override
	public void onConnected(Bundle connectionData) {
		Log.d(ActivityUtils.APPTAG, "Dectection client connected");
		continueRemoveUpdates();
	}

	private void continueRemoveUpdates() {
		mActivityRecognitionClient.removeActivityUpdates(mCurrentIntent);
		mCurrentIntent.cancel();
		requestDisconnection();
	}

	@Override
	public void onDisconnected() {
		Log.d(ActivityUtils.APPTAG, "Dectection client disconnected");
		mActivityRecognitionClient = null;
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult((Activity) mContext,
						ActivityUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (SendIntentException e) {

			}
		} 
		else {
			Log.d(ActivityUtils.APPTAG, "Connection fails: " + connectionResult.getErrorCode());
		}   
	}
}
