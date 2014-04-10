package eda397.group10.communication;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;

public class JsonExtractor extends AsyncTask<HttpResponse, Void, JSONArray> {

	@Override
	protected JSONArray doInBackground(HttpResponse... params) {
		BufferedReader reader;
		JSONArray finalResult = new JSONArray();
		try {
			reader = new BufferedReader(new InputStreamReader(params[0].getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
			    builder.append(line).append("\n");
			}
			JSONTokener tokener = new JSONTokener(builder.toString());
			finalResult = new JSONArray(tokener);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.println(Log.ASSERT, "catch", e.toString());
			e.printStackTrace();
		} 
		
		return finalResult;
	}

}
