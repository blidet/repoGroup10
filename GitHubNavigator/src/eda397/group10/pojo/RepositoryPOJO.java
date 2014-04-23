package eda397.group10.pojo;

/*
 *This POJO is used to store the data of the repository JOSN array, when different arrays used, 
 *we may need to create different POJOs in the package, or create different constructor for the 
 *same POJO. 
 */

public class RepositoryPOJO{
	
	private String name;
	private String starCount;
	private String description;
	private UserPOJO owner;
	
	public RepositoryPOJO(String name,String starCount,String description,UserPOJO owner){
		this.name = name;
		this.starCount = starCount;
		this.description = description;
		this.owner = owner;
	}
	
	public String getName() {
		return name;
	}

	public String getStarCount() {
		return starCount;
	}

	public String getDescription() {
		return description;
	}

	public UserPOJO getOwner() {
		return owner;
	}
	
	

}
