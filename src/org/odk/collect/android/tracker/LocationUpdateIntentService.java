package org.odk.collect.android.tracker;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import com.google.android.gms.location.LocationClient;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class LocationUpdateIntentService extends IntentService {
	
	private static final String TAG = "UpdateLocationIntentService";

	public LocationUpdateIntentService() {
		super("LocationUpdateIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		Location location = (Location) bundle.get(LocationClient.KEY_LOCATION_CHANGED);
		logUpdateLocation(location);
	}
	
	private void logUpdateLocation(Location location) {
		StringBuilder sb = new StringBuilder("{\"location\":{");
		sb.append("\"time\":").append(location.getTime()).append(",");
		sb.append("\"alt\":").append(location.getAltitude()).append(",");
		sb.append("\"lat\":").append(location.getLatitude()).append(",");
		sb.append("\"lon\":").append(location.getLongitude()).append(",");
		sb.append("\"speed\":").append(location.getSpeed()).append(",");
		sb.append("\"error\":").append(location.getAccuracy());
		sb.append("}}");
		
		String log = sb.toString();
		Log.d(TAG, "Location: " + log);
		File f = new File(Environment.getExternalStorageDirectory(),"Travel_Study/data.txt");
		try{ 
			if (!f.exists()) {
				f.createNewFile();
			}
			FileWriter out = new FileWriter(f,true);
			out.write(log + "\n");
			out.close();
		}catch (Exception e) {
			Utils.log(new Date(), TAG, e.getMessage());
			return;
		}
	}
}
