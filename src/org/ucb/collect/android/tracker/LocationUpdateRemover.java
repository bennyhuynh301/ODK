package org.ucb.collect.android.tracker;

import java.util.Date;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;

public class LocationUpdateRemover  implements ConnectionCallbacks, OnConnectionFailedListener {

	private Context mContext;
	private LocationClient mLocationClient;
	private PendingIntent mCurrentIntent;
	
	public LocationUpdateRemover(Context context) {
		mContext = context;
		mLocationClient = null;
	}

	public void removeUpdates(PendingIntent requestIntent) {
		mCurrentIntent = requestIntent;
		requestConnection();
	}

	private void requestConnection() {
		getLocationClient().connect();
	}

	public LocationClient getLocationClient() {
		if (mLocationClient == null) {
			setLocationClient(new LocationClient(mContext, this, this));
		}
		return mLocationClient;
	}

	private void requestDisconnection() {
		getLocationClient().disconnect();
		setLocationClient(null);
	}

	public void setLocationClient(LocationClient client) {
		mLocationClient = client;
	}

	@Override
	public void onConnected(Bundle connectionData) {
		Log.d(LocationUtils.APPTAG, "Location client connected");
		Utils.log(new Date(), LocationUtils.APPTAG, "Location client connected");
		continueRemoveUpdates();
	}

	private void continueRemoveUpdates() {
		mLocationClient.removeLocationUpdates(mCurrentIntent);
		mCurrentIntent.cancel();
		requestDisconnection();
	}

	@Override
	public void onDisconnected() {
		Log.d(LocationUtils.APPTAG, "Location client disconnected");
		Utils.log(new Date(), LocationUtils.APPTAG, "Location client disconnected");
		mLocationClient = null;
	}

	//Temporarily fixed.  Should implement better mechanism
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
//			try {
//				connectionResult.startResolutionForResult((Activity) mContext,
//						ActivityUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
//			} catch (SendIntentException e) {
//				Utils.log(new Date(), LocationUtils.APPTAG, e.getMessage());
//			}
			requestDisconnection();
		} 
		else {
			Log.d(LocationUtils.APPTAG, "Connection fails: " + connectionResult.getErrorCode());
			Utils.log(new Date(), LocationUtils.APPTAG, "Connection fails: " + connectionResult.getErrorCode());
		}   
	}
}