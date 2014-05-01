package eda397.group10.notifications;

import java.util.GregorianCalendar;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;

import eda397.group10.communication.GithubRequest;
import eda397.group10.communication.JsonExtractor;
import eda397.group10.navigator.MainActivity;
import eda397.group10.navigator.R;
import eda397.group10.pojo.NotificationPOJO;
import eda397.group10.pojo.notifications.*;
import eda397.group10.utils.CalendarUtil;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class EventService extends Service {
	GregorianCalendar timeline = CalendarUtil.convertToCalendar("2014-04-15T17:26:27Z");
	GregorianCalendar timeline_temp = CalendarUtil.convertToCalendar("2014-04-20T17:26:27Z");
	//TODO: fix timeline

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
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
			Intent mainIntent = new Intent(this, MainActivity.class);
			startActivity(mainIntent);
		}
	}

	/**
	 * Makes a call to the github API to see if there are any new events
	 */
	private class PollTask extends GithubRequest {
		public PollTask(String url, Header header) {
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
				for (int i = 0; i < json.length(); i++) {
					String created_at = json.getJSONObject(i).getString("created_at");
					GregorianCalendar createdAt = CalendarUtil.convertToCalendar(created_at);

					if (createdAt.after(timeline) && createdAt.before(timeline_temp)) {
						//TODO: this timeline is only for testing purposes

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
						default:
							new NotificationPOJO(json.getJSONObject(i), EventService.this);	
							break;
						}
					}
				}
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
