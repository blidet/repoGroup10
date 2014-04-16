package eda397.group10.communication;

import org.json.JSONArray;

import eda397.group10.navigator.NewsListFragment;

public class NewsJSONParser extends JSONExtractor {
	
	private NewsListFragment context;
	
	public NewsJSONParser(NewsListFragment context){
		this.context = context;
	}

	@Override
	protected void onPostExecute(JSONArray result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}
	
	

}
