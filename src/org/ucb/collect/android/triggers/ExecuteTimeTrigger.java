package org.ucb.collect.android.triggers;

import java.util.Calendar;

import org.ucb.collect.android.R;
import org.ucb.collect.android.activities.FormChooserList;
import org.ucb.collect.android.provider.FormsProviderAPI.FormsColumns;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


public class ExecuteTimeTrigger extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		int icon = R.drawable.survey_alert;
		CharSequence tickerText = "A friendly survey reminder";
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;
		
		CharSequence contentTitle = "A friendly survey reminder";
		String form = intent.getExtras().getString("form");
		CharSequence contentText = "Please fill out survey: "+form;
			
		Intent notificationIntent = getFormEntryIntent(context,form);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(form.hashCode(), notification);
		Log.i("t","ExecuteTimeReceived for form "+form+" at "+Calendar.getInstance());
	}
	
	//consider default form?
	static Intent getFormEntryIntent(Context context, String s){
		Intent intent = new Intent(context, FormChooserList.class);
		String where=FormsColumns.JR_FORM_ID+" = '"+s+"'";
		//make sure cursor is close
		Cursor c = null;
		try{
			ContentResolver cr = context.getContentResolver();
		    c = cr.query(FormsColumns.CONTENT_URI, null,where, null, null);
		    if (c.moveToFirst()){
		    	long idFormsTable = c.getLong(c.getColumnIndex("_id"));
		    	Uri formUri = ContentUris.withAppendedId(FormsColumns.CONTENT_URI, idFormsTable);
		    	intent = new Intent(Intent.ACTION_EDIT, formUri);
		    }
		} catch (Exception e){
			
		} finally { 
			c.close();
		}
		
	    return intent;
	}
}
