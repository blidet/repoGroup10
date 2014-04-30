package eda397.group10.pojo;

import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import eda397.group10.navigator.MainActivity;
import eda397.group10.navigator.R;

@SuppressLint("NewApi")
public class NotificationPOJO {
	// notificationId allows you to update the notification later on.
	private int notificationId;
	private String text;
	
	/**
	 * Creates the actual notifications shown to the user.
	 * @param input
	 * @param context
	 */
	public NotificationPOJO(JSONObject input, Service context)  {
		try {
			notificationId = input.getInt("id");
			JSONObject subject = input.getJSONObject("subject");
			text = subject.getString("type") + ": " + subject.getString("title");

			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.news72)
			.setContentTitle("Id: " + notificationId)
			.setContentText(text);
			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(context, MainActivity.class);

			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			
			// Adds the back stack for the Intent (but not the Intent itself)
			//stackBuilder.addParentStack(MainActivity.class);
			
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			
			PendingIntent resultPendingIntent =
					stackBuilder.getPendingIntent(
							0,
							PendingIntent.FLAG_UPDATE_CURRENT
							);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager notificationManager =
					(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			notificationManager.notify(notificationId, mBuilder.build());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
