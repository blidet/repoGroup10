package eda397.group10.adapters;

import java.util.ArrayList;

import eda397.group10.database.PathDataBase;
import eda397.group10.navigator.R;
import eda397.group10.navigator.TaskFragment;
import eda397.group10.pojo.FilePOJO;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	private PathDataBase db;
	
	public TaskListAdapter(TaskFragment taskFragment, ArrayList<FilePOJO> files, LayoutInflater layoutInflater) {
		this.fileList = files;
		this.layoutInflater = layoutInflater;
		this.taskFragment = taskFragment;
		db = PathDataBase.getInstance(taskFragment.getActivity());
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
		View rowView = layoutInflater.inflate(R.layout.task_list_row, parent, false);
		final FilePOJO file = fileList.get(position);
		
		//Set row label
		TextView pathName = (TextView) rowView.findViewById(R.id.path_name);
		String filename = file.getFilename();
		pathName.setText(filename);
		
		//Create on-click-listener for checkbox
		CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.checkbox);
		//TODO: make checkbox checked if already in task
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//TODO: handle folders!
		        db.open();
				if (isChecked) {
					db.addPath(file.getFullUrl());
				} else {
					db.removePath(file.getFullUrl());
				}
				db.close();
			}
		});
		
		//Create on click listener for the row itself
		rowView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (file.getType().equals("tree")) {
					//TODO: create back stack
					taskFragment.showFolder(file.getFullUrl());
				}
			}
		});
		
		return rowView;
	}

}
