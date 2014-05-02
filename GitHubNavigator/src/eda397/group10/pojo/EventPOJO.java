package eda397.group10.pojo;

public class EventPOJO {
	
	/**
	 * Comment used for commit events.
	 */
	private String comment;
	
	private UserPOJO actor;
	private String repoName;
	private String ref;
	private String refType;
	public String getRefType() {
		return refType;
	}
	public void setRefType(String refType) {
		this.refType = refType;
	}
	private String actionTime;
	private String type;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public UserPOJO getActor() {
		return actor;
	}
	public void setActor(UserPOJO actor) {
		this.actor = actor;
	}
	public String getRepoName() {
		return repoName;
	}
	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getActionTime() {
		return actionTime;
	}
	public void setActionTime(String actionTime) {
		this.actionTime = actionTime;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

}
