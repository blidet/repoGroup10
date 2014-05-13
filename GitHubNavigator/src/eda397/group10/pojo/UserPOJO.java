package eda397.group10.pojo;

import android.graphics.Bitmap;

public class UserPOJO {
	
	private String name;
	private String avatarUrl;
	private int userId;
	private Bitmap avatarBitmap;

	public String getName() {
		return name;
	}
	
	public int getUserId(){
		return userId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public Bitmap getAvatarBitmap() {
		return avatarBitmap;
	}

	public void setAvatarBitmap(Bitmap avatarBitmap) {
		this.avatarBitmap = avatarBitmap;
	}
	
	

}
