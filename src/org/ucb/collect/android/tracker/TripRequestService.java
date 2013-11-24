package org.ucb.collect.android.tracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;


public class TripRequestService extends Service {
	private static final String TAG = "TripRequestService";
	private static final String SERVER_URL= "http://TQS-LB-317129516.us-west-1.elb.amazonaws.com:61245/api/mqtrips";
	private static final String GOOGLE_URL= "http://maps.googleapis.com/maps/api/directions/json";

	private PowerManager.WakeLock wakeLock;
	private WifiManager.WifiLock wifiLock;

	private class TripModel {
		String tripId;
		String url;
		String mode;
		public TripModel(String tripId, String mode, String url) {
			this.tripId = tripId;
			this.url = url;
			this.mode = mode;
		}
	}

	@Override
	public void onCreate(){
		Log.d(TAG,"TripRequestService creates");
		Utils.log(new Date(), TAG, "TripRequestService creates");
		super.onCreate();

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TripRequestService");  
		wakeLock.acquire();

		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, "TripRequestService");
		wifiLock.acquire();
	}

	@Override
	public int onStartCommand(Intent in, int flags, int startId){
		switch (Utils.networkState(this)){
		case Utils.NO_CONNECTION:
			Log.d(TAG,"TripRequestService NO_CONNECTION");
			Utils.log(new Date(), TAG, "TripRequestService NO_CONNECTION");
			Utils.retryLater(this, RequestTripsReceiver.class, 3600);
			stopSelf();
			break;
		case Utils.WAIT_FOR_WIFI:
			Log.d(TAG, "TripRequestService WAIT_FOR_WIFI");
			Utils.log(new Date(), TAG, "TripRequestService WAIT_FOR_WIFI");
			Utils.retryLater(this, RequestTripsReceiver.class, 10);
			break;
		case Utils.HAS_CONNECTION:
			Log.d(TAG, "TripRequestService HAS_CONNECTION");
			Utils.log(new Date(), TAG, "TripRequestService HAS_CONNECTION");
			new RequestTrip().execute();
			stopSelf();
			break;
		default:
			stopSelf();
			break;
		}
		return START_STICKY;
	}

	private class RequestTrip extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			boolean needRetry = false;
			Context context = getApplicationContext();
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String phoneID = telephonyManager.getDeviceId();
			if (phoneID == null) {
				phoneID = "null";
			}
			Log.d(TAG, "PhoneId: " + phoneID);
			Utils.log(new Date(), TAG, "PhoneId: " + phoneID);
			String jsonContent = requestTrips(phoneID);
			ArrayList<TripModel> arr = null;
			if (jsonContent != null) {
				arr = parseTrips(jsonContent);
				needRetry = needRetry || false;
			}
			String path = null;
			if  (arr == null) {
				needRetry = needRetry || true;
			}
			else if (arr.size() != 0) {
				path = sendTripRequestToGoogleApi(arr);
				needRetry = needRetry || false;
			}
			else if (arr.size() == 0) {
				return null;
			}
			if (path == null) {
				needRetry = needRetry || true;
			}
			else {
				needRetry = needRetry || false;
			}
			
			if (!needRetry) {
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
			    Editor editor = pref.edit();
			    editor.putString("ZipPath", path);
			    editor.putBoolean("IsRequestTrip", true);
			    editor.commit();
				Intent postTrip = new Intent(context, PostTripReceiver.class);
				context.sendBroadcast(postTrip);
			}
			else {
				Utils.retryLater(context, RequestTripsReceiver.class, 3600);
			}
			return null;
		}	

		private String requestTrips(String phoneID) {
			try {
				HttpClient httpClient = new DefaultHttpClient();
				String param = "?phone_id=" + phoneID;
				String url = SERVER_URL + param;
				HttpGet httpGet = new HttpGet(url);
				BasicHttpResponse httpResponse = null;
				httpResponse = (BasicHttpResponse) httpClient.execute(httpGet);
				InputStream is = httpResponse.getEntity().getContent();
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				int nRead;
				byte[] data = new byte[1024];
				while ((nRead = is.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}
				buffer.flush();
				buffer.close();
				is.close();
				String jsonContent = new String(buffer.toByteArray());
				return jsonContent;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		private ArrayList<TripModel> parseTrips(String jsonContent) {
			String[] modes = {"driving", "walking", "bicycling", "transit"};
			ArrayList<TripModel> arr = new ArrayList<TripModel>();
			JSONParser parser = new JSONParser();
			try {
				JSONObject obj = (JSONObject) parser.parse(jsonContent);
				@SuppressWarnings("unchecked")
				Set<String> keys = obj.keySet();
				for (String k : keys) {
					JSONObject content = (JSONObject) obj.get(k);
					String origin = content.get("depart_lat") + "," + content.get("depart_lon");
					String destination = content.get("arrive_lat") + "," + content.get("arrive_lon");
					String departTime = content.get("depart_time").toString();

					for (String mode : modes) {
						StringBuilder url = new StringBuilder(GOOGLE_URL);
						url.append("?origin=").append(origin);
						url.append("&destination=").append(destination);
						url.append("&sensor=false");

                        //by Hoang
						url.append("&departure_time=").append(getFutureDate(Long.valueOf(departTime)*1000).getTimeInMillis()/1000);

						url.append("&mode=").append(mode);
						url.append("&alternatives=true");

						TripModel tm = new TripModel(k, mode, url.toString());
						arr.add(tm);
					}
				}
				return arr;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}

        /**
         * by Hoang. I.e. Input timestamp is Monday in the past and output is the next Monday of now.
         * @param t in millis
         * @return date of next week of current time
         */
        GregorianCalendar today = new GregorianCalendar();
        public GregorianCalendar getFutureDate(long t){
            today.setTime(new Date());

            GregorianCalendar g = new GregorianCalendar();
            g.setTime(new Date(t));

            if (g.after(today)){
                return g;
            }

            g.add(Calendar.DAY_OF_YEAR,7);
            return getFutureDate(g.getTimeInMillis());
        }

		private String sendTripRequestToGoogleApi(ArrayList<TripModel> arr) {
			ArrayList<File> files = new ArrayList<File>();
			HttpClient httpClient = new DefaultHttpClient();
			String directory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Travel_Study/";
			try {
				for (TripModel tm : arr) {
					String fileName = directory + "trip_" + tm.tripId + "_" + tm.mode + ".json";
					File trip = new File(fileName);
					FileOutputStream out = new FileOutputStream(trip);
					HttpGet get = new HttpGet(tm.url);
					BasicHttpResponse resp = null;
					resp = (BasicHttpResponse) httpClient.execute(get);
					InputStream is = resp.getEntity().getContent();
					int nRead;
					byte[] data = new byte[1024];
					while ((nRead = is.read(data, 0, data.length)) != -1) {
						out.write(data, 0, nRead);
					}
					out.flush();
					out.close();
					is.close();
					files.add(trip);
					Thread.sleep(1500);
				}
				String path = zipFile(files);
				for (File f : files) {
					f.delete();
				}
				return path;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		private String zipFile(ArrayList<File> files) {
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Travel_Study/trips_" + System.currentTimeMillis() + ".zip";
			try {
				ZipFile zipFile = new ZipFile(path);
				ZipParameters parameters = new ZipParameters();
				parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
				parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); 
				zipFile.addFiles(files, parameters);
				return path;
			} catch (Exception e) {
				Utils.log(new Date(), TAG, e.getMessage());
				e.printStackTrace();
			}
			return null;
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
