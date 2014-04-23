package eda397.group10.pojo;

public class PushEventPOJO{
	
	private UserPOJO actor;
	private String repoName;
	private String ref;
	private String actionTime;
	
	public PushEventPOJO(UserPOJO actor, String repoName, String ref, String actionTime){
		this.actor = actor;
		this.repoName = repoName;
		this.ref = ref;
		this.actionTime = actionTime;
	}

	public UserPOJO getActor() {
		return actor;
	}

	public String getRepoName() {
		return repoName;
	}

	public String getRef() {
		return ref;
	}

	public String getActionTime() {
		return actionTime;
	}
	
	

}
