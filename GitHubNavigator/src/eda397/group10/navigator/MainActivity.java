package eda397.group10.navigator;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    /**
     * This method sends the user to GitHubs registration page when
     * the "Register" button is clicked.
     * @param view
     */
    public void onClickRegister(View view) {
    	Intent internetIntent = new Intent(Intent.ACTION_VIEW,
    			Uri.parse("https://github.com/join"));
    	internetIntent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
    	internetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	this.startActivity(internetIntent);
    }
    
    /**
     * The user clicked the login button.
     * At the moment it is just a link to the project page.
     * TODO: check github credentials
     * @param view
     */
    public void onClickLogin(View view) {
    	Intent intent = new Intent(this, ProjectPageActivity.class);
    	startActivity(intent);
    }

}
