package eda397.group10.navigator;

import eda397.group10.notifications.NotificationAlarm;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

@SuppressLint("NewApi")
public class SettingsFragment extends Fragment implements OnItemSelectedListener{	
	Spinner notificationSpinner;
	Spinner intervalSpinner;
	Spinner vibrationSpinner;
	Spinner ledSpinner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		SharedPreferences settingsPrefs = getActivity().getSharedPreferences(getResources().getString(R.string.SETTINGS_PREFERENCES),0);


		//NOTIFICATION SETTING SPINNER
		notificationSpinner = (Spinner) rootView.findViewById(R.id.notification_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.on_off_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		notificationSpinner.setAdapter(adapter);
		notificationSpinner.setOnItemSelectedListener(this);
		int selectedValue = settingsPrefs.getInt(getResources().getString(R.string.CHECK_FOR_NOTIFICATIONS), 0);
		notificationSpinner.setSelection(selectedValue);


		//NOTIFICATION INTERVAL SPINNER
		intervalSpinner = (Spinner) rootView.findViewById(R.id.interval_spinner);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
				R.array.intervals_array, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		intervalSpinner.setAdapter(adapter2);
		int selectedValue2 = settingsPrefs.getInt(getResources().getString(R.string.INTERVAL_SPINNER_SELECTED), 2);
		intervalSpinner.setSelection(selectedValue2);
		intervalSpinner.setOnItemSelectedListener(this);
		
		
		//VIBRATION SPINNER
		vibrationSpinner = (Spinner) rootView.findViewById(R.id.vibration_spinner);
		ArrayAdapter<CharSequence> vibrationAdapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.on_off_array, android.R.layout.simple_spinner_item);
		vibrationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		vibrationSpinner.setAdapter(vibrationAdapter);
		int vibrationValue = settingsPrefs.getInt(getResources().getString(R.string.VIBRATION_VALUE_SELECTED), 0);
		vibrationSpinner.setSelection(vibrationValue);
		vibrationSpinner.setOnItemSelectedListener(this);
		
		//LED LIGHTS SPINNER
		ledSpinner = (Spinner) rootView.findViewById(R.id.led_spinner);
		ArrayAdapter<CharSequence> ledAdapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.on_off_array, android.R.layout.simple_spinner_item);
		ledAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ledSpinner.setAdapter(ledAdapter);
		int ledValue = settingsPrefs.getInt(getResources().getString(R.string.LED_LIGHT_VALUE_SELECTED), 0);
		ledSpinner.setSelection(ledValue);
		ledSpinner.setOnItemSelectedListener(this);
		
		return rootView;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, 
			int pos, long id) {
		SharedPreferences settingsPrefs = getActivity().getSharedPreferences(getResources().getString(R.string.SETTINGS_PREFERENCES),0);
		Editor toEdit = settingsPrefs.edit();
		if (parent.equals(notificationSpinner)) {
			toEdit.putInt(getResources().getString(R.string.CHECK_FOR_NOTIFICATIONS), pos);

			String selectedInterval = parent.getItemAtPosition(pos).toString();
			switch(selectedInterval) {
			case "On":
				int intervalPos = intervalSpinner.getSelectedItemPosition();
				intervalSpinner.setEnabled(true);
				vibrationSpinner.setEnabled(true);
				ledSpinner.setEnabled(true);
				onItemSelected(intervalSpinner, view, intervalPos, id);
				break;
			case "Off":
				toEdit.putInt(getResources().getString(R.string.SECONDS_BETWEEN_UPDATES), 0);
				intervalSpinner.setEnabled(false);
				vibrationSpinner.setEnabled(false);
				ledSpinner.setEnabled(false);
				break;
			default:
				Log.println(Log.ASSERT, "Spinner", "could not set interval");
				break;
			}

		} else if(parent.equals(intervalSpinner)) {
			//Value in spinner changed (notification interval)
			toEdit.putInt(getResources().getString(R.string.INTERVAL_SPINNER_SELECTED), pos);

			String selectedInterval = parent.getItemAtPosition(pos).toString();
			int seconds = 0;
			switch(selectedInterval) {
			case "15 seconds":
				seconds = 15;
				break;
			case "30 seconds":
				seconds = 30;
				break;
			case "1 minute":
				seconds = 1*60;
				break;
			case "5 minutes":
				seconds = 5*60;
				break;
			case "10 minutes":
				seconds = 10*60;
				break;
			case "15 minutes":
				seconds = 15*60;
				break;
			case "30 minutes":
				seconds = 30*60;
				break;
			default:
				Log.println(Log.ASSERT, "Spinner", "could not set interval");
				break;
			}
			toEdit.putInt(getResources().getString(R.string.SECONDS_BETWEEN_UPDATES), seconds);
		} else if (parent.equals(vibrationSpinner)) {
			toEdit.putInt(getResources().getString(R.string.VIBRATION_VALUE_SELECTED), pos);
		} else if (parent.equals(ledSpinner)) {
			toEdit.putInt(getResources().getString(R.string.LED_LIGHT_VALUE_SELECTED), pos);
		}

		toEdit.commit();
		
		//restart the notification alarm with the new interval settings
		NotificationAlarm alarm = new NotificationAlarm();
		alarm.startAlarm(getActivity());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		//TODO
		// Another interface callback
	}


}
