package eda397.group10.navigator;

import eda397.group10.communication.GithubRequest;
import eda397.group10.communication.JsonExtractor;
import eda397.group10.notifications.NotificationAlarm;
import eda397.group10.sliding.NavDrawerItem;
import eda397.group10.sliding.NavDrawerListAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.Intent;
import android.content.SharedPreferences.Editor;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

@SuppressLint("NewApi")
public class AuthenticatedMainActivity extends Activity{
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private SharedPreferences sh_Pref;
	//private Menu theMenu;
	//private MenuItem theItem;
	private int currentPosition;
	private boolean showRefresh = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_authenticated_main);

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();
		
		sh_Pref = getSharedPreferences(getResources().getString(R.string.LOGIN_CREDENTIALS_PREFERENCE_NAME),0);

		// adding nav drawer items to array
		// Home
		//navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		
		//That is just the menu in the right order of the tasks
		
		// News
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));		
		// Settings
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));	
		// Profile
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Logout
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		// Repositories
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		
		
		
		
		// Communities, Will add a counter here
		//navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
		// Pages
		//navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// What's hot, We  will add a counter here
		//navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));
		

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
		
		/*
		 * Here we are searching the repo name 
		 */
		SharedPreferences sh_Pref = getSharedPreferences(getResources().getString(R.string.LOGIN_CREDENTIALS_PREFERENCE_NAME),0);
        boolean authenticated = sh_Pref.getBoolean(getResources().getString(R.string.AUTH_PREFERENCE), false);
        
        if (authenticated) {
        	//Update timestamp to ignore events before current time
        	SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.SETTINGS_PREFERENCES),0);        	
        	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        	Editor toEdit = settings.edit();
        	toEdit.putString(getResources().getString(R.string.LAST_POLL), dateFormat.format(cal.getTime()));
        	toEdit.commit();
        	
    		//Create alarm that polls for notifications
            NotificationAlarm alarm = new NotificationAlarm();
            alarm.startAlarm(this);
        	
        	//Create a Header with the username and password saved in "Shared Preferences":
        	Header header = BasicScheme.authenticate(
                    new UsernamePasswordCredentials(sh_Pref.getString(getResources().getString(R.string.USERNAME_PREFERENCE), ""), 
                    		sh_Pref.getString(getResources().getString(R.string.PASSWORD_PREFERENCE), "")),
                    HTTP.UTF_8, false);
        	//Send HTTP request to retrieve user repos:
    		new RepoRetriever(getResources().getString(R.string.FETCH_REPOS_URL),header);
        } else {
        	//If you are not loged going back to login page
        	Intent intent = new Intent(this, MainActivity.class);
        	startActivity(intent);
        }
	}
	
	

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
			
			if(parent.getItemAtPosition(position) instanceof NavDrawerItem)
				openRepository((NavDrawerItem)parent.getItemAtPosition(position));

		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		//theItem = menu.findItem(R.id.action_refresh);
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.action_refresh:
			displayView(currentPosition);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		menu.findItem(R.id.action_refresh).setVisible(showRefresh);
		
//		MenuInflater inflater = getMenuInflater();
//	    inflater.inflate(R.menu.refresh, menu);
//	    MenuItem item = menu.findItem(R.id.action_refresh);
//	    item.setVisible(showRefresh);
	    
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		currentPosition = position;
		Fragment fragment = null;
		ListFragment listFragment = null;
		switch (position) {
		case 0:
			showRefresh = true;
			listFragment = new TheListFragment(getResources().getString(R.string.NEWS_ACTION));
			break;
		case 1:
			showRefresh = false;
			//fragment = new FindPeopleFragment();
			break;
		case 2:	
		    showRefresh = false;		    
			fragment = new SettingsFragment();
			//fragment = new PhotosFragment();
			break;
		case 3:
//----------------add confirmation for logout ----------------------------------

			
			
//---------------------------------------------------------------------
			sh_Pref.edit().clear().commit();
			 Intent firstpage=new Intent(this,MainActivity.class);			 
			 startActivity(firstpage);
			break;
		case 4:
			showRefresh = true;
			listFragment = new TheListFragment(getResources().getString(R.string.REPO_ACTION));			
			break;
		case 5:
			//fragment = new WhatsHotFragment();
			break;
		/**
		 * Currently used for the repository news feed.
		 */
		case 99:
			showRefresh = true;
			listFragment = new TheListFragment(getResources().getString(R.string.REPO_NEWS_ACTION));
			break;

		default:
			break;
		}
		
		invalidateOptionsMenu();

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			if(position <= mDrawerList.getCount()){
				mDrawerList.setItemChecked(position, true);
				mDrawerList.setSelection(position);
				setTitle(navMenuTitles[position]);
			}
			
			mDrawerLayout.closeDrawer(mDrawerList);
		}else if(listFragment != null){
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, listFragment).commit();

			// update selected item and title, then close the drawer
			if(position <= mDrawerList.getCount()){
				mDrawerList.setItemChecked(position, true);
				mDrawerList.setSelection(position);
				setTitle(navMenuTitles[position]);
			}
			
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}
	
	public void openRepository(String fullName){
		
		//======= Variables =======
		
		SharedPreferences sh_Pref;
		Editor toEdit;
		
		//===== Functionality =====
		
		/**
		 * Store the repository in the shared preferences.
		 */
		sh_Pref = getSharedPreferences(getResources().getString(R.string.SETTINGS_PREFERENCES),0);
		toEdit = sh_Pref.edit();
		toEdit.putString(getResources().getString(R.string.CURRENT_REPOSITORY_PREFERENCE), fullName);
        toEdit.putBoolean(getResources().getString(R.string.HAS_CURRENT_REPOSITORY_PREFERENCE), true);
        toEdit.commit();
        
        //TODO Open the repository news view.
        displayView(99);
		
	}
	
	
	/**
	 * Opens the clicked repository in the slider menu, as well as storing it 
	 * as the most recent repository in the shared preferences.
	 * 
	 * @param navDrawerItem
	 */
	private void openRepository(NavDrawerItem navDrawerItem){
		
		//======= Variables =======
		
		SharedPreferences sh_Pref;
		Editor toEdit;
		
		//===== Functionality =====
		
		/**
		 * Makes sure that this nav drawer item repressents a repository.
		 */
		if(navDrawerItem.getType() != NavDrawerItem.NavDrawerItemType.REPOSITORY)
			return;
		
		/**
		 * Store the repository in the shared preferences.
		 */
		sh_Pref = getSharedPreferences(getResources().getString(R.string.SETTINGS_PREFERENCES),0);
		toEdit = sh_Pref.edit();
		toEdit.putString(getResources().getString(R.string.CURRENT_REPOSITORY_PREFERENCE), navDrawerItem.getTitle());
        toEdit.putBoolean(getResources().getString(R.string.HAS_CURRENT_REPOSITORY_PREFERENCE), true);
        toEdit.commit();
        
        //TODO Open the repository news view.
        displayView(99);
		
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	/**
	 * Retrieves the current users repositories, then passes them on to RepoBuilder.
	 *
	 */
	private class RepoRetriever extends GithubRequest {
		public RepoRetriever(String url, Header header) {
			super(url, header);
		}
		
		@Override
    	public void onPostExecute(HttpResponse result) {
			Integer statusCode = result.getStatusLine().getStatusCode();
			Log.println(Log.ASSERT, "get repos", "status code: "+statusCode+"");
			
			RepoBuilder repoBuilder = new RepoBuilder();
			repoBuilder.execute(result);
		}
	}
	
	/**
	 * Extracts repo information from the HTTP response.
	 *
	 */
	private class RepoBuilder extends JsonExtractor {
		@Override
    	public void onPostExecute(JSONArray json) {
			Log.println(Log.ASSERT, "REPO BUILDER:::", json.toString());
			
			try {
				for (int i = 0; i < json.length(); i++) {
					String name = json.getJSONObject(i).get(getResources().getString(R.string.REPOSITORY_JSON_KEY)).toString();
					//Log.println(Log.ASSERT, "NAME", name);
					/*
					 * Writing the repo names
					 */
					navDrawerItems.add(new NavDrawerItem(name, navMenuIcons.getResourceId(3, -1), 
							NavDrawerItem.NavDrawerItemType.REPOSITORY));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*
			 *Creating the nav menu
			 */
			// Recycle the typed array
			navMenuIcons.recycle();

			mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

			// setting the nav drawer list adapter
			adapter = new NavDrawerListAdapter(getApplicationContext(),
					navDrawerItems);
			mDrawerList.setAdapter(adapter);

			// enabling action bar app icon and behaving it as toggle button
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setHomeButtonEnabled(true);

			mDrawerLayout.setDrawerListener(mDrawerToggle);
			
		}
	}


}