package org.ucb.collect.android.tracker;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PostTripReceiver extends BroadcastReceiver {
	private static final String TAG = "TripRequestService";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG,"PostTripReceiver starts");
		Utils.log(new Date(), TAG, "PostTripReceiver starts");
		Intent postIntent = new Intent(context, PostTripService.class);
		context.startService(postIntent);
	}
}
