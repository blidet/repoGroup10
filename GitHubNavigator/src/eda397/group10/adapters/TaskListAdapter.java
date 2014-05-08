package eda397.group10.adapters;

import java.util.ArrayList;

import eda397.group10.navigator.R;
import eda397.group10.pojo.FilePOJO;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskListAdapter extends BaseAdapter {
	private ArrayList<FilePOJO> fileList;
	private LayoutInflater layoutInflater;
	
	public TaskListAdapter(ArrayList<FilePOJO> files, LayoutInflater layoutInflater) {
		this.fileList = files;
		this.layoutInflater = layoutInflater;
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
		TextView pathName = (TextView) rowView.findViewById(R.id.path_name);
		
		FilePOJO file = fileList.get(position);
		String path = file.getPath();
		
		pathName.setText(path);
		
		return rowView;
	}

}
