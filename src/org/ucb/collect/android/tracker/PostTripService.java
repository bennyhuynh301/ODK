package org.ucb.collect.android.tracker;

import java.io.File;
import java.util.Date;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PostTripService extends Service {
	private static final String TAG = "PostTripService";
	private static final String SERVER_URL= "http://TQS-LB-317129516.us-west-1.elb.amazonaws.com:61245/api/trips";

	private PowerManager.WakeLock wakeLock;
	private WifiManager.WifiLock wifiLock;

	@Override
	public void onCreate(){
		Log.d(TAG,"PostTripService creates");
		Utils.log(new Date(), TAG, "PostTripService creates");
		super.onCreate();

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PostTripService");  
		wakeLock.acquire();

		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, "PostTripService");
		wifiLock.acquire();
	}

	@Override
	public int onStartCommand(Intent in, int flags, int startId){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String path = pref.getString("ZipPath", null);
		switch (Utils.networkState(this)){
		case Utils.NO_CONNECTION:
			Log.d(TAG,"PostTripService NO_CONNECTION");
			Utils.log(new Date(), TAG, "PostTripService NO_CONNECTION");
			Utils.retryLater(this,PostTripReceiver.class, 3600);
			break;
		case Utils.WAIT_FOR_WIFI:
			Log.d(TAG, "PostTripService WAIT_FOR_WIFI");
			Utils.log(new Date(), TAG, "PostTripService WAIT_FOR_WIFI");
			Utils.retryLater(this,PostTripReceiver.class, 10);
			break;
		case Utils.HAS_CONNECTION:
			Log.d(TAG, "PostTripService HAS_CONNECTION");
			Utils.log(new Date(), TAG, "PostTripService HAS_CONNECTION");
			new Upload(path).execute();
			stopSelf();
			break;
		default:
			stopSelf();
			break;
		}
		return START_STICKY;
	}

	private class Upload extends AsyncTask<Void, Void, Void> {

		String path;
		public Upload(String path) {
			this.path = path;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			Context context = getApplicationContext();
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String phoneID = telephonyManager.getDeviceId();
			Log.d(TAG, "PhoneId: " + phoneID);
			Utils.log(new Date(), TAG, "PhoneId: " + phoneID);
			String response = post(phoneID, path);
			if (response.equals("FAILURE")) {
				Log.d(TAG, "Upload fails. Retry....");
				Utils.log(new Date(), TAG, "Upload fails. Retry....");
				Utils.retryLater(context,PostTripReceiver.class, 3600);
			}
			else if (response.equals("SUCCESS")) {
				Log.d(TAG, "Upload success. Done....");
				Utils.log(new Date(), TAG, "Upload success. Done....");
				File f = new File(this.path);
				f.delete();
			}
			return null;
		}	
	}

	private String post(String phoneID, String zipPath) {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(SERVER_URL);
			MultipartEntity multEntity = new MultipartEntity();
			BasicHttpResponse httpResponse = null;
			File zipFile = new File(zipPath);
			double zipLength = zipFile.length()/1024;
			Log.d(TAG, "Zip size: " + zipLength + "KB");
			Utils.log(new Date(), TAG, "Zip size: " + zipLength + "KB");
			multEntity.addPart("phone_id", new StringBody(phoneID));
			multEntity.addPart("tripFile", new FileBody(zipFile, "application/zip"));
			httpPost.setEntity(multEntity);
			Log.d(TAG, "Executing httpPost");
			Utils.log(new Date(), TAG, "Executing httpPost");
			httpResponse = (BasicHttpResponse) httpClient.execute(httpPost);
			Log.d(TAG, httpResponse.getStatusLine().toString()+", "+
					httpResponse.getProtocolVersion().toString());
			Utils.log(new Date(), TAG, httpResponse.getStatusLine().toString()+", "+
					httpResponse.getProtocolVersion().toString());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return "SUCCESS";
			}
			else {
				return "FAILURE";
			}
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
			Utils.log(new Date(), TAG, e.getMessage());
			e.printStackTrace();
			return "FAILURE";
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy(){
		releaseLocks();
		super.onDestroy();
	}

	private void releaseLocks(){
		wakeLock.release();
		wifiLock.release();
	}
}
