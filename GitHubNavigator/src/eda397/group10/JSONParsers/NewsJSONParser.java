package eda397.group10.JSONParsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import eda397.group10.communication.JsonExtractor;
import eda397.group10.navigator.TheListFragment;
import eda397.group10.pojo.PushEventPOJO;
import eda397.group10.pojo.RepositoryPOJO;
import eda397.group10.pojo.UserPOJO;
import eda397.group10.widget.*;
import eda397.group10.widget.LoadMoreListView.OnLoadMoreListener;
import android.widget.BaseAdapter;

public class NewsJSONParser extends AsyncTask<HttpResponse, Void, ArrayList<PushEventPOJO>> {
	
	private TheListFragment context;
	private ArrayList<PushEventPOJO> datas;
	private boolean loadMore;
	
	public NewsJSONParser(TheListFragment context, boolean loadMore){
		this.context = context;
		datas = new ArrayList<PushEventPOJO>();
		this.loadMore = loadMore;
	}
	
	@Override
	protected ArrayList<PushEventPOJO> doInBackground(HttpResponse... params) {
		// TODO Auto-generated method stub
		
		BufferedReader reader;
		JSONArray json = new JSONArray();
		try {
			reader = new BufferedReader(new InputStreamReader(params[0].getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
			    builder.append(line).append("\n");
			}
			
			Log.println(Log.INFO, "***************", params[0].getHeaders("Link")[0].getValue());
			
			
			JSONTokener tokener = new JSONTokener(builder.toString());
			Log.println(Log.INFO, "_________________________________",tokener.toString());
			json = new JSONArray(tokener);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.println(Log.ASSERT, "catch", e.toString());
			e.printStackTrace();
		} 
		
		try{
			
			for (int i = 0; i < json.length(); i++) {
				JSONObject obj = json.getJSONObject(i);
				if(obj.getString("type").equals("PushEvent")){
					JSONObject actorObj = obj.getJSONObject("actor");
					String actorName = actorObj.getString("login");
					URL avatarUrl = new URL(actorObj.getString("avatar_url"));
					InputStream istr = avatarUrl.openStream();
					Drawable imageDrawable = Drawable.createFromStream(istr, "src");
					UserPOJO actor = new UserPOJO(0, actorName, imageDrawable);
					
					JSONObject repoObj = obj.getJSONObject("repo");
					String repoName = repoObj.getString("name");
					
					JSONObject payLoadObj = obj.getJSONObject("payload");
					String branch = payLoadObj.getString("ref");
					
					PushEventPOJO pojo = new PushEventPOJO(actor,repoName,branch,null);
					datas.add(pojo);
				}
				
				
			}
			
		}catch(JSONException ex){
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return datas;
	}

	@Override
	protected void onPostExecute(ArrayList<PushEventPOJO> pojos) {
		// TODO Auto-generated method stub
		context.loadingProgress.dismiss();
		context.setList(pojos);
		if(loadMore){
		    //((BaseAdapter)context.getListAdapter()).notifyDataSetChanged();
			context.getListView().invalidateViews();
		}
		
		
		//((BaseAdapter)context.getListAdapter()).notifyDataSetChanged();
		//((LoadMoreListView) context.getListView()).onLoadMoreComplete();
		
//		((LoadMoreListView)context.getListView())
//        .setOnLoadMoreListener(new OnLoadMoreListener() {
//            public void onLoadMore() {
//                // Do the work to load more items at the end of list here
//                System.out.println("=================================================================================");
//            }
//        });
//		
	}

}
