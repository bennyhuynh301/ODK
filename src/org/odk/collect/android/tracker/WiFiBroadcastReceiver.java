package org.odk.collect.android.tracker;

import java.util.Date;

import org.odk.collect.android.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class WiFiBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "WiFiBroadcastReceiver";
	private Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	private Context mContext = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "WiFiBroadcastReceiver starts");
		Utils.log(new Date(), TAG, "WiFiBroadcastReceiver starts");
		mContext = context;
		int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
		Log.d(TAG, "Wifi state: " + wifiState);
		Utils.log(new Date(), TAG, "Wifi state: " + wifiState);
		if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
			sendWiFiNotification();
		}
	}

	private void sendWiFiNotification() {
		NotificationCompat.Builder builder =
				new NotificationCompat.Builder(mContext);

		builder.setContentTitle("Travel Quality Study")
		.setContentText("Please make sure Wifi is enabled.")
		.setSmallIcon(R.drawable.study_logo)
		.setContentIntent(getContentIntent("WIFI"))
		.setSound(sound);

		NotificationManager notifyManager = (NotificationManager)
				mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		notifyManager.notify(1, builder.build());
	}

	private PendingIntent getContentIntent(String networkType) {
		Intent	intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
		return PendingIntent.getActivity(mContext, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

}
