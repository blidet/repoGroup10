package eda397.group10.communication;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;

/**
 * This class is used to fetch the JSON string, from the API we can see that the returned result
 * is a JSON array, so we return the JSON array instead of string.
 */
public class JsonObjectExtractor extends AsyncTask<HttpResponse, Void, JSONObject> {

	@Override
	protected JSONObject doInBackground(HttpResponse... params) {
		BufferedReader reader;
		JSONObject finalResult = new JSONObject();
		try {
			reader = new BufferedReader(new InputStreamReader(params[0].getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
			    builder.append(line).append("\n");
			}
			JSONTokener tokener = new JSONTokener(builder.toString());
			finalResult = new JSONObject(tokener);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.println(Log.ASSERT, "catch", e.toString());
			e.printStackTrace();
		} 
		
		return finalResult;
	}

}
