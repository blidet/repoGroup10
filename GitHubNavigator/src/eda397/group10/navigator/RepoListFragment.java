package eda397.group10.navigator;

import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;

import eda397.group10.communication.GithubRequest;
import eda397.group10.communication.JSONParser;
import eda397.group10.pojo.RepositoryPOJO;
import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

@SuppressLint("NewApi")
public class RepoListFragment extends ListFragment {
	private ListView repoList;
	private LayoutInflater layoutInflator;
	private RepoListFragment repo;
	public ProgressDialog loadingProgress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.repo_list, container, false);
		repo = this;
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
		new RepoRetriever(getResources().getString(R.string.FETCH_REPOS_URL), header);
		return rootView;
	}
	
	public void setList(ArrayList<RepositoryPOJO> datas){
		repoList.setAdapter(new ListAdapter(this,datas,layoutInflator));		
	}
	
	private class RepoRetriever extends GithubRequest {
		public RepoRetriever(String url, Header header) {
			super(url, header);
		}
		
		@Override
    	public void onPostExecute(HttpResponse result) {
			Integer statusCode = result.getStatusLine().getStatusCode();
			Log.println(Log.ASSERT, "get repos", "status code: "+statusCode+"");
			
			JSONParser repoBuilder = new JSONParser(repo);
			repoBuilder.execute(result);
		}
	}
	

}
