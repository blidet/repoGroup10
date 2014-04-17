package eda397.group10.navigator;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ListFragment;
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
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

		Spinner spinner = (Spinner) rootView.findViewById(R.id.interval_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.intervals_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

		SharedPreferences settingsPrefs = getActivity().getSharedPreferences(getResources().getString(R.string.SETTINGS_PREFERENCES),0);
		int selectedValue = settingsPrefs.getInt(getResources().getString(R.string.INTERVAL_SPINNER_SELECTED), 2);
		spinner.setSelection(selectedValue);

		return rootView;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, 
			int pos, long id) {
		//Value in spinner changed (notification interval)
		//TODO: changed interval doesnt take place until after the app is restarted
		SharedPreferences settingsPrefs = getActivity().getSharedPreferences(getResources().getString(R.string.SETTINGS_PREFERENCES),0);
		Editor toEdit = settingsPrefs.edit();
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
		case "Turn notifications off":
			seconds=0;
			break;
		default:
			Log.println(Log.ASSERT, "Spinner", "could not set interval");
			break;
		}
		toEdit.putInt(getResources().getString(R.string.SECONDS_BETWEEN_UPDATES), seconds);
		toEdit.commit();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		//TODO
		// Another interface callback
	}


}
