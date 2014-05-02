package eda397.group10.navigator;

import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;

import com.google.gson.Gson;

import eda397.group10.adapters.NewsListAdapter;
import eda397.group10.adapters.RepoListAdapter;
import eda397.group10.communication.GithubRequest;
import eda397.group10.pojo.EventPOJO;
import eda397.group10.JSONParsers.NewsJSONParser;
import eda397.group10.JSONParsers.RepoJSONParser;
import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

/*
 * This is the fragment which lists all the user repositories.
 */

@SuppressLint("NewApi")
public class TheListFragment extends ListFragment {
	private ListView repoList;
	//private LoadMoreListView repoList;
	private LayoutInflater layoutInflator;
	private TheListFragment thisContext;
	public ProgressDialog loadingProgress;
	private String actionType;
	
	private ProgressBar mProgressBarLoadMore;
	
	public String loadMoreUrl=null;
	private boolean isLoadingMore = false;
	private int mCurrentScrollState;
	private int pageCount = 2;
	
	public TheListFragment(String actionType){
		this.actionType = actionType;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.repo_list, container, false);
		thisContext = this;
		repoList = (ListView)rootView.findViewById(android.R.id.list);
		View footerView = inflater.inflate(R.layout.load_more_footer, null, false);
		mProgressBarLoadMore = (ProgressBar)footerView.findViewById(R.id.load_more_progressBar);
		repoList.addFooterView(footerView);
		
		ArrayList theEvents = new ArrayList();
		SharedPreferences  mPrefs = getActivity().getPreferences(0);
		Gson gson = new Gson(); 
	    boolean first = mPrefs.getBoolean("first", true);
	    if(!first){
	    	for(int i=0;i<20;i++){
				 String json = mPrefs.getString(Integer.toString(i), "");
				 if(!json.equals("")){
					 EventPOJO theEvent = gson.fromJson(json, EventPOJO.class);
					 theEvents.add(theEvent);
				 }
			}
	    }
	    
		SharedPreferences sh_Pref = getActivity().getSharedPreferences(getResources().getString(R.string.LOGIN_CREDENTIALS_PREFERENCE_NAME),0);
		final String userName = sh_Pref.getString(getResources().getString(R.string.USERNAME_PREFERENCE), "");
		final Header header = BasicScheme.authenticate(
                new UsernamePasswordCredentials(sh_Pref.getString(getResources().getString(R.string.USERNAME_PREFERENCE), ""), 
                		sh_Pref.getString(getResources().getString(R.string.PASSWORD_PREFERENCE), "")),
                HTTP.UTF_8, false);
	
		switch(actionType){
		case "repo_action":
			new RepoRetriever(getResources().getString(R.string.FETCH_REPOS_URL), header, false);
			break;
		case "news_action":
			if(!first){
				repoList.setAdapter(new RepoListAdapter(this,theEvents,inflater));
			}
			
			new RepoRetriever("https://api.github.com/users/"+userName+"/received_events", header, false); 
			break;
		}	

		repoList.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
                boolean larger = firstVisibleItem + visibleItemCount >= totalItemCount;
                
                if (!isLoadingMore && larger && mCurrentScrollState != SCROLL_STATE_IDLE) {
                	isLoadingMore = true;
                	mProgressBarLoadMore.setVisibility(View.VISIBLE);
                	new RepoRetriever("https://api.github.com/users/"+userName+"/received_events?page="+pageCount, header,true);
                	pageCount++;
                }
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub				
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					view.invalidateViews();
				}
				mCurrentScrollState = scrollState;
				isLoadingMore = false;
			}
			
		});

		loadingProgress = new ProgressDialog(getActivity());
        loadingProgress.setMessage("Loading......");
        loadingProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingProgress.show();
		this.layoutInflator = inflater;
			
		return rootView;
	}
	
	public void setList(ArrayList datas, boolean shouldLoadMore){
		switch(actionType){
		case "repo_action":
			repoList.setAdapter(new RepoListAdapter(this,datas,layoutInflator));
			break;
		case "news_action":
			int position = repoList.getFirstVisiblePosition();
			mProgressBarLoadMore.setVisibility(View.GONE);
			repoList.setAdapter(new NewsListAdapter(this,datas,layoutInflator));
			if(shouldLoadMore){
				repoList.setSelectionFromTop(position+1, 0);
				//isLoadingMore = false;
			}
			break;
		}	
				
	}
	
	private class RepoRetriever extends GithubRequest {
		private boolean loadMore;
		public RepoRetriever(String url, Header header, boolean loadMore) {
			super(url, header);
			this.loadMore = loadMore;
		}
		
		@Override
    	public void onPostExecute(HttpResponse result) {
			switch(actionType){
			case "repo_action":
				RepoJSONParser repoBuilder = new RepoJSONParser(thisContext);
				repoBuilder.execute(result);
				break;
			case "news_action":
				NewsJSONParser newsBuilder = new NewsJSONParser(thisContext,loadMore);
				newsBuilder.execute(result);
				break;
			}					
		}
	}
}
