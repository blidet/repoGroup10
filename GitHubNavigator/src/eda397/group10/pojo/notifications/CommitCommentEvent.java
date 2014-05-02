package eda397.group10.pojo.notifications;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import eda397.group10.pojo.NotificationPOJO;

public class CommitCommentEvent extends NotificationPOJO{

	public CommitCommentEvent(JSONObject input, Service context) throws JSONException {
		super(input, context);
		
		JSONObject payload = input.getJSONObject("payload");
		JSONObject comment = payload.getJSONObject("comment");
		String text = comment.getString("body");
		
		setTitle("New comment");
		setText(text);
		setExpandedText(text);
	}

}
