package eda397.group10.navigator;

import eda397.group10.notifications.NotificationService;
import android.support.v7.app.ActionBarActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
        
        startAlarm();
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
     * Starts an alarm that will initiate the poll for updates in github, and then repeat itself.
     */
    private void startAlarm() {
    	SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.SETTINGS_PREFERENCES),0);
        //int minutes = prefs.getInt("interval", 1);
    	int minutes =1;
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, NotificationService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        am.cancel(pi);
        // minutes <= 0 means notifications are disabled
        if (minutes > 0) {
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + minutes*60*1000,
                minutes*60*1000, pi);

            Log.println(Log.ASSERT, "alarm", "started");
        }
        
       // startService(new Intent(this, NotificationService.class));
    }

}
