package eda397.group10.navigator;

import java.io.IOException;

import org.eclipse.egit.github.core.Authorization;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.OAuthService;

import android.util.Log;

/**
 * 
 * 
 * @author Oscar
 *
 */
public class UserManager {

	/**
	 * The singleton instance of the user manager.
	 */
	private static UserManager me = null;
	
	/**
	 * The name and password of the test user.
	 */
	public static final String TEST_USER_NAME = "group10testuser";
	public static final String TEST_USER_PASSWORD = "eda397group10";
	
	/**
	 * Get the singleton instance of the user manager.
	 */
	public static UserManager getInstance(){
		if(me==null)
			me = new UserManager();
		return me;
	}
	
	public boolean authenticate(String name, String password){
		
//		OAuthService oAuthService = new OAuthService();
//		oAuthService.getClient().setCredentials(name, password);
//		Authorization auth = new Authorization();
//		try {
//			auth = oAuthService.createAuthorization(auth);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		Log.println(Log.INFO, "user_management", "Auth: " + auth);
		
		GitHubClient client = new GitHubClient();
		client.setCredentials(name, password);
		Log.println(Log.INFO, "user_management", "Client: " + client.getUser());
		return true;
	}
	
}
