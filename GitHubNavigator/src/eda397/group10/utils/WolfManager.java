package eda397.group10.utils;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class WolfManager {
	
	public static void PlayNotificationSound(Context context){
		
		try {
		    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		    Ringtone r = RingtoneManager.getRingtone(context, notification);
		    r.play();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
	}

}
