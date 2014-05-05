package eda397.group10.adapters;

import java.util.ArrayList;

import eda397.group10.navigator.AuthenticatedMainActivity;
import eda397.group10.navigator.R;
import eda397.group10.navigator.TheListFragment;
import eda397.group10.pojo.RepositoryPOJO;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This is the adapter used to adapt the data fetched from the JSON array to the list,
 * since the each item of the list contains more than one value from of the JSON array,
 * it's better to set use the adapter.
 */
@SuppressLint("NewApi") public class RepoListAdapter extends BaseAdapter {
	
	private TheListFragment contex;
	private ArrayList<RepositoryPOJO> datas;
	private TextView repoName;
	private TextView description;
	private TextView star;
	private ImageView image;
	private LayoutInflater layoutInflater;
	private SharedPreferences sh_Pref;
	
	
	public RepoListAdapter(TheListFragment contex,ArrayList<RepositoryPOJO> datas,LayoutInflater layoutInflater){
		this.contex = contex;
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
	public View getView(final int pos, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = layoutInflater.inflate (R.layout.repo_list_row, parent, false);
		sh_Pref = contex.getActivity().getSharedPreferences(contex.getActivity().getResources().getString(R.string.LOGIN_CREDENTIALS_PREFERENCE_NAME),0);
		repoName = (TextView)convertView.findViewById(R.id.repo_name);
		description = (TextView)convertView.findViewById(R.id.repo_discrib);
		star = (TextView)convertView.findViewById(R.id.star_count);
		image = (ImageView)convertView.findViewById(R.id.owner_icon);
		
		convertView.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				Log.println(Log.ASSERT, "repo adapter", datas.get(pos).getFullName());
				
				/**
				 * Get the main activity.
				 */
				AuthenticatedMainActivity ama = (AuthenticatedMainActivity)contex.getActivity();
				
				/**
				 * Store the repository in the shared preferences.
				 */
				ama.openRepository(datas.get(pos).getFullName());
				
			}
		});
				
		RepositoryPOJO pojo = datas.get(pos);
		String des = pojo.getDescription();
		if(des.length()>40){
			des = des.substring(0, 39) + "...";
		}
		repoName.setText(pojo.getName());
		description.setText(des);
		star.setText(pojo.getStarCount());
		if(pojo.getOwner().getUserId()==sh_Pref.getInt("userId", 0000)){
			image.setImageResource(R.drawable.ic_repo_self);
		}else{
			image.setImageResource(R.drawable.ic_repo_others);
		}
		
		return convertView;
	}

}
