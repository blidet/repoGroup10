package eda397.group10.navigator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import eda397.group10.adapters.TaskListAdapter;
import eda397.group10.communication.GithubRequest;
import eda397.group10.database.PathDataBase;
import eda397.group10.pojo.FilePOJO;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TaskFragment extends ListFragment {
	private ListView dataList;
	private LayoutInflater inflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
		this.inflater = inflater;
		dataList = (ListView)rootView.findViewById(android.R.id.list);
		
		//TODO: static url.....
		showFolder("https://api.github.com/repos/blidet/repoGroup10/git/trees/1564202c2ff0da75228a255240f8c043c77e45da");  
        
		return rootView;
	}
	
	public void showFolder(String url) {
		SharedPreferences sh_Pref = getActivity().getSharedPreferences(getResources().getString(R.string.LOGIN_CREDENTIALS_PREFERENCE_NAME),0);
		final String userName = sh_Pref.getString(getResources().getString(R.string.USERNAME_PREFERENCE), "");
		final Header header = BasicScheme.authenticate(
				new UsernamePasswordCredentials(sh_Pref.getString(getResources().getString(R.string.USERNAME_PREFERENCE), ""), 
						sh_Pref.getString(getResources().getString(R.string.PASSWORD_PREFERENCE), "")),
						HTTP.UTF_8, false);

		new DataRetriever(url, header);
	}
	
	/**
	 * Retrieves data from github API
	 *
	 */
	private class DataRetriever extends GithubRequest {

		public DataRetriever(String url, Header header) {
			super(url, header);
		}

		@Override
		public void onPostExecute(HttpResponse result) {
			JsonParser parser = new JsonParser();
			parser.execute(result);
		}
	}

	/**
	 * Translates retrieved from github api into java objects
	 *
	 */
	private class JsonParser extends AsyncTask<HttpResponse, Void, JSONObject> {

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

		@Override
		public void onPostExecute(JSONObject result) {
			JSONArray tree;
			try {
				tree = result.getJSONArray("tree");
				ArrayList<FilePOJO> fileList = new ArrayList<FilePOJO>();
				
				for (int i = 0; i < tree.length(); i++) {
					JSONObject object = tree.getJSONObject(i);
					String path = object.getString("path");
					String type = object.getString("type");
					String fullUrl = object.getString("url");
					FilePOJO file = new FilePOJO(path, type, fullUrl);
					fileList.add(file);
				}
				
				dataList.setAdapter(new TaskListAdapter(TaskFragment.this, fileList, inflater));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
