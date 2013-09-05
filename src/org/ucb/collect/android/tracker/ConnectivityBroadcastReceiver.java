package org.ucb.collect.android.tracker;

import java.lang.reflect.Method;
import java.util.Date;

import org.ucb.collect.android.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ConnectivityBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "ConnectivityBroadcastReceiver";
	private Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	private Context mContext = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "ConnectivityBroadcastReceiver starts");
		Utils.log(new Date(), TAG, "ConnectivityBroadcastReceiver starts");
		mContext = context;
		boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		if (noConnectivity) {
			Log.d(TAG, "No connection");
			Utils.log(new Date(), TAG, "No connection");
			if (!isMobileDataEnabled()) {
				sendDataPlanNotification();
			}
		}
		else {
			Log.d(TAG, "Has connection");
			Utils.log(new Date(), TAG, "Has connection");
		}
	}

	private void sendDataPlanNotification() {
		NotificationCompat.Builder builder =
				new NotificationCompat.Builder(mContext);

		builder.setContentTitle("Travel Quality Study")
		.setContentText("Please make sure mobile data is enabled.")
		.setSmallIcon(R.drawable.exclamation)
		.setContentIntent(getContentIntent())
		.setSound(sound)
		.setAutoCancel(true);

		NotificationManager notifyManager = (NotificationManager)
				mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		notifyManager.notify(0, builder.build());
	}

	private PendingIntent getContentIntent() {
		Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
		return PendingIntent.getActivity(mContext, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private boolean isMobileDataEnabled() {
		boolean mobileDataEnabled = false;
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			Class<?> cmClass = Class.forName(cm.getClass().getName());
			Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
			method.setAccessible(true); 
			mobileDataEnabled = (Boolean) method.invoke(cm);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
			Utils.log(new Date(), TAG, e.getMessage());
			e.printStackTrace();
			return mobileDataEnabled;
		}
		Log.d(TAG, "Mobile data is enable: " + mobileDataEnabled);
		Utils.log(new Date(), TAG, "Mobile data is enable: " + mobileDataEnabled);
		return mobileDataEnabled;
	}
}
