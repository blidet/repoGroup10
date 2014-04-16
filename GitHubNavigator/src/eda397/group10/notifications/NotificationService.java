package eda397.group10.notifications;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;

import eda397.group10.communication.Constants;
import eda397.group10.communication.GithubRequest;
import eda397.group10.navigator.MainActivity;
import eda397.group10.navigator.ProjectPageActivity;
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
       
		SharedPreferences sh_Pref = getSharedPreferences(Constants.LOGIN_CREDENTIALS_PREFERENCE_NAME,0);
        boolean authenticated = sh_Pref.getBoolean(Constants.AUTH_PREFERENCE, false);
        
        if (authenticated) {
        	//Create a Header with the username and password saved in "Shared Preferences":
        	Header header = BasicScheme.authenticate(
                    new UsernamePasswordCredentials(sh_Pref.getString(Constants.USERNAME_PREFERENCE, ""), 
                    		sh_Pref.getString(Constants.PASSWORD_PREFERENCE, "")),
                    HTTP.UTF_8, false);
        	//Send HTTP request to poll for updates (in a new thread):
        	Log.println(Log.ASSERT, "get notifications", "..starting poller...");
    		PollTask poller = new PollTask(Constants.POLL_UPDATES_URL, header);
        } else {
        	//If you are not logged going back to login page
        	Intent mainIntent = new Intent(this, MainActivity.class);
        	startActivity(mainIntent);
        }
    }
    
    
    private class PollTask extends GithubRequest {
		public PollTask(String url, Header header) {
			super(url, header);
		}
		
		@Override
    	public void onPostExecute(HttpResponse result) {
			Integer statusCode = result.getStatusLine().getStatusCode();
			Log.println(Log.ASSERT, "get notifications", "status code: "+statusCode+" NOTIFICATION");
			
			createNotification();
			stopSelf();
		}
	}
    
    public void onDestroy() {
        super.onDestroy();
    }
    
    private void createNotification() {
    	NotificationCompat.Builder mBuilder =
    	        new NotificationCompat.Builder(this)
    	        .setSmallIcon(R.drawable.news72)
    	        .setContentTitle("My notification")
    	        .setContentText("Hello World!");
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
    	// mId allows you to update the notification later on.
    	int mId = 1; 
    	mNotificationManager.notify(mId, mBuilder.build());
    }
}
