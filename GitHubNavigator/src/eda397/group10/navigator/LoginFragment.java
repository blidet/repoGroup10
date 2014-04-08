package eda397.group10.navigator;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {
	
	private EditText userNameEdit;
	private EditText passwordEdit;
	private String userName;
	private String password;
	private Button loginButton;
	private Button registerButton;
	private final int AUTHENTICATED_CODE = 200;
	private final int FAILED_AUTHENTICATION = 401;
	private ProgressDialog loginProgress;
	private AlertDialog.Builder dialogBuilder;
	private AlertDialog authFailAlert;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub		
		View rootView = inflater.inflate(R.layout.fragment_login, container, false);
		
		userNameEdit = (EditText) rootView.findViewById(R.id.login_username);
        passwordEdit = (EditText)rootView.findViewById(R.id.login_password);
        loginButton = (Button)rootView.findViewById(R.id.login_button);
        registerButton = (Button)rootView.findViewById(R.id.register_button);
        loginProgress = new ProgressDialog(getActivity());
        loginProgress.setMessage("Authenticating......");
        loginProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
        dialogBuilder = new AlertDialog.Builder(getActivity());
        authFailAlert = dialogBuilder.setMessage("Authntication failed").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        }).create();
        
        /**
         * The user clicked the login button.
         * At the moment it is just a link to the project page.
         * TODO: check github credentials
         * @param view
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loginProgress.show();
				LoginChecker checker = new LoginChecker();
				checker.execute("https://api.github.com");
			}
		});
        
        /**
         * This method sends the user to GitHubs registration page when
         * the "Register" button is clicked.
         * @param view
         */
        registerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent internetIntent = new Intent(Intent.ACTION_VIEW,
		    			Uri.parse("https://github.com/join"));
		    	internetIntent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
		    	internetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    	getActivity().startActivity(internetIntent);
			}
		});
 
		return rootView;
	}
	
	/**
	 * This AsyncTask is used for the authentication, it is done by sending a http GET request with the
	 * credentials filled in the http header. Authenticated: 200 OK; Authentication failed: 401 Unauthorized
	 */
	private class LoginChecker extends AsyncTask<String,Void,Integer>{

		@Override
		protected Integer doInBackground(String... url) {
			// TODO Auto-generated method stub
			userName = userNameEdit.getText().toString();
			password = passwordEdit.getText().toString();

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url[0]);
			request.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials(userName, password),
                    HTTP.UTF_8, false));
			
			HttpResponse response = null;
			try {
				response = client.execute(request);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return response.getStatusLine().getStatusCode();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			loginProgress.dismiss();
			if(result == AUTHENTICATED_CODE){
				Intent projectPageActivityIntent = new Intent(getActivity(),AuthenticatedMainActivity.class);
				getActivity().startActivity(projectPageActivityIntent);
			}else if(result == FAILED_AUTHENTICATION){
				authFailAlert.show();
			}
			super.onPostExecute(result);
		}
		
	}

	

}
