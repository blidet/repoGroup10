package eda397.group10.pojo.notifications;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import eda397.group10.navigator.AuthenticatedMainActivity;
import eda397.group10.navigator.R;
import eda397.group10.pojo.NotificationPOJO;

public class PushEvent extends NotificationPOJO {

	public PushEvent(JSONObject input, Service context) throws JSONException {
		super(input, context);
		
		JSONObject actor = input.getJSONObject("actor");
		JSONObject repo = input.getJSONObject("repo");
		JSONObject payload = input.getJSONObject("payload");
		
		String title = actor.getString("login") + " pushed to " + repo.getString("name");
		JSONArray commits = payload.getJSONArray("commits");
		String text = commits.getJSONObject(0).getString("message"); //not sure if it should be first or last commit
		
		setTitle(title);
		setText(text);
		setExpandedText(text);
		
		setLight(NotificationPOJO.LEDColor.GREEN);
		
		String action = context.getResources().getString(R.string.REPO_NEWS_ACTION);
		String repoName = repo.getString("name");
		setTarget(AuthenticatedMainActivity.class, context, action, repoName);
	}

}
