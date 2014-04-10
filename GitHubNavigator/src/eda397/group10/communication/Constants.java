package eda397.group10.communication;

/**
 * A class that contains constants used in the program.
 * 
 * @author Oscar
 *
 */
public class Constants {
	
	/**
	 * Http request URL's.
	 */
	public static final String AUTHENTICATE_URL = "https://api.github.com";
	public static final String FETCH_REPOS_URL = AUTHENTICATE_URL + "/user/repos";
	
	/**
	 * The credentials for the test user of the github project.
	 */
	public static final String TEST_USER_USERNAME = "group10testuser";
	public static final String TEST_USER_PASSWORD = "eda397group10";
	
	/**
	 * Json keys.
	 */
	public static final String REPOSITORY_JSON_KEY = "full_name";
	
	/**
	 * Shared preference keys.
	 */
	public static final String LOGIN_CREDENTIALS_PREFERENCE_NAME = "Login Credentials";
	public static final String AUTH_PREFERENCE = "Authenticated";
	public static final String USERNAME_PREFERENCE = "Username";
	public static final String PASSWORD_PREFERENCE = "Password";

}
