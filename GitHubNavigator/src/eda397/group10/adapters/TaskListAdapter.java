package eda397.group10.adapters;

import java.util.ArrayList;

import eda397.group10.database.DataBaseTools;
import eda397.group10.navigator.AuthenticatedMainActivity;
import eda397.group10.navigator.R;
import eda397.group10.navigator.TaskFragment;
import eda397.group10.pojo.FilePOJO;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class TaskListAdapter extends BaseAdapter {
	private ArrayList<FilePOJO> fileList;
	private LayoutInflater layoutInflater;
	private TaskFragment taskFragment;
	private DataBaseTools db;
	private AuthenticatedMainActivity mainActivity;

	public TaskListAdapter(TaskFragment taskFragment, ArrayList<FilePOJO> files, LayoutInflater layoutInflater) {
		this.fileList = files;
		this.layoutInflater = layoutInflater;
		this.taskFragment = taskFragment;
		this.mainActivity = (AuthenticatedMainActivity)taskFragment.getActivity();
		db = DataBaseTools.getInstance(taskFragment.getActivity());
	}

	@Override
	public int getCount() {
		return fileList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return fileList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = null;
		final FilePOJO file = fileList.get(position);

		String filename = file.getFilename();
		
		if (file.getType().equals("tree")) {
			rowView = layoutInflater.inflate(R.layout.folder_list_row, parent, false);
			TextView folderName = (TextView) rowView.findViewById(R.id.folder_name);
			folderName.setText(filename);
			
			//Create on click listener for the row itself
			rowView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (file.getType().equals("tree")) {
						TaskFragment childFragment = new TaskFragment(file.getFullUrl(),false);
						mainActivity.tasksUrlStack.push(file.getFullUrl());
						mainActivity.taskFragId++;
						mainActivity.switchAndAddFragment(childFragment,Integer.toString(mainActivity.taskFragId));						
					}
				}
			});
			
			rowView.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View arg0) {
					// TODO Auto-generated method stub
					return false;
				}				
			});
			
		} else if (file.getType().equals("blob")) {
			//FILE
			rowView = layoutInflater.inflate(R.layout.file_list_row, parent, false);
			final CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.file_check);
			TextView fileName = (TextView) rowView.findViewById(R.id.file_name);
			fileName.setText(filename);
			
			SharedPreferences preferences = taskFragment.getActivity().getSharedPreferences(taskFragment.getResources().getString(R.string.SETTINGS_PREFERENCES),0);
			final String currentRepository = preferences.getString(taskFragment.getResources().getString(R.string.CURRENT_REPOSITORY_PREFERENCE), "none");
			
			db.open();
			//mainActivity.tasksUrlStack.push(file.getFullUrl());
			checkbox.setChecked(db.findPath(currentRepository+"/"+file.getFilename()));
			db.close();

			//Create on-click-listener for checkbox
			checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					db.open();
					if (isChecked) {
						db.addPath(currentRepository+"/"+file.getFilename());
					} else {
						db.removePath(currentRepository+"/"+file.getFilename());
					}
					db.close();
					Log.println(Log.ASSERT, "file", currentRepository + "/" + file.getFilename());
					
					//TODO: full path
				}
			});
					
			//Create on click listener for the row itself
			rowView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (file.getType().equals("blob")) {
						checkbox.setChecked(!checkbox.isChecked());
					}
				}
			});
		}
		
		return rowView;
	}


}
