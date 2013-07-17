package org.odk.collect.android.tracker;

import java.util.Date;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;


public class LocationUpdateRequester implements ConnectionCallbacks, OnConnectionFailedListener {

	private long timeUpdateInterval;
	private Context mContext;
	private PendingIntent mUpdateLocationPendingIntent;
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	
	private static LocationUpdateRequester instance = null;
	
	public static LocationUpdateRequester getInstance() {
		if (instance == null) {
			instance = new LocationUpdateRequester();
		}
		return instance;
	}
	
	private LocationUpdateRequester() {
		mUpdateLocationPendingIntent = null;
		mLocationClient = null;
		mLocationRequest = null;
		timeUpdateInterval = LocationUtils.NIGHTTIME_UPDATE_INTERVAL_IN_MILLISECONDS;	
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	public long getUpdateTimeInterval() {
    	return timeUpdateInterval;
    }
    
    public void setUpdateTimeInterval(long timeInterval) {
    	timeUpdateInterval = timeInterval;
    }
	
	public PendingIntent getRequestPendingIntent() {
        return mUpdateLocationPendingIntent;
    }
	
	public void setRequestPendingIntent(PendingIntent intent) {
		mUpdateLocationPendingIntent = intent;
    }
	
	public void requestUpdates() {
		requestConnection();
	}
	
	private void continueRequestLocationUpdates() {
		getLocationClient().requestLocationUpdates(getLocationRequest(), createRequestPendingIntent());
		requestDisconnection();
	}
	
	private PendingIntent createRequestPendingIntent() {
		if (getRequestPendingIntent() != null) {
			return mUpdateLocationPendingIntent;
		}
		else {
			Intent intent = new Intent(mContext, LocationUpdateIntentService.class);
			PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, intent, 
					PendingIntent.FLAG_UPDATE_CURRENT);
			setRequestPendingIntent(pendingIntent);
			return pendingIntent;
		}
	}

	private void requestConnection() {
		getLocationClient().connect();
	}
	
	private void requestDisconnection() {
		getLocationClient().disconnect();
	}

	private LocationClient getLocationClient() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(mContext, this, this);
		}
		return mLocationClient;
	}
	
	private LocationRequest getLocationRequest() {
		if (mLocationRequest == null) {
			mLocationRequest = LocationRequest.create();
			mLocationRequest.setInterval(timeUpdateInterval);
			mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
			mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
		}
		return mLocationRequest;
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.d(LocationUtils.APPTAG, "Location client connected");
		continueRequestLocationUpdates();
	}

	@Override
	public void onDisconnected() {
		Log.d(LocationUtils.APPTAG, "Location client disconnected");
		mLocationRequest = null;
		mLocationClient = null;
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult((Activity) mContext,
                    LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (SendIntentException e) {
				Utils.log(new Date(), LocationUtils.APPTAG, e.getMessage());
            }
        } 
		else {
			Log.d(LocationUtils.APPTAG, "Connection fails: " + connectionResult.getErrorCode());
			Utils.log(new Date(), LocationUtils.APPTAG, "Connection fails: " + connectionResult.getErrorCode());
        }
    }
}
