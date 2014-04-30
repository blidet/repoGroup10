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
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import eda397.group10.navigator.MainActivity;
import eda397.group10.navigator.R;

@SuppressLint("NewApi")
public class NotificationPOJO {
	private NotificationCompat.Builder notificationBuilder;
	private NotificationManager notificationManager;
	
	// notificationId allows you to update the notification later on.
	private int notificationId;
	
	/**
	 * Creates the actual notifications shown to the user.
	 * @param input
	 * @param context
	 */
	public NotificationPOJO(JSONObject input, Service context)  {
		try {
			notificationId = input.getInt("id");
			JSONObject subject = input.getJSONObject("subject");
			String text = subject.getString("type") + ": " + subject.getString("title");
			JSONObject repository = input.getJSONObject("repository");
			JSONObject owner = repository.getJSONObject("owner");
			new URLRequester().execute(owner.getString("avatar_url"));
			
			String reason = input.getString("reason");
			switch(reason) {
			case "subscribed":
				reason = "You subscribed to an issue";
				break;
			case "comment":
				reason ="New comment";
				break;
			}
			
			notificationBuilder =
					new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.git_logo)
			.setContentTitle(reason)
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
	         NotificationPOJO.this.setImage(result);
	     }
		
	}
	
	/**
	 * Updates the large icon of this notification.
	 * @param image
	 */
	private void setImage(Bitmap image) {
		notificationBuilder.setLargeIcon(image);
		notificationManager.notify(notificationId, notificationBuilder.build());
	}
}
