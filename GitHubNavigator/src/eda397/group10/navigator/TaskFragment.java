package eda397.group10.navigator;

import java.util.ArrayList;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;
import eda397.group10.adapters.TaskListAdapter;
import eda397.group10.communication.GithubRequest;
import eda397.group10.pojo.FilePOJO;
import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import eda397.group10.JSONParsers.ShaParser;
import eda397.group10.JSONParsers.TasksJSONParser;

@SuppressLint("ValidFragment")
public class TaskFragment extends ListFragment {
	private ListView dataList;
	private LayoutInflater inflater;
	private TaskFragment thisContext;
	public ProgressDialog loadingProgress;
	private String workingUrl;
	private boolean needsSha;
	
	public TaskFragment(String workingUrl,boolean needsSha){
		this.needsSha = needsSha;
		this.workingUrl = workingUrl;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
		thisContext = this;
		this.inflater = inflater;
		dataList = (ListView)rootView.findViewById(android.R.id.list);
		
		showFolder();
		
		loadingProgress = new ProgressDialog(getActivity());
        loadingProgress.setMessage("Loading......");
        loadingProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingProgress.show();
        
		return rootView;
	}
	
	public void showFolder() {
		SharedPreferences sh_Pref = getActivity().getSharedPreferences(getResources().getString(R.string.LOGIN_CREDENTIALS_PREFERENCE_NAME),0);
		final Header header = BasicScheme.authenticate(
				new UsernamePasswordCredentials(sh_Pref.getString(getResources().getString(R.string.USERNAME_PREFERENCE), ""), 
						sh_Pref.getString(getResources().getString(R.string.PASSWORD_PREFERENCE), "")),
						HTTP.UTF_8, false);

		new DataRetriever(workingUrl, header);
	}
	
	/**
	 * Retrieves data from github API
	 *
	 */
	private class DataRetriever extends GithubRequest {

		public DataRetriever(String url, Header header) {
			super(url, header);
		}

		@Override
		public void onPostExecute(HttpResponse result) {			
			if(!needsSha){
				TasksJSONParser parser = new TasksJSONParser(thisContext);
				parser.execute(result);
			}else{
				ShaParser parser = new ShaParser(thisContext);
				parser.execute(result);			
			}
			
		}
	}
	
	public void setList(ArrayList<FilePOJO> fileList){
		dataList.setAdapter(new TaskListAdapter(TaskFragment.this, fileList, inflater));
	}
	
	
}
