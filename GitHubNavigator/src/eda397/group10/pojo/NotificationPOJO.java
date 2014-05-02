package eda397.group10.pojo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import eda397.group10.navigator.MainActivity;
import eda397.group10.navigator.R;

@SuppressLint("NewApi")
public class NotificationPOJO {
	private NotificationCompat.Builder notificationBuilder;
	private NotificationManager notificationManager;

	// notificationId allows the notification to be updated later on.
	private int notificationId;

	/**
	 * Creates the actual notifications shown to the user.
	 * @param input
	 * @param context
	 */
	public NotificationPOJO(JSONObject input, Service context)  {
		try {
			notificationId = input.getInt("id");
			String text = input.getString("created_at");
			String title = "";
			JSONObject actor = input.getJSONObject("actor");



			//TODO: only part of the title is visible
			//Design the notification
			notificationBuilder =
					new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.git_logo)
			.setContentTitle(title)
			.setContentText(text);

			//set the notification icon
			new URLRequester().execute(actor.getString("avatar_url"));


			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(context, MainActivity.class);

			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			//TODO: fix proper back stack
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
			notificationBuilder.setContentIntent(resultPendingIntent);
			notificationManager =
					(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			notificationManager.notify(notificationId, notificationBuilder.build());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	/**
	 * Converts a URL to an icon for the notification.
	 *
	 */
	private class URLRequester extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... URLs) {
			try {
				URL url = new URL(URLs[0]);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				return myBitmap;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			NotificationPOJO.this.setIcon(result);
		}

	}

	/**
	 * Updates the large icon of this notification.
	 * @param image
	 */
	protected void setIcon(Bitmap image) {
		notificationBuilder.setLargeIcon(image);
		notificationManager.notify(notificationId, notificationBuilder.build());
	}
	
	/**
	 * Sets the title of the notification.
	 * @param title
	 */
	protected void setTitle(String title) {
		notificationBuilder.setContentTitle(title);
		notificationManager.notify(notificationId, notificationBuilder.build());
	}
	
	/**
	 * Sets the content text of the notification.
	 * @param text
	 */
	protected void setText(String text) {
		notificationBuilder.setContentText(text);
		notificationManager.notify(notificationId, notificationBuilder.build());
	}
	
	/**
	 * Makes the notification expandable (only available on Android 4.1 and later)
	 * and sets the text to be shown in the expanded view.
	 * @param text the text to be shown in the expanded view
	 */
	protected void setExpandedText(String text) {
		//Creates the expanded view of the notification (only on Android 4.1 and later)
		NotificationCompat.BigTextStyle bigStyle =
				new NotificationCompat.BigTextStyle()
		.bigText(text);
		notificationBuilder.setStyle(bigStyle);
		notificationManager.notify(notificationId, notificationBuilder.build());
	}
}
