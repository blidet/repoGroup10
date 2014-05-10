package eda397.group10.JSONParsers;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import eda397.group10.communication.GithubRequest;
import eda397.group10.navigator.AuthenticatedMainActivity;
import eda397.group10.navigator.R;
import eda397.group10.navigator.TaskFragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class ShaParser extends AsyncTask<HttpResponse, Void, String> {
	
	private TaskFragment context;
	private AuthenticatedMainActivity mainActivity;
	
	public ShaParser(TaskFragment context){
		this.context = context;
		mainActivity = (AuthenticatedMainActivity)context.getActivity();
	}

	@Override
	protected String doInBackground(HttpResponse... reponse) {
		
		BufferedReader reader;
		JSONArray finalArray;
		String finalResult = null;
		try {
			reader = new BufferedReader(new InputStreamReader(reponse[0].getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
			JSONTokener tokener = new JSONTokener(builder.toString());
			finalArray = new JSONArray(tokener);
			for(int i = 0; i<finalArray.length(); i++){
				JSONObject branchObj = finalArray.getJSONObject(i);
				if(branchObj.getString("name").equals("master")){
					 finalResult = branchObj.getJSONObject("commit").getString("sha");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.println(Log.ASSERT, "catch", e.toString());
			e.printStackTrace();
		} 
		
		return finalResult;
	}

	@Override
	protected void onPostExecute(String result) {
		
		SharedPreferences sh_Pref = context.getActivity().getSharedPreferences(context.getActivity().getResources().getString(R.string.LOGIN_CREDENTIALS_PREFERENCE_NAME),0);
		final Header header = BasicScheme.authenticate(
				new UsernamePasswordCredentials(sh_Pref.getString(context.getActivity().getResources().getString(R.string.USERNAME_PREFERENCE), ""), 
						sh_Pref.getString(context.getActivity().getResources().getString(R.string.PASSWORD_PREFERENCE), "")),
						HTTP.UTF_8, false);
		
		SharedPreferences settings_preferences = context.getActivity().getSharedPreferences(context.getActivity().getResources().getString(R.string.SETTINGS_PREFERENCES),0);
		String currentRepository = settings_preferences.getString(context.getActivity().getResources().getString(R.string.CURRENT_REPOSITORY_PREFERENCE), "none");
		
		String workingUrl = "https://api.github.com/repos/" + currentRepository + "/git/trees/"+result;
		mainActivity.tasksUrlStack.push(workingUrl);
		new DataRetriever(workingUrl, header);
		super.onPostExecute(result);
	}
	
	private class DataRetriever extends GithubRequest {

		public DataRetriever(String url, Header header) {
			super(url, header);
		}

		@Override
		public void onPostExecute(HttpResponse result) {
			TasksJSONParser parser = new TasksJSONParser(context);
			parser.execute(result);
		}
	}
	

}
