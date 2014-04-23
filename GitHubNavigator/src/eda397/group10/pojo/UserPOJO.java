package eda397.group10.pojo;

import android.graphics.drawable.Drawable;

public class UserPOJO {
	
	private String name;
	private Drawable avatar;
	private int userId;
	
	public UserPOJO(int userId, String name, Drawable avatar){
		this.userId = userId;
		this.name = name;
		this.avatar = avatar;
	}

	public String getName() {
		return name;
	}

	public Drawable getAvatar() {
		return avatar;
	}
	
	public int getUserId(){
		return userId;
	}
	
	

}
