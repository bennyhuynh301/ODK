package org.ucb.collect.android.tracker;

import java.util.Date;

import org.ucb.collect.android.R;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;


public class LocationUpdateRequester implements ConnectionCallbacks, OnConnectionFailedListener {
	private Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
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
		Utils.log(new Date(), LocationUtils.APPTAG, "Location client connected");
		LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!gps_enabled && !network_enabled) {
			Log.d(LocationUtils.APPTAG, "Location provider is not enabled");
			Utils.log(new Date(), LocationUtils.APPTAG, "Location provider is not enabled");
			sendLocationAccessNotification();
		}
		continueRequestLocationUpdates();
	}
	
	private void sendLocationAccessNotification() {
		NotificationCompat.Builder builder =
				new NotificationCompat.Builder(mContext);

		builder.setContentTitle("Travel Quality Study")
		.setContentText("Please make sure Location Service is enabled.")
		.setSmallIcon(R.drawable.study_logo)
		.setContentIntent(getContentIntent())
		.setSound(sound)
		.setAutoCancel(true);

		NotificationManager notifyManager = (NotificationManager)
				mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		notifyManager.notify(1, builder.build());
	}

	private PendingIntent getContentIntent() {
		Intent	intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		return PendingIntent.getActivity(mContext, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Override
	public void onDisconnected() {
		Log.d(LocationUtils.APPTAG, "Location client disconnected");
		mLocationRequest = null;
		mLocationClient = null;
	}

	//Temporarily fixed.  Should implement better mechanism
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			//            try {
			//                connectionResult.startResolutionForResult((Activity) mContext,
			//                    LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
			//            } catch (SendIntentException e) {
			//				Utils.log(new Date(), LocationUtils.APPTAG, e.getMessage());
			//            }
			requestConnection();
		} 
		else {
			Log.d(LocationUtils.APPTAG, "Connection fails: " + connectionResult.getErrorCode());
			Utils.log(new Date(), LocationUtils.APPTAG, "Connection fails: " + connectionResult.getErrorCode());
		}
	}
}
