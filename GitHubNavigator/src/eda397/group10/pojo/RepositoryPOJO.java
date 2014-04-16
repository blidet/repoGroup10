package eda397.group10.pojo;

/*
 *This POJO is used to store the data of the repository JOSN array, when different arrays used, 
 *we may need to create different POJOs in the package, or create different constructor for the 
 *same POJO. 
 */

public class RepositoryPOJO {
	
	private String name;
	private String starCount;
	private String description;
	private String ownerUrl;
	
	public RepositoryPOJO(String name,String starCount,String description,String ownerUrl){
		this.name = name;
		this.starCount = starCount;
		this.description = description;
		this.ownerUrl = ownerUrl;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStarCount() {
		return starCount;
	}
	public void setStarCount(String starCount) {
		this.starCount = starCount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOwnerUrl() {
		return ownerUrl;
	}
	public void setOwnerUrl(String ownerUrl) {
		this.ownerUrl = ownerUrl;
	}
	
	

}