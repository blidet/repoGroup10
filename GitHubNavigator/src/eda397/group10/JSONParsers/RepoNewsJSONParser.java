package eda397.group10.JSONParsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import eda397.group10.navigator.TheListFragment;
import eda397.group10.pojo.EventPOJO;
import eda397.group10.pojo.UserPOJO;

public class RepoNewsJSONParser extends AsyncTask<HttpResponse, Void, ArrayList<EventPOJO>> {
	
	private TheListFragment context;
	private static ArrayList<EventPOJO> datas;
	private boolean loadMore;
	private HashMap<String, Bitmap> imageCache;
	
	public RepoNewsJSONParser(TheListFragment context, boolean loadMore){
		this.context = context;
		if(!loadMore){
			datas = new ArrayList<EventPOJO>();
		}
		imageCache = new HashMap<String, Bitmap>();
		this.loadMore = loadMore;
	}
	
	@Override
	protected ArrayList<EventPOJO> doInBackground(HttpResponse... params) {
		// TODO Auto-generated method stub
		
		Log.println(Log.DEBUG, "RepoNewsDoInBackground", "Trying to parse json etc.");
		
		BufferedReader reader;
		JSONArray json = new JSONArray();
		try {
			reader = new BufferedReader(new InputStreamReader(params[0].getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
			    builder.append(line).append("\n");
			    Log.println(Log.INFO, "************", line);
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
				
				String committerName;
				//Drawable imageDrawable;
				Bitmap imageBitmap;
				
				if(!obj.isNull("committer")){
					JSONObject committerObj = obj.getJSONObject("committer");
					committerName = committerObj.getString("login");
					String avatarUrlKey = committerObj.getString("avatar_url");
					URL avatarUrl = new URL(avatarUrlKey);
					imageBitmap = null;
					if(imageCache.containsKey(avatarUrlKey)){
						imageBitmap = imageCache.get(avatarUrlKey);
					}else{
						InputStream istr = avatarUrl.openStream();				
						imageBitmap = BitmapFactory.decodeStream(istr);
						istr.close();
						imageCache.put(avatarUrlKey, imageBitmap);
					}				
				}else if(!obj.isNull("author")){
					JSONObject authorObj = obj.getJSONObject("author");
					committerName = authorObj.getString("login");
					String avatarUrlKey = authorObj.getString("avatar_url");
					URL avatarUrl = new URL(avatarUrlKey);
					imageBitmap = null;
					if(imageCache.containsKey(avatarUrlKey)){
						imageBitmap = imageCache.get(avatarUrlKey);
					}else{
						InputStream istr = avatarUrl.openStream();				
						imageBitmap = BitmapFactory.decodeStream(istr);
						istr.close();
						imageCache.put(avatarUrlKey, imageBitmap);
					}				
				}else{
					committerName = "Unknown";
					imageBitmap = null;
				}
				
				UserPOJO author = new UserPOJO();
				//author.setAvatar(imageDrawable);
				author.setAvatarBitmap(imageBitmap);
				author.setName(committerName);
				
				EventPOJO event = new EventPOJO();
				event.setType("commitEvent");
				event.setActor(author);
				event.setRepoName("undefined");
				
				JSONObject commitObject = obj.getJSONObject("commit");
				String message = commitObject.getString("message");
				event.setComment(message);
				datas.add(event);
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
		Log.println(Log.DEBUG, "RepoNewsOnPostExecute", "Execute done, trying to set list.");
		// TODO Auto-generated method stub
		context.loadingProgress.dismiss();
		context.setList(pojos,loadMore);
	}

}
