package eda397.group10.pojo;

public class FilePOJO {
	private String filename;
	private String type;
	private String fullUrl;

	public FilePOJO(String filename, String type, String fullUrl) {
		this.filename = filename;
		this.type = type;
		this.fullUrl = fullUrl;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getType() {
		return type;
	}

	public String getFullUrl() {
		return fullUrl;
	}
}
