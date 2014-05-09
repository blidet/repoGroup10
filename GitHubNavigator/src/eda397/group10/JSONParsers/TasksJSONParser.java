package eda397.group10.JSONParsers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;
import eda397.group10.adapters.TaskListAdapter;
import eda397.group10.navigator.TaskFragment;
import eda397.group10.pojo.FilePOJO;

public class TasksJSONParser extends AsyncTask<HttpResponse, Void, JSONObject> {
	
	private ArrayList<FilePOJO> fileList;
    private	ArrayList<FilePOJO> folderList;
    private TaskFragment context;
	
	public TasksJSONParser(TaskFragment context){
		this.context = context;
		fileList = new ArrayList<FilePOJO>();
		folderList = new ArrayList<FilePOJO>();
	}

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
			
			for (int i = 0; i < tree.length(); i++) {
				JSONObject object = tree.getJSONObject(i);
				String path = object.getString("path");
				String type = object.getString("type");
				String fullUrl = object.getString("url");
				FilePOJO file = new FilePOJO(path, type, fullUrl);
				if(type.equals("tree")){
					folderList.add(file);
				}else{
					fileList.add(file);
				}					
			}
			context.loadingProgress.dismiss();
			folderList.addAll(fileList);
			//dataList.setAdapter(new TaskListAdapter(TaskFragment.this, folderList, inflater));
			context.setList(folderList);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
