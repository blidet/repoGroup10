package eda397.group10.notifications;

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
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

/**
 * This class polls the github API for updates/notifications in the user's repos.
 *
 */
@SuppressLint("NewApi")
public class NotificationService extends Service {

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
        	//Create a Header with the username and password saved in "Shared Preferences":
        	Header header = BasicScheme.authenticate(
                    new UsernamePasswordCredentials(sh_Pref.getString(getResources().getString(R.string.USERNAME_PREFERENCE), ""), 
                    		sh_Pref.getString(getResources().getString(R.string.PASSWORD_PREFERENCE), "")),
                    HTTP.UTF_8, false);
        	//Send HTTP request to poll for updates (in a new thread):
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

			if (statusCode==200) {
				NotificationBuilder jsonExtractor = new NotificationBuilder();
				jsonExtractor.execute(result);
			} else {
				//TODO: take care of other status codes
			}			
			
			stopSelf();
		}
	}
    
    /**
     * Extracts data from the returned JSONArray
     */
    private class NotificationBuilder extends JsonExtractor {
    	@Override
    	public void onPostExecute(JSONArray json) {
			try {
				for (int i = 0; i < json.length(); i++) {
					new NotificationPOJO(json.getJSONObject(i), NotificationService.this);
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
