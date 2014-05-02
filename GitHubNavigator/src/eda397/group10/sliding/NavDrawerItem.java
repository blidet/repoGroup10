package eda397.group10.sliding;

public class NavDrawerItem {
	
	/**
	 * The different types of nav drawer items.
	 * 
	 * @author Oscar
	 *
	 */
	public enum NavDrawerItemType {REPOSITORY, NONE};
	
	/**
	 * The type of nav drawer item this is.
	 */
	private NavDrawerItemType type = NavDrawerItemType.NONE;
	
	private String title;
	private int icon;
	private String count = "0";
	// boolean to set visiblity of the counter
	private boolean isCounterVisible = false;
	
	public NavDrawerItem(){}

	public NavDrawerItem(String title, int icon){
		this.title = title;
		this.icon = icon;
	}
	
	public NavDrawerItem(String title, int icon, NavDrawerItemType type){
		this.title = title;
		this.icon = icon;
		this.type = type;
	}
	
	public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count){
		this.title = title;
		this.icon = icon;
		this.isCounterVisible = isCounterVisible;
		this.count = count;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public int getIcon(){
		return this.icon;
	}
	
	public String getCount(){
		return this.count;
	}
	
	public boolean getCounterVisibility(){
		return this.isCounterVisible;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setIcon(int icon){
		this.icon = icon;
	}
	
	public void setCount(String count){
		this.count = count;
	}
	
	public void setCounterVisibility(boolean isCounterVisible){
		this.isCounterVisible = isCounterVisible;
	}
	
	public NavDrawerItemType getType() {
		return type;
	}

	public void setType(NavDrawerItemType type) {
		this.type = type;
	}
}
