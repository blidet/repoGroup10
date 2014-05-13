package eda397.group10.JSONParsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import eda397.group10.communication.JsonExtractor;
import eda397.group10.navigator.R;
import eda397.group10.navigator.TheListFragment;
import eda397.group10.pojo.EventPOJO;
import eda397.group10.pojo.RepositoryPOJO;
import eda397.group10.pojo.UserPOJO;
import android.widget.BaseAdapter;

public class NewsJSONParser extends AsyncTask<HttpResponse, Void, ArrayList<EventPOJO>> {
	
	private TheListFragment context;
	private static ArrayList<EventPOJO> datas;
	private boolean loadMore;
	private HashMap<String, Bitmap> imageBitmap;
	
	
	public NewsJSONParser(TheListFragment context, boolean loadMore){
		this.context = context;
		if(!loadMore){
			datas = new ArrayList<EventPOJO>();
		}
		imageBitmap = new HashMap<String,Bitmap>();
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
			}
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
				String avatarUrlKey = actorObj.getString("avatar_url");
				URL avatarUrl = new URL(avatarUrlKey);
				
		        Bitmap myBitmap = null;	
				
				if(imageBitmap.containsKey(actorName)){
					myBitmap = imageBitmap.get(actorName);
				}else{
					InputStream istr = avatarUrl.openStream();
					myBitmap = BitmapFactory.decodeStream(istr);
				    imageBitmap.put(actorName, myBitmap);
				}
				
				
				UserPOJO actor = new UserPOJO();
				actor.setAvatarBitmap(myBitmap);
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
					event.setMoreToShow(true);
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
				case "IssueCommentEvent":
					JSONObject issueObj = payLoadObj.getJSONObject("issue");
					int issueN = issueObj.getInt("number");
					event.setIssueNumber(Integer.toString(issueN));
					datas.add(event);
					break;
				case "IssuesEvent":
					JSONObject issueObj1 = payLoadObj.getJSONObject("issue");
					String action = payLoadObj.getString("action");
					int issueN1 = issueObj1.getInt("number");
					event.setIssueNumber(Integer.toString(issueN1));
					event.setAction(action);
					datas.add(event);
					break;
				case "CommitCommentEvent":
					JSONObject commitObj = payLoadObj.getJSONObject("comment");
					String commitId = commitObj.getString("commit_id");
					event.setCommitId(commitId);
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
