package eda397.group10.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import eda397.group10.navigator.R;

public class NotificationAlarm {
	/**
     * Starts an alarm that will initiate the poll for updates in github, and then repeat itself.
     */
    public void startAlarm(Context context) {
    	//Get the update interval set by user in settings
    	SharedPreferences prefs = context.getSharedPreferences(context.getResources().getString(R.string.SETTINGS_PREFERENCES),0);
    	int seconds = prefs.getInt(context.getResources().getString(R.string.SECONDS_BETWEEN_UPDATES), 60);
    	
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, EventService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
        
        am.cancel(pendingIntent); //cancel old alarms
        
        // seconds <= 0 means notifications are disabled
        if (seconds > 0) {
        	am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 1000,
                    seconds*1000, pendingIntent);
        } 
        
    }

}
