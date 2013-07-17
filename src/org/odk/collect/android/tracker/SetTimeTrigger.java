package org.odk.collect.android.tracker;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


//Set alarm notification for today only
public class SetTimeTrigger extends BroadcastReceiver{
	private static final String TAG = "SetTimeTrigger";
	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG,"SetTimeReceived starts");
		Utils.log(new Date(), TAG, "SetTimeReceived starts");
		context.startService(new Intent(context,UploadDataService.class));
	}
}
