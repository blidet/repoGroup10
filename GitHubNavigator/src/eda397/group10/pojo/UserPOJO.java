package eda397.group10.pojo;

import android.graphics.drawable.Drawable;

public class UserPOJO {
	
	private String name;
	private Drawable avatar;
	private int userId;

	public String getName() {
		return name;
	}

	public Drawable getAvatar() {
		return avatar;
	}
	
	public int getUserId(){
		return userId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAvatar(Drawable avatar) {
		this.avatar = avatar;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	

}
