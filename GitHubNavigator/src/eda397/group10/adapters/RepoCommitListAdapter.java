package eda397.group10.adapters;

import java.util.ArrayList;

import eda397.group10.navigator.R;
import eda397.group10.navigator.TheListFragment;
import eda397.group10.pojo.EventPOJO;
import eda397.group10.pojo.RepositoryPOJO;
import eda397.group10.pojo.UserPOJO;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RepoCommitListAdapter extends BaseAdapter {

	private ArrayList<EventPOJO> datas;
	private TextView actionText;
	private TextView time;
	private ImageView avatar;
	private LayoutInflater layoutInflater;
	private TheListFragment contex;
	private TextView actorNameText;

	public RepoCommitListAdapter(TheListFragment contex,ArrayList<EventPOJO> datas,LayoutInflater layoutInflater){
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
		convertView = layoutInflater.inflate(R.layout.repo_commit_list_row, parent, false);
		actionText = (TextView)convertView.findViewById(R.id.commit_action);
		avatar = (ImageView)convertView.findViewById(R.id.commit_owner_icon2);
		actorNameText = (TextView)convertView.findViewById(R.id.commit_actor);

		EventPOJO event = datas.get(pos);
		String type = event.getType();
		UserPOJO user= event.getActor();
		String actorName = user.getName();
		Bitmap imageBitmap = user.getAvatarBitmap();
		if(!actorName.equals("Unknown")){
			avatar.setImageBitmap(imageBitmap);
		}else{
			avatar.setImageResource(R.drawable.ic_avatar);
		}
		
		String action = null;
		switch(type){
		
		case "commitEvent" :
			action = event.getComment();			
			if(action.length()>40){
				action = action.substring(0, 39) + "...";
			}
			
			actionText.setText(action);
			
			actorNameText.setText(actorName);
			
			break;
		}


		return convertView;
	}

}