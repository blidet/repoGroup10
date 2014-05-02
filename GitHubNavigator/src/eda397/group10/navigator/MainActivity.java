package eda397.group10.navigator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity {

	private SharedPreferences sh_Pref;
	private boolean authenticated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        sh_Pref = getSharedPreferences(getResources().getString(R.string.LOGIN_CREDENTIALS_PREFERENCE_NAME),0);
        authenticated = sh_Pref.getBoolean(getResources().getString(R.string.AUTH_PREFERENCE), false);

        if (savedInstanceState == null && !authenticated) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
        else if(authenticated){
        	startActivity(new Intent(this,AuthenticatedMainActivity.class));
        	finish();
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
    /** Called when the user clicks on logout 
    public void logout_action (View view) {
    	Intent intent = new Intent(this, LoginFragment.class);
    	startActivity(intent);
    	
    }  */
}
