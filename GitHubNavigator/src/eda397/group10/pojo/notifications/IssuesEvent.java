package eda397.group10.pojo.notifications;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import eda397.group10.navigator.AuthenticatedMainActivity;
import eda397.group10.navigator.R;
import eda397.group10.pojo.NotificationPOJO;

public class IssuesEvent extends NotificationPOJO {

	public IssuesEvent(JSONObject input, Service context) throws JSONException {
		super(input, context);

		JSONObject repo = input.getJSONObject("repo");
		JSONObject payload = input.getJSONObject("payload");
		JSONObject issue = payload.getJSONObject("issue");
		String action = payload.getString("action");
		String title = "";
		String text = issue.getString("title");
		
		switch(action) {
		case "opened":
			title = "New issue opened";
			setExpandedText(text + "\n\n" + issue.getString("body"));
			break;
		case "reopened":
			title = "Issue reopened";
			break;
		case "closed":
			title = "Issue closed";
			break;
		}
		
		setText(text);
		setTitle(title);
		
		setLight(NotificationPOJO.LEDColor.WHITE);
		
		String clickAction = context.getResources().getString(R.string.REPO_NEWS_ACTION);
		String repoName = repo.getString("name");
		setTarget(AuthenticatedMainActivity.class, context, clickAction, repoName);
	}

}
