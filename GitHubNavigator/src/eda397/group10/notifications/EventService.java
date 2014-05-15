package eda397.group10.notifications;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import eda397.group10.communication.GithubRequest;
import eda397.group10.communication.JsonExtractor;
import eda397.group10.navigator.R;
import eda397.group10.pojo.notifications.*;
import eda397.group10.utils.CalendarUtil;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

@SuppressLint("SimpleDateFormat") public class EventService extends Service {
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	/**
	 * The default vibration length.
	 */
	public static final int DEFAULT_VIBRATION_MS = 1000;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		handleIntent(intent);
		return START_NOT_STICKY;
	}
	
	private void handleIntent(Intent intent) { 
		SharedPreferences sh_Pref = getSharedPreferences(getResources().getString(R.string.LOGIN_CREDENTIALS_PREFERENCE_NAME),0);
		boolean authenticated = sh_Pref.getBoolean(getResources().getString(R.string.AUTH_PREFERENCE), false);
		
		if (authenticated) {
			String userName= sh_Pref.getString(getResources().getString(R.string.USERNAME_PREFERENCE), "");
			String password = sh_Pref.getString(getResources().getString(R.string.PASSWORD_PREFERENCE), "");
			//Create a Header with the username and password saved in "Shared Preferences":
			Header header = BasicScheme.authenticate(
					new UsernamePasswordCredentials(userName, password),
					HTTP.UTF_8, false);
			//Send HTTP request to poll for updates (in a new thread):
			@SuppressWarnings("unused")
			PollTask poller = new PollTask("https://api.github.com/users/"+userName+"/received_events", header);
		} else {
			//If you are not logged going back to login page
			//TODO causes crash
			//http://stackoverflow.com/questions/3689581/calling-startactivity-from-outside-of-an-activity
//			Intent mainIntent = new Intent(this, MainActivity.class);
	//		startActivity(mainIntent);
		}
	}

	/**
	 * Makes a call to the github API to see if there are any new events
	 */
	private class PollTask extends GithubRequest {
		public PollTask(String url, Header... header) {
			super(url, header);
		}

		@Override
		public void onPostExecute(HttpResponse result) {
			Integer statusCode = result.getStatusLine().getStatusCode();

			if (statusCode==200) {
				NotificationBuilder notificationBuilder = new NotificationBuilder();
				notificationBuilder.execute(result);
			} else {
				//TODO: take care of other status codes
			}			

			stopSelf();
		}
	}

	/**
	 * Extracts data from the returned JSONArray and creates notifications
	 */
	private class NotificationBuilder extends JsonExtractor {
		@Override
		public void onPostExecute(JSONArray json) {
			try {
				SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.SETTINGS_PREFERENCES),0);
				String lastPoll = settings.getString(getResources().getString(R.string.LAST_POLL), "");
				GregorianCalendar timeline = CalendarUtil.convertToCalendar(lastPoll);
								
				for (int i = 0; i < json.length(); i++) {
					String created_at = json.getJSONObject(i).getString("created_at");
					GregorianCalendar createdAt = CalendarUtil.convertToCalendar(created_at);

					if (createdAt.after(timeline)) {
						
						/**
						 * Vibrate for 500 milliseconds when you get a valid notification.
						 */ 
						AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
						SharedPreferences settingsPrefs = getSharedPreferences(getResources().getString(R.string.SETTINGS_PREFERENCES),0);
						int vibrationValue = settingsPrefs.getInt(getResources().getString(R.string.VIBRATION_VALUE_SELECTED), 0);
						//vibrationValue == 0 : vibration ON
						//TODO: change to string or boolean
					    if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT && vibrationValue==0) {
					    	Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
							v.vibrate(DEFAULT_VIBRATION_MS);
					    }
						
						String eventType = json.getJSONObject(i).getString("type");
						switch(eventType) {
						case "PushEvent":
							new PushEvent(json.getJSONObject(i), EventService.this);	
							break;
						case "IssuesEvent":
							new IssuesEvent(json.getJSONObject(i), EventService.this);
							break;
						case "IssueCommentEvent":
							new IssueCommentEvent(json.getJSONObject(i), EventService.this);
							break;
						case "CommitCommentEvent":
							new CommitCommentEvent(json.getJSONObject(i), EventService.this);
							break;
						//default:
							//new NotificationPOJO(json.getJSONObject(i), EventService.this);	
						}
					}
				}
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
				
	        	Editor toEdit = settings.edit();
	        	toEdit.putString(getResources().getString(R.string.LAST_POLL), dateFormat.format(cal.getTime()));
	        	toEdit.commit();
	        	Log.println(Log.ASSERT, "Getting updates", settings.getString(getResources().getString(R.string.LAST_POLL), "defValue"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void onDestroy() {
		super.onDestroy();
	}
}
