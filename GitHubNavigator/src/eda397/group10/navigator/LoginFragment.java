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

import eda397.group10.communication.GithubRequest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {
	
	private EditText userNameEdit;
	private EditText passwordEdit;
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
				loginProgress.show();

		    	String userName = userNameEdit.getText().toString();
				String password = passwordEdit.getText().toString();
				
				//Creates a basic authentication Header object, which is then used to create a GithubRequest object to handle the authentication 
				Header header = BasicScheme.authenticate(
		                new UsernamePasswordCredentials(userName, password),
		                HTTP.UTF_8, false);
				LoginChecker checker = new LoginChecker("https://api.github.com", header);
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
				Intent internetIntent = new Intent(Intent.ACTION_VIEW,
		    			Uri.parse("https://github.com/join"));
		    	internetIntent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
		    	internetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    	getActivity().startActivity(internetIntent);
			}
		});
 
		return rootView;
	}
    
    private class LoginChecker extends GithubRequest {
    	
    	public LoginChecker(String url, Header header) {
			super(url, header);
		}

    	/**
    	 * Checks the status code of the HTTP response. Depending on the response, 
    	 * either the next activity is started or the error is shown to the user.
    	 */
		@Override
    	public void onPostExecute(HttpResponse result) {
    		super.onPostExecute(result);
    		loginProgress.dismiss();
    		Integer statusCode = result.getStatusLine().getStatusCode();
    		
    		if(statusCode == AUTHENTICATED_CODE){
    			Intent projectPageActivityIntent = new Intent(getActivity(),AuthenticatedMainActivity.class);
    			getActivity().startActivity(projectPageActivityIntent);
    		}else if(statusCode == FAILED_AUTHENTICATION){
    			authFailAlert.show();
    		} else {
    			//TODO: handle other status codes.
    		}
    	}
    }
}
