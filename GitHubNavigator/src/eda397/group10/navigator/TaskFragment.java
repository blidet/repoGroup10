package eda397.group10.navigator;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import eda397.group10.communication.GithubRequest;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TaskFragment extends ListFragment {
	private ListView dataList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
		dataList = (ListView)rootView.findViewById(android.R.id.list);
		
		return rootView;
	}
	
	private class DataRetriever extends GithubRequest {

		public DataRetriever(String url, Header[] header) {
			super(url, header);
			// TODO Auto-generated constructor stub
		}
		
		@Override
    	public void onPostExecute(HttpResponse result) {
			
		}
	}
}
