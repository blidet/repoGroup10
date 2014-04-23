package eda397.group10.adapters;

import java.util.ArrayList;

import eda397.group10.navigator.R;
import eda397.group10.navigator.TheListFragment;
import eda397.group10.pojo.PushEventPOJO;
import eda397.group10.pojo.RepositoryPOJO;
import eda397.group10.pojo.UserPOJO;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsListAdapter extends BaseAdapter {

	private ArrayList<PushEventPOJO> datas;
	private TextView actionText;
	private TextView time;
	private ImageView avatar;
	private LayoutInflater layoutInflater;
	private TheListFragment contex;
	
	public NewsListAdapter(TheListFragment contex,ArrayList<PushEventPOJO> datas,LayoutInflater layoutInflater){
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
		return 0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = layoutInflater.inflate(R.layout.news_list_row, parent, false);
		actionText = (TextView)convertView.findViewById(R.id.actiontext);
		avatar = (ImageView)convertView.findViewById(R.id.owner_icon2);
		
		PushEventPOJO push = datas.get(pos);
		UserPOJO user= push.getActor();
		String actorName = user.getName();
		String branch = push.getRef();
		String repoName = push.getRepoName();
		String action = actorName + " pushed to " + branch + " at " + repoName;
		Drawable imageDrawable = user.getAvatar();
		
		actionText.setText(action);
		avatar.setImageDrawable(imageDrawable);

		return convertView;
	}

}
