package eda397.group10.notifications;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eda397.group10.communication.GithubRequest;
import eda397.group10.communication.JsonExtractor;
import eda397.group10.navigator.MainActivity;
import eda397.group10.navigator.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * This class polls the github API for updates/notifications in the user's repos.
 *
 */
public class NotificationService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.println(Log.ASSERT, "get notifications", ".........");
        handleIntent(intent);
        return START_NOT_STICKY;
    }
    
    private void handleIntent(Intent intent) {
    	Log.println(Log.ASSERT, "get notifications", "...handle intent...");
       
		SharedPreferences sh_Pref = getSharedPreferences(getResources().getString(R.string.LOGIN_CREDENTIALS_PREFERENCE_NAME),0);
        boolean authenticated = sh_Pref.getBoolean(getResources().getString(R.string.AUTH_PREFERENCE), false);
        
        if (authenticated) {
        	//Create a Header with the username and password saved in "Shared Preferences":
        	Header header = BasicScheme.authenticate(
                    new UsernamePasswordCredentials(sh_Pref.getString(getResources().getString(R.string.USERNAME_PREFERENCE), ""), 
                    		sh_Pref.getString(getResources().getString(R.string.PASSWORD_PREFERENCE), "")),
                    HTTP.UTF_8, false);
        	//Send HTTP request to poll for updates (in a new thread):
        	Log.println(Log.ASSERT, "get notifications", "..starting poller...");
    		PollTask poller = new PollTask(getResources().getString(R.string.FETCH_NOTIFICATIONS_URL), header);
        } else {
        	//If you are not logged going back to login page
        	Intent mainIntent = new Intent(this, MainActivity.class);
        	startActivity(mainIntent);
        }
    }
    
    
    /**
     * Makes a call to the github API to see if there are any new notifications
     */
    private class PollTask extends GithubRequest {
		public PollTask(String url, Header header) {
			super(url, header);
		}
		
		@Override
    	public void onPostExecute(HttpResponse result) {
			Integer statusCode = result.getStatusLine().getStatusCode();
			Log.println(Log.ASSERT, "get notifications", "status code: "+statusCode+" NOTIFICATION");
			
			NotificationBuilder jsonExtractor = new NotificationBuilder();
			jsonExtractor.execute(result);
			
			stopSelf();
		}
	}
    
    /**
     * Extracts data from the returned JSONArray
     */
    private class NotificationBuilder extends JsonExtractor {
    	@Override
    	public void onPostExecute(JSONArray json) {
    		Log.println(Log.ASSERT, "Notification builder.....", json.toString());

			try {
				for (int i = 0; i < json.length(); i++) {
					createNotification(json.getJSONObject(i));	
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
    
    /**
     * Creates the actual notifications shown to the user.
     * TODO: move to separate class.
     * @param input
     * @throws JSONException
     */
    private void createNotification(JSONObject input) throws JSONException {
    	// mId allows you to update the notification later on.
    	int mId = input.getInt("id");
    	JSONObject subject = input.getJSONObject("subject");
    	String text = subject.getString("type") + ": " + subject.getString("title");
    	
    	NotificationCompat.Builder mBuilder =
    	        new NotificationCompat.Builder(this)
    	        .setSmallIcon(R.drawable.news72)
    	        .setContentTitle("Id: " + mId)
    	        .setContentText(text);
    	// Creates an explicit intent for an Activity in your app
    	Intent resultIntent = new Intent(this, MainActivity.class);

    	// The stack builder object will contain an artificial back stack for the
    	// started Activity.
    	// This ensures that navigating backward from the Activity leads out of
    	// your application to the Home screen.
    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
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
    	NotificationManager mNotificationManager =
    	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	mNotificationManager.notify(mId, mBuilder.build());
    }
}
