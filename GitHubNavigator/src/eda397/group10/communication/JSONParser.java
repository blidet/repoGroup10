package eda397.group10.communication;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eda397.group10.navigator.RepoListFragment;
import eda397.group10.pojo.RepositoryPOJO;

public class JSONParser extends JsonExtractor {

	private ArrayList<RepositoryPOJO> datas;
	private RepoListFragment contex;
	
	public JSONParser(RepoListFragment contex){
		this.contex = contex;
	}
	
	@Override
	protected void onPostExecute(JSONArray json) {
		// TODO Auto-generated method stub
		datas = new ArrayList<RepositoryPOJO>();
		try{
			for (int i = 0; i < json.length(); i++) {
				JSONObject obj = json.getJSONObject(i);
				String name = obj.getString("name");
				String discription = obj.getString("description");
				String star = obj.getString("stargazers_count");
				RepositoryPOJO pojo = new RepositoryPOJO(name,star,discription,null);				
				datas.add(pojo);
			}
		}catch(JSONException ex){
			
		}
		contex.loadingProgress.dismiss();
		contex.setList(datas);
	}
	
}
