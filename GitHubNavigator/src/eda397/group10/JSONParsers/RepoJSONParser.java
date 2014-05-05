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
import eda397.group10.communication.JsonExtractor;
import eda397.group10.navigator.TheListFragment;
import eda397.group10.pojo.RepositoryPOJO;
import eda397.group10.pojo.UserPOJO;

/**
 * This class is used to parse the JSON array, the result will be stored in POJOs, 
 * these POJOs will be saved in an ArrayList, after fetching all the needed POJOs,
 * we call the set the adapter of the list which displays all the data from this 
 * JSON array.
 */

public class RepoJSONParser extends AsyncTask<HttpResponse, Void, ArrayList<RepositoryPOJO>>{

	private ArrayList<RepositoryPOJO> datas;
	private TheListFragment contex;
	
	public RepoJSONParser(TheListFragment contex){
		this.contex = contex;
		datas = new ArrayList<RepositoryPOJO>();
	}
	
	@Override
	protected ArrayList<RepositoryPOJO> doInBackground(HttpResponse... params) {
		// TODO Auto-generated method stub
		BufferedReader reader;
		JSONArray json = new JSONArray();
		try {
			reader = new BufferedReader(new InputStreamReader(params[0].getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
			    builder.append(line).append("\n");
			}
			JSONTokener tokener = new JSONTokener(builder.toString());
			json = new JSONArray(tokener);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.println(Log.ASSERT, "catch", e.toString());
			e.printStackTrace();
		} 
		
		try{
			for (int i = 0; i < json.length(); i++) {
				JSONObject obj = json.getJSONObject(i);
				String name = obj.getString("name");
				String discription = obj.getString("description");
				String star = obj.getString("stargazers_count");
				JSONObject userObj = obj.getJSONObject("owner");
				int userId = userObj.getInt("id");
				UserPOJO userPojo = new UserPOJO();
				userPojo.setUserId(userId);
				RepositoryPOJO pojo = new RepositoryPOJO(name,star,discription,userPojo);		
				
				pojo.setFullName(obj.getString("full_name"));
				
				datas.add(pojo);
			}
		}catch(JSONException ex){
			
		}
		
		return datas;
	}
	
	@Override
	protected void onPostExecute(ArrayList<RepositoryPOJO> pojos) {
		// TODO Auto-generated method stub		
		contex.loadingProgress.dismiss();
		contex.setList(pojos,false);
	}

	
	
}
