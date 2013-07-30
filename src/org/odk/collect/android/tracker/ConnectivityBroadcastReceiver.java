package org.odk.collect.android.tracker;

import java.util.Date;

import org.odk.collect.android.R;

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
			sendWiFiNotification();
			sendDataPlanNotification();
		}
		else {
			Log.d(TAG, "Has connection");
			Utils.log(new Date(), TAG, "Has connection");
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
	
	private void sendDataPlanNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext);

        builder.setContentTitle("Travel Quality Study")
               .setContentText("Please make sure mobile data is enabled.")
               .setSmallIcon(R.drawable.study_logo)
               .setContentIntent(getContentIntent("DATA_PLAN"))
               .setSound(sound);

        NotificationManager notifyManager = (NotificationManager)
        		mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.notify(0, builder.build());
    }
	
    private PendingIntent getContentIntent(String networkType) {
    	Intent intent = null;
    	if (networkType.equals("WIFI")) {
    		intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
    	}
    	else {
    		intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
    	}
        return PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
