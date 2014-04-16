package eda397.group10.navigator;

import java.util.ArrayList;

import eda397.group10.pojo.RepositoryPOJO;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This is the adapter used to adapt the data fetched from the JSON array to the list,
 * since the each item of the list contains more than one value from of the JSON array,
 * it's better to set use the adapter.
 */
public class ListAdapter extends BaseAdapter {
	
	//private RepoListFragment contex;
	private ArrayList<RepositoryPOJO> datas;
	private TextView repoName;
	private TextView description;
	private TextView star;
	//private ImageView image;
	private LayoutInflater layoutInflater;
	
	
	public ListAdapter(RepoListFragment contex,ArrayList<RepositoryPOJO> datas,LayoutInflater layoutInflater){
		//this.contex = contex;
		this.datas = datas;
		this.layoutInflater = layoutInflater;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = layoutInflater.inflate (R.layout.repo_list_row, parent, false);
		repoName = (TextView)convertView.findViewById(R.id.repo_name);
		description = (TextView)convertView.findViewById(R.id.repo_discrib);
		star = (TextView)convertView.findViewById(R.id.star_count);
		//image = (ImageView)convertView.findViewById(R.id.owner_icon);
				
		RepositoryPOJO pojo = datas.get(pos);
		String des = pojo.getDescription();
		if(des.length()>40){
			des = des.substring(0, 39) + "...";
		}
		repoName.setText(pojo.getName());
		description.setText(des);
		star.setText(pojo.getStarCount());
		
		return convertView;
	}

}
