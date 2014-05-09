package eda397.group10.pojo.notifications;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.util.Log;
import eda397.group10.navigator.AuthenticatedMainActivity;
import eda397.group10.navigator.R;
import eda397.group10.pojo.NotificationPOJO;

public class CommitCommentEvent extends NotificationPOJO{

	public CommitCommentEvent(JSONObject input, Service context) throws JSONException {
		super(input, context);
		JSONObject repo = input.getJSONObject("repo");
		JSONObject payload = input.getJSONObject("payload");
		JSONObject comment = payload.getJSONObject("comment");
		String text = comment.getString("body");
		
		setTitle("New comment");
		setText(text);
		setExpandedText(text);

		setLight(NotificationPOJO.LEDColor.WHITE);
		
		String action = context.getResources().getString(R.string.REPO_NEWS_ACTION);
		String repoName = repo.getString("name");
		setTarget(AuthenticatedMainActivity.class, context, action, repoName);
		
		BuildNotification();
	}

}
