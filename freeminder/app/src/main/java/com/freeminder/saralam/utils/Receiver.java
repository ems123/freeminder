
package com.freeminder.saralam.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.freeminder.saralam.MainActivity;
import com.freeminder.saralam.R;
import com.parse.ParseBroadcastReceiver;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


/**
 * Created by Sasikumar Reddy on 31-08-2015.
 */

public class Receiver extends ParseBroadcastReceiver {

	private final String TAG = "Parse Notification";
	private String msg = "";


    @Override
    public void onReceive(Context context, Intent intent) {
		Log.d(TAG,"Push notification received!!");
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }



	/*

		@Override
		public void onReceive(Context ctx, Intent intent) {
			Log.i(TAG, "PUSH RECEIVED!!!");

			try {
				String action = intent.getAction();
				String channel = intent.getExtras().getString("com.parse.Channel");
				JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

				Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
				Iterator itr = json.keys();
				while (itr.hasNext()) {
					String key = (String) itr.next();
					Log.d(TAG, "..." + key + " => " + json.getString(key));
					if(key.equals("string")){
						msg = json.getString(key);
					}
				}
			} catch (JSONException e) {
				Log.d(TAG, "JSONException: " + e.getMessage());
			}


			Bitmap icon = BitmapFactory.decodeResource(ctx.getResources(),
					R.drawable.ic_launcher);

			Intent launchActivity = new Intent(ctx, MainActivity.class);
			PendingIntent pi = PendingIntent.getActivity(ctx, 0, launchActivity, PendingIntent.FLAG_UPDATE_CURRENT);

			Notification noti = new NotificationCompat.Builder(ctx)
					.setContentTitle("PUSH RECEIVED")
					.setContentText(msg)
					.setSmallIcon(R.drawable.ic_launcher)
					.setLargeIcon(icon)
					.setContentIntent(pi)
					.setAutoCancel(true)
					.build();

			NotificationManager nm = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(0, noti);

		}*/

}

