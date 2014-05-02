package eda397.group10.pojo.notifications;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import eda397.group10.pojo.NotificationPOJO;

public class IssueCommentEvent extends NotificationPOJO {

	public IssueCommentEvent(JSONObject input, Service context) throws JSONException {
		super(input, context);
		
		JSONObject payload = input.getJSONObject("payload");

		JSONObject issue = payload.getJSONObject("issue");
		JSONObject comment = payload.getJSONObject("comment");
		
		setTitle(issue.getString("title"));
		setText(comment.getString("body"));
	}

}
