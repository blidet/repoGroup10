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
import eda397.group10.pojo.EventPOJO;
import eda397.group10.pojo.RepositoryPOJO;
import eda397.group10.pojo.UserPOJO;
import eda397.group10.widget.*;
import eda397.group10.widget.LoadMoreListView.OnLoadMoreListener;
import android.widget.BaseAdapter;

public class NewsJSONParser extends AsyncTask<HttpResponse, Void, ArrayList<EventPOJO>> {
	
	private TheListFragment context;
	private static ArrayList<EventPOJO> datas;
	private boolean loadMore;
	
	public NewsJSONParser(TheListFragment context, boolean loadMore){
		this.context = context;
		if(!loadMore){
			datas = new ArrayList<EventPOJO>();
		}
		
		this.loadMore = loadMore;
	}
	
	@Override
	protected ArrayList<EventPOJO> doInBackground(HttpResponse... params) {
		// TODO Auto-generated method stub
		
		BufferedReader reader;
		JSONArray json = new JSONArray();
		try {
			reader = new BufferedReader(new InputStreamReader(params[0].getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
			    builder.append(line).append("\n");
			    Log.println(Log.INFO, "************", line);
			}
			
			
//			for(int i=0; i<params[0].getAllHeaders().length;i++){
//				Log.println(Log.INFO, "################", params[0].getAllHeaders()[i].getName()+" : "+params[0].getAllHeaders()[i].getValue());
//			}
			
//			for(int i=0; i<params[0].getFirstHeader("Link").getElements().length;i++){
//				Log.println(Log.INFO, "***************", params[0].getFirstHeader("Link").getElements()[i].getName()+" ------- "+params[0].getFirstHeader("Link").getElements()[i].getValue());
//			}
			//String linkHeaderValue = params[0].getFirstHeader("Link");
			//String nextUrl = linkHeaderValue.r
			
			JSONTokener tokener = new JSONTokener(builder.toString());
			json = new JSONArray(tokener);
			
			
		} catch (Exception e) {
			Log.println(Log.ASSERT, "catch", e.toString());
			e.printStackTrace();
		} 
		
		try{
			for (int i = 0; i < json.length(); i++) {
				JSONObject obj = json.getJSONObject(i);
				String eventType = obj.getString("type");
				JSONObject actorObj = obj.getJSONObject("actor");
				String actorName = actorObj.getString("login");
				URL avatarUrl = new URL(actorObj.getString("avatar_url"));
				System.out.println("66666666666666666666 "+avatarUrl);
				InputStream istr = avatarUrl.openStream();
				
				Drawable imageDrawable = Drawable.createFromStream(istr, "src");
				istr.close();
				
				UserPOJO actor = new UserPOJO();
				actor.setAvatar(imageDrawable);
				actor.setName(actorName);
				
				JSONObject repoObj = obj.getJSONObject("repo");
				String repoName = repoObj.getString("name");				
				JSONObject payLoadObj = obj.getJSONObject("payload");								
				EventPOJO event = new EventPOJO();
				event.setType(eventType);
				event.setActor(actor);
				event.setRepoName(repoName);
				switch(eventType){
				case "PushEvent":	
					String branch = payLoadObj.getString("ref");
					event.setRef(branch);
					datas.add(event);				
					break;
				case "CreateEvent":
					String refType = payLoadObj.getString("ref_type");
					String ref = payLoadObj.getString("ref");
					event.setRefType(refType);
					if(!refType.equals("repository")){
						event.setRef(ref);
					}
					datas.add(event);
					break;
				case "ForkEvent":
					JSONObject forkeeObj = payLoadObj.getJSONObject("forkee");
					String forkee = forkeeObj.getString("name");
					event.setRepoName(forkee);
					datas.add(event);
					break;
				}
			}
			
		}catch(JSONException ex){
			ex.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return datas;
	}

	@Override
	protected void onPostExecute(ArrayList<EventPOJO> pojos) {
		// TODO Auto-generated method stub
		context.loadingProgress.dismiss();
		context.setList(pojos,loadMore);
	}

}
