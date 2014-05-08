package eda397.group10.adapters;

import java.util.ArrayList;

import eda397.group10.navigator.R;
import eda397.group10.navigator.TheListFragment;
import eda397.group10.pojo.EventPOJO;
import eda397.group10.pojo.UserPOJO;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsListAdapter extends BaseAdapter {

	private ArrayList<EventPOJO> datas;
	private TextView actionText;
	private TextView time;
	private ImageView avatar;
	private LayoutInflater layoutInflater;
	private TheListFragment contex;
	
	public NewsListAdapter(TheListFragment contex,ArrayList<EventPOJO> datas,LayoutInflater layoutInflater){
		this.contex = contex;
		this.datas = datas;
		this.layoutInflater = layoutInflater;
	}
	
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		convertView = layoutInflater.inflate(R.layout.news_list_row, parent, false);
		actionText = (TextView)convertView.findViewById(R.id.actiontext);
		avatar = (ImageView)convertView.findViewById(R.id.owner_icon2);

		EventPOJO event = datas.get(pos);
		String type = event.getType();
		UserPOJO user= event.getActor();
		String actorName = user.getName();
		Bitmap bitmap = user.getAvatarBitmap();
		avatar.setImageBitmap(bitmap);
		String action = null;
		String repoName = event.getRepoName();
		switch(type){
		case "PushEvent":			
			String branch = event.getRef();	
			action = getBoldBlue(actorName) + " pushed to " + getBoldBlue(branch) + " at " + getBoldBlue(repoName);
			actionText.setText(Html.fromHtml(action));
			break;
		case "CreateEvent":
			String refType = event.getRefType();
			if(refType.equals("repository")){
				action = getBoldBlue(actorName) + " created repository " + getBoldBlue(repoName);
			}else{
				String ref = event.getRef();
				action = getBoldBlue(actorName) + " created " + getBoldBlue(refType) + " " +  getBoldBlue(ref) + " at " + getBoldBlue(repoName);
			}
			actionText.setText(Html.fromHtml(action));
			break;
		case "ForkEvent":
			action = getBoldBlue(actorName) + " forked " + getBoldBlue(repoName);
			actionText.setText(Html.fromHtml(action));
			break;
		case "IssueCommentEvent":
			action = getBoldBlue(actorName) + " commented on issue " + getBoldBlue(repoName + "#" + event.getIssueNumber());
			actionText.setText(Html.fromHtml(action));
			break;
		case "IssuesEvent":
			action = getBoldBlue(actorName) +" "+ event.getAction() + " issue " + getBoldBlue(repoName + "#" + event.getIssueNumber());
			actionText.setText(Html.fromHtml(action));
			break;
		case "CommitCommentEvent":
			String commitId = event.getCommitId().substring(0, 9);
			action = getBoldBlue(actorName) + " commented on commit " + getBoldBlue(repoName+"@"+commitId);
			actionText.setText(Html.fromHtml(action));
			break;
		}
		

		return convertView;
	}
	
	private String getBoldBlue(String target){
		return "<font color='#0020C2'><b>"+target+"</b></font>";
	}

}
