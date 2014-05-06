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
	private boolean showTabs = false;
	private ActionBar actionBar;
	private ListFragment newsListFragment = null;
	private boolean firstLoad = true;
	
	/**
	 * The string of the currently displayed list fragment.
	 */
	private String currentListFragmentString = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_authenticated_main);

		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // show the given tab
	        	switch(tab.getPosition()){
	        	case 0:
	        		if(!firstLoad){
	        			displayView(-2);
	        		}	        		
	        		break;
	        	case 1:
	        		firstLoad = false;
	        		displayView(-1);
	        		break;
	        	}
	        }

	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // hide the given tab
	        }

	        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // probably ignore this event
	        }
	    };

	    actionBar.addTab(actionBar.newTab().setText("Events").setTabListener(tabListener));
	    actionBar.addTab(actionBar.newTab().setText("Commits").setTabListener(tabListener));


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

		// News
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1), 
				NavDrawerItem.NavDrawerItemType.NEWS));		
		// Settings
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(2, -1),
				NavDrawerItem.NavDrawerItemType.SETTINGS));

		// Tasks
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(1, -1), NavDrawerItem.NavDrawerItemType.TASKS));

		// Logout
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(3, -1),
				NavDrawerItem.NavDrawerItemType.LOGOUT));
		// Repositories
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(4, -1),
				NavDrawerItem.NavDrawerItemType.REPOSITORIES));

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
			//TODO on first time display view for first nav item
			if(mDrawerList.getItemAtPosition(0) instanceof NavDrawerItem)
				displayView(0, (NavDrawerItem)mDrawerList.getItemAtPosition(0));
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

			//======= Variables =======

			/**
			 * The clicked item.
			 */
			NavDrawerItem item = null;

			//===== Functionality =====

			/**
			 * Get the nav drawer item at the clicked position.
			 */
			if(item == null && parent.getItemAtPosition(position) instanceof NavDrawerItem)
				item = (NavDrawerItem)parent.getItemAtPosition(position);

			/**
			 * Display the view of the clicked item.
			 */
			displayView(position, item);

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
//			if(mDrawerList.getItemAtPosition(currentPosition) instanceof NavDrawerItem)
//				displayView(currentPosition, (NavDrawerItem)mDrawerList.getItemAtPosition(currentPosition));
			if(!currentListFragmentString.equals(""))
				refreshCurrentFragment();
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

		if(drawerOpen){
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			menu.findItem(R.id.action_refresh).setVisible(false);
		}else{
			menu.findItem(R.id.action_refresh).setVisible(showRefresh);
			if(showTabs){
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			}
		}


		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 */
	private void displayView(int position) {

		//======= Variables =======

		ListFragment listFragment = null;

		//===== Functionality =====

		switch (position) {
		case -2:
			listFragment = new TheListFragment(getResources().getString(R.string.REPO_NEWS_ACTION));
			break;
		case -1:
			listFragment = new TheListFragment(getResources().getString(R.string.REPO_COMMIT_NEWS_ACTION));
			default : break;
		}

		if(listFragment == null)
			return;

		switchFragment(listFragment);

	}

	/**
	 * Displaying the view of a nav drawer list item at position the specified adapter view.
	 */
	private void displayView(int position, NavDrawerItem item) {

		//======= Variables =======

		Fragment fragment = null;
		ListFragment listFragment = null;
		SharedPreferences sharedPreferences;
		Editor toEdit;

		//===== Functionality =====

		/**
		 * Makes sure that there is a nav drawer item and that it has a type.
		 */
		if(item == null)
			return;
		if(item.getType() == null)
			return;

		/**
		 * Set the current position.
		 */
		currentPosition = position;

		switch (item.getType()) {
		case NEWS : 
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			showTabs = false;
			showRefresh = true;
			currentListFragmentString = getResources().getString(R.string.NEWS_ACTION);
			listFragment = new TheListFragment(currentListFragmentString);
			break;
		case SETTINGS : 
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			showTabs = false;
		    showRefresh = false;		    
			fragment = new SettingsFragment();
			break;
		case LOGOUT :
			//TODO Add logout confirmation???
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			showTabs = false;
		    showRefresh = false;
		    sh_Pref.edit().clear().commit();
			 Intent firstpage=new Intent(this,MainActivity.class);			 
			 startActivity(firstpage);
			break;
		case REPOSITORIES :
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			showTabs = false;
			showRefresh = true;
			currentListFragmentString = getResources().getString(R.string.REPO_ACTION);
			listFragment = new TheListFragment(currentListFragmentString);			
			break;
		case REPOSITORY:

			/**
			 * Store the repository in the shared preferences.
			 */
			sharedPreferences = getSharedPreferences(getResources().getString(R.string.SETTINGS_PREFERENCES),0);
			toEdit = sharedPreferences.edit();
			toEdit.putString(getResources().getString(R.string.CURRENT_REPOSITORY_PREFERENCE), item.getTitle());
	        toEdit.putBoolean(getResources().getString(R.string.HAS_CURRENT_REPOSITORY_PREFERENCE), true);
	        toEdit.commit();

			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			showTabs = true;
			showRefresh = true;
			currentListFragmentString = getResources().getString(R.string.REPO_NEWS_ACTION);
			listFragment = new TheListFragment(currentListFragmentString);
			newsListFragment = listFragment;
			break;
		case TASKS:
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			showTabs = false;
		    showRefresh = false;		    
			fragment = new TaskFragment();
			break;
		default:
			break;
		}

		invalidateOptionsMenu();

		if (fragment != null) {
			switchFragment(fragment);

			// update selected item and title, then close the drawer
			if(position < mDrawerList.getCount()){
				mDrawerList.setItemChecked(position, true);
				mDrawerList.setSelection(position);
				setTitle(navMenuTitles[position]);
			}

			mDrawerLayout.closeDrawer(mDrawerList);
		}else if(listFragment != null){
			switchFragment(listFragment);

			// update selected item and title, then close the drawer
			if(position>=0){
				if(position < mDrawerList.getCount()){
					mDrawerList.setItemChecked(position, true);
					mDrawerList.setSelection(position);
					//TODO fix titles.
					if(position < navMenuTitles.length)
						setTitle(navMenuTitles[position]);
				}			
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	/**
	 * Switches the current fragment.
	 * 
	 * @param fragment
	 */
	private void switchFragment(Fragment fragment){
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();
	}

	public void openRepository(String fullName){

		//======= Variables =======

		SharedPreferences sh_Pref;
		Editor toEdit;
		ListFragment listFragment;

		//===== Functionality =====

		/**
		 * Store the repository in the shared preferences.
		 */
		sh_Pref = getSharedPreferences(getResources().getString(R.string.SETTINGS_PREFERENCES),0);
		toEdit = sh_Pref.edit();
		toEdit.putString(getResources().getString(R.string.CURRENT_REPOSITORY_PREFERENCE), fullName);
        toEdit.putBoolean(getResources().getString(R.string.HAS_CURRENT_REPOSITORY_PREFERENCE), true);
        toEdit.commit();
        
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		showTabs = true;
		showRefresh = true;
		currentListFragmentString = getResources().getString(R.string.REPO_NEWS_ACTION);
		listFragment = new TheListFragment(currentListFragmentString);
		newsListFragment = listFragment;

		setTitle(fullName);

		switchFragment(listFragment);

	}
	
	/**
	 * Refreshes the current list fragment.
	 */
	private void refreshCurrentFragment(){
		
		//======= Variables =======
		
		ListFragment listFragment;
		
		//===== Functionality =====
		
		listFragment = new TheListFragment(currentListFragmentString);
		
		switchFragment(listFragment);
		
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
			//Log.println(Log.ASSERT, "REPO BUILDER:::", json.toString());

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

	/**
	 * Updates the intent if the activity is already started when it recieves an intent.
	 */
	@Override
	protected void onNewIntent (Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected void onResume () {
		super.onResume();

		Bundle extras = getIntent().getExtras();
		String action;
		if(extras != null) {
			action= extras.getString(Intent.EXTRA_TEXT);
			Log.println(Log.ASSERT, "resume", action);
			//TODO: set fragment according to action
		}
	}
}