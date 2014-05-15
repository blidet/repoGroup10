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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.util.Log;
import eda397.group10.navigator.AuthenticatedMainActivity;
import eda397.group10.navigator.R;

@SuppressLint("NewApi")
public class NotificationPOJO {
	
	public enum LEDColor {WHITE, GREEN, RED, NONE};
	
	private NotificationCompat.Builder notificationBuilder;
	private NotificationManager notificationManager;
	
	/**
	 * The context that handles this notification.
	 */
	protected Context notificationContext;
	
	/**
	 * The default on and off interval for the notification light.
	 */
	public static final int DEFAULT_NOTIFICATION_LIGHT_ON_MS = 2000;
	public static final int DEFAULT_NOTIFICATION_LIGHT_OFF_MS = 1000;

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

			notificationContext = context;

			//TODO: only part of the title is visible
			//Design the notification
			notificationBuilder =
					new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.git_logo)
			.setContentTitle(title)
			.setContentText(text)
			.setAutoCancel(true);

			//set the notification icon
			new URLRequester().execute(actor.getString("avatar_url"));

			setTarget(AuthenticatedMainActivity.class, context, context.getResources().getString(R.string.NEWS_ACTION));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/**
	 * Builds/Fires the notification.
	 */
	protected void BuildNotification(){
		
		/**
		 * Makes sure that there is a context connected with this notification.
		 */
		if(notificationContext == null){
			Log.println(Log.ERROR, "Notification", "Context of notification not specified.");
			return;
		}
		
		notificationManager =
				(NotificationManager) notificationContext.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(notificationId, notificationBuilder.build());
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
		//notificationManager.notify(notificationId, notificationBuilder.build());
	}
	
	/**
	 * Sets the title of the notification.
	 * @param title
	 */
	protected void setTitle(String title) {
		notificationBuilder.setContentTitle(Html.fromHtml(title));
		//notificationManager.notify(notificationId, notificationBuilder.build());
	}
	
	/**
	 * Sets the content text of the notification.
	 * @param text
	 */
	protected void setText(String text) {
		notificationBuilder.setContentText(Html.fromHtml(text));
		//notificationManager.notify(notificationId, notificationBuilder.build());
	}
	
	/**
	 * Applies a notification light with the color argb.
	 * 
	 * @param argb
	 */
	protected void setLight(LEDColor color) {
		SharedPreferences settingsPrefs = notificationContext.getSharedPreferences(notificationContext.getString(R.string.SETTINGS_PREFERENCES),0);
		int lightValue = settingsPrefs.getInt(notificationContext.getResources().getString(R.string.LED_LIGHT_VALUE_SELECTED), 0);
		//lightvalue 0 = lights on
		//TODO: change to string or boolean
		
		if (lightValue==0) {
			int hexColorCode = 0xFFFFFFFF;
			
			switch(color){
				case WHITE : hexColorCode = 0xFFFFFFFF; break;
				case GREEN : hexColorCode = 0xFF00CD00; break;
				case RED : hexColorCode = 0xFFff0000; break;
				case NONE :return;
				default : return;
			}
			notificationBuilder.setLights(hexColorCode, DEFAULT_NOTIFICATION_LIGHT_ON_MS, DEFAULT_NOTIFICATION_LIGHT_OFF_MS);
		}
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
		.bigText(Html.fromHtml(text));
		notificationBuilder.setStyle(bigStyle);
		//notificationManager.notify(notificationId, notificationBuilder.build());
	}
	
	protected void setTarget(Class<?> target, Service context, String action) {
		setTarget(target, context, action, "");
	}
	
	protected void setTarget(Class<?> target, Service context, String action, String repoName) {
		// Creates an explicit intent for an Activity in your app
					Intent resultIntent = new Intent(context, target);
					
					//Set the action for the intent. This will decide which fragment will be opened 
					//when the user clicks the notification.
					resultIntent.setAction(Intent.ACTION_RUN);
					resultIntent.putExtra("ACTION", action);
					resultIntent.putExtra("REPONAME", repoName);

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
	}
}
