package eda397.group10.navigator;

import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;

import eda397.group10.adapters.NewsListAdapter;
import eda397.group10.adapters.RepoListAdapter;
import eda397.group10.communication.GithubRequest;
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
import android.widget.ListView;

/*
 * This is the fragment which lists all the user repositories.
 */

@SuppressLint("NewApi")
public class TheListFragment extends ListFragment {
	private ListView repoList;
	private LayoutInflater layoutInflator;
	private TheListFragment thisContext;
	public ProgressDialog loadingProgress;
	private String actionType;
	
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
		loadingProgress = new ProgressDialog(getActivity());
        loadingProgress.setMessage("Loading......");
        loadingProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingProgress.show();
		this.layoutInflator = inflater;
		SharedPreferences sh_Pref = getActivity().getSharedPreferences(getResources().getString(R.string.LOGIN_CREDENTIALS_PREFERENCE_NAME),0);
		Header header = BasicScheme.authenticate(
                new UsernamePasswordCredentials(sh_Pref.getString(getResources().getString(R.string.USERNAME_PREFERENCE), ""), 
                		sh_Pref.getString(getResources().getString(R.string.PASSWORD_PREFERENCE), "")),
                HTTP.UTF_8, false);
    	//Send HTTP request to retrieve user repos:		
		switch(actionType){
		case "repo_action":
			new RepoRetriever(getResources().getString(R.string.FETCH_REPOS_URL), header);
			break;
		case "news_action":
			new RepoRetriever("https://api.github.com/users/haozhenxiao/received_events", header); 
			break;
		}		
		//new RepoRetriever("https://api.github.com/users/haozhenxiao/received_events", header); 
		return rootView;
	}
	
	public void setList(ArrayList datas){
		switch(actionType){
		case "repo_action":
			repoList.setAdapter(new RepoListAdapter(this,datas,layoutInflator));
			break;
		case "news_action":
			repoList.setAdapter(new NewsListAdapter(this,datas,layoutInflator));
			break;
		}	
				
	}
	
	private class RepoRetriever extends GithubRequest {
		public RepoRetriever(String url, Header header) {
			super(url, header);
		}
		
		@Override
    	public void onPostExecute(HttpResponse result) {
			switch(actionType){
			case "repo_action":
				RepoJSONParser repoBuilder = new RepoJSONParser(thisContext);
				repoBuilder.execute(result);
				break;
			case "news_action":
				NewsJSONParser newsBuilder = new NewsJSONParser(thisContext);
				newsBuilder.execute(result);
				break;
			}		
			
		}
	}
	

}
