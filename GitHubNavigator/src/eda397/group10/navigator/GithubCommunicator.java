package eda397.group10.navigator;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.os.AsyncTask;

public class GithubCommunicator extends AsyncTask<String, Void, String> {
	
	private final Header currentHeader;
	
	public GithubCommunicator(String[] url, Header header){
		currentHeader = header;
		this.execute(url);
	}

	@Override
	protected String doInBackground(String... url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url[0]);
		request.addHeader(currentHeader);
		
		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//return response.getStatusLine().getStatusCode();
		return response.toString();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		//requester.handleResponse(result);
	}

}
