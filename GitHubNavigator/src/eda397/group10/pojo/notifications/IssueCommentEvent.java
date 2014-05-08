package eda397.group10.pojo.notifications;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import eda397.group10.navigator.AuthenticatedMainActivity;
import eda397.group10.navigator.MainActivity;
import eda397.group10.navigator.R;
import eda397.group10.navigator.SettingsFragment;
import eda397.group10.pojo.NotificationPOJO;

public class IssueCommentEvent extends NotificationPOJO {

	public IssueCommentEvent(JSONObject input, Service context) throws JSONException {
		super(input, context);
		
		JSONObject repo = input.getJSONObject("repo");
		JSONObject payload = input.getJSONObject("payload");

		JSONObject issue = payload.getJSONObject("issue");
		JSONObject comment = payload.getJSONObject("comment");
		
		setTitle(issue.getString("title"));
		setText(comment.getString("body"));
		
		setLight(NotificationPOJO.LEDColor.WHITE);
		
		String action = context.getResources().getString(R.string.REPO_NEWS_ACTION);
		String repoName = repo.getString("name");
		setTarget(AuthenticatedMainActivity.class, context, action, repoName);
	}

}
