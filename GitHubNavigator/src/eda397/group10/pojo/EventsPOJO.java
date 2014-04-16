package eda397.group10.pojo;

import java.util.ArrayList;

public class EventsPOJO {
	
	private String type;
	private String repo;
	private UserPOJO actor;
	private ArrayList<String> infoList;
	
	public EventsPOJO(String type, String repo, UserPOJO actor){
		this.type = type;
		this.repo = repo;
		this.actor = actor;
		this.infoList = new ArrayList<String>();
	}

	public String getType() {
		return type;
	}

	public String getRepo() {
		return repo;
	}
	
	public ArrayList<String> getInfo(){
		String s1 = "";
		String s2 = "";
		switch(type){
		  case "PushEvent":
			  s1 = actor.getName();
			  break;
		  case "WatchEvent":
			  break;
		  case "IssuesEvent":
			  break;
		  case "MemberEvent":
			  break;
		  case "CreateEvent":
			  break;
		  case "ForkEvent":
			  break;
		  default:
			  
		
		
		}
		return infoList;
	}
	

}
