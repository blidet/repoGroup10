package eda397.group10.pojo.notifications;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.SharedPreferences;
import android.util.Log;
import eda397.group10.communication.GithubRequest;
import eda397.group10.communication.JsonExtractor;
import eda397.group10.communication.JsonObjectExtractor;
import eda397.group10.database.PathDataBase;
import eda397.group10.navigator.AuthenticatedMainActivity;
import eda397.group10.navigator.R;
import eda397.group10.pojo.NotificationPOJO;

public class PushEvent extends NotificationPOJO {

	public PushEvent(JSONObject input, Service context) throws JSONException {
		super(input, context);
		
		/**
		 * The sha of the first commit in the push.
		 */
		String sha = "";
		
		JSONObject actor = input.getJSONObject("actor");
		JSONObject repo = input.getJSONObject("repo");
		JSONObject payload = input.getJSONObject("payload");
		
		String title = actor.getString("login") + " pushed to " + repo.getString("name");
		JSONArray commits = payload.getJSONArray("commits");
		String text = commits.getJSONObject(0).getString("message"); //not sure if it should be first or last commit
		
		for(int i = 0; i < commits.length(); ++i){
			sha = commits.getJSONObject(i).getString("sha");
			Log.println(Log.DEBUG, "Push event", "Sha " + i + ": " + sha);
		}
		
		setTitle(title);
		setText(text);
		setExpandedText(text);
		
		setLight(NotificationPOJO.LEDColor.GREEN);
		
		String action = context.getResources().getString(R.string.REPO_NEWS_ACTION);
		String repoName = repo.getString("name");
		setTarget(AuthenticatedMainActivity.class, context, action, repoName);
		
		SharedPreferences sh_Pref = notificationContext.getSharedPreferences(notificationContext.getResources().getString(R.string.LOGIN_CREDENTIALS_PREFERENCE_NAME),0);
		boolean authenticated = sh_Pref.getBoolean(notificationContext.getResources().getString(R.string.AUTH_PREFERENCE), false);
		
		if (authenticated) {
			String userName= sh_Pref.getString(notificationContext.getResources().getString(R.string.USERNAME_PREFERENCE), "");
			String password = sh_Pref.getString(notificationContext.getResources().getString(R.string.PASSWORD_PREFERENCE), "");
			//Create a Header with the username and password saved in "Shared Preferences":
			Header header = BasicScheme.authenticate(
					new UsernamePasswordCredentials(userName, password),
					HTTP.UTF_8, false);
			String url = "https://api.github.com/repos/"+repoName+"/git/trees/" + sha;
			Log.println(Log.DEBUG, "push event sha url", url);
			@SuppressWarnings("unused")
			ShaContentRetreiver scr = new ShaContentRetreiver(url, header);
		}else{
			Log.println(Log.ERROR, "Push event", "User not authenticated. Unable to request data from GitHub");
		}
	}
	
	/**
	 * Makes a call to the github API to see retrieve the changed files of a sha.
	 */
	private class ShaContentRetreiver extends GithubRequest {
		public ShaContentRetreiver(String url, Header... header) {
			super(url, header);
		}

		@Override
		public void onPostExecute(HttpResponse result) {
			Integer statusCode = result.getStatusLine().getStatusCode();

			if (statusCode==200) {
				NotificationConflictChecker notificationBuilder = new NotificationConflictChecker();
				notificationBuilder.execute(result);
			} else {
				//TODO: take care of other status codes
			}
		}
	}

	private class NotificationConflictChecker extends JsonObjectExtractor {
		@Override
		public void onPostExecute(JSONObject json) {
			
			//======= Variables =======
			
			JSONArray tree = new JSONArray();
			boolean isConflict = false;
			PathDataBase db = PathDataBase.getInstance(notificationContext);
			
			//===== Functionality =====
			
			try {
				tree = json.getJSONArray("tree");
				db.open();
				
				for(int i = 0; i < tree.length(); ++i){
					JSONObject file = (JSONObject)tree.get(i);
					String url = file.getString("url");
					isConflict = PathDataBase.getInstance(notificationContext).findPath(url);
					if(isConflict){
						Log.println(Log.DEBUG, "Notification checker", "Found conflicting file: " + url);
						break;
					}
				}
				db.close();
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			BuildNotification();
		}
	}

}
