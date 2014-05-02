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

public class RepoNewsListAdapter extends BaseAdapter {

	private ArrayList<EventPOJO> datas;
	private TextView actionText;
	private TextView time;
	private ImageView avatar;
	private LayoutInflater layoutInflater;
	private TheListFragment contex;
	
	public RepoNewsListAdapter(TheListFragment contex,ArrayList<EventPOJO> datas,LayoutInflater layoutInflater){
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
		
		EventPOJO event = datas.get(pos);
		String type = event.getType();
		UserPOJO user= event.getActor();
		String actorName = user.getName();
		Bitmap imageBitmap = user.getAvatarBitmap();
		avatar.setImageBitmap(imageBitmap);
		String action = null;
		String repoName = event.getRepoName();
		switch(type){
		case "PushEvent":			
			String branch = event.getRef();			
			action = actorName + " pushed to " + branch + " at " + repoName;	
			actionText.setText(action);
			break;
		case "CreateEvent":
			System.out.println("----------------------------------------------------------------------");
			String refType = event.getRefType();
			if(refType.equals("repository")){
				action = actorName + " created repository " + repoName;
			}else{
				String ref = event.getRef();
				action = actorName + " created " + refType + " " + ref + " at " + repoName;
			}
			actionText.setText(action);
			break;
		case "ForkEvent":
			action = actorName + " forked " + repoName;
			actionText.setText(action);
			break;
		case "commitEvent" :
			action = actorName + " : " + event.getComment();
			actionText.setText(action);
			break;
		}
		

		return convertView;
	}

}
