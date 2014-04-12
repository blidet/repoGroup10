package eda397.group10.communication;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

/**
 * Class that handles general requests to GitHub.
 * To use this class, add a class that extends this one in the view that should do the request.
 * The function onPostExecute should be overwritten in the new class to handle the response of the request.
 *
 */
public class GithubRequest extends AsyncTask<String, Void, HttpResponse> {
	private final Header currentHeader;
	
	/**
	 * 
	 * @param url the URL to send the request to
	 * @param header the header data that should be sent with the requests (for example username and password)
	 */
	public GithubRequest(String url, Header header){
		currentHeader = header;
		this.execute(url);
	}

	/**
	 * The actual request. This method is called by the execute method.
	 */
	@Override
	protected HttpResponse doInBackground(String... url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url[0]);
		request.addHeader(currentHeader);
		
		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response;
	}
}
