package eda397.group10.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import eda397.group10.database.BaseSqlitePath;

//Tool for adding, remove or check paths
public class DataBaseTools {
	private static DataBaseTools instance = null;
	 
	private static final int DB_VERSION = 1;
	private static final String DB_NAME_PATH = "Tasks_Data.db";
	private static final String DB_NAME_SHA = "Sha_Data.db";
	
	private static final String TASKS_DATA = "Tasks_Data";
	private static final String SHA_DATA = "Sha_Data";
	
	private static final String ROW_PATH = "Path";
	private static final String ROW_NAME = "Name";
	private static final String ROW_SHA = "Sha";
	
	//Database for Files
	private SQLiteDatabase bdd_files;
	//Database for repo
	private SQLiteDatabase bdd_repo;
 
	private BaseSqlitePath PathBaseSqlite;
	private BaseSqliteSha ShaBaseSqlite;
 
	private DataBaseTools(Context context){
		//Creating the DataBase
		PathBaseSqlite = new BaseSqlitePath(context, TASKS_DATA, null, DB_VERSION);
		ShaBaseSqlite = new BaseSqliteSha(context, SHA_DATA, null, DB_VERSION);
	}
	
	public static DataBaseTools getInstance(Context context) {
		if (instance == null) {
			instance = new DataBaseTools(context);
		}
		return instance;
	}
 

	public void open(){
		//Opening the DB in writing
		bdd_files = PathBaseSqlite.getWritableDatabase();
		bdd_repo = ShaBaseSqlite.getWritableDatabase();
	}
 
	public void close(){
		//Closing The DB in writing
		bdd_files.close();
		bdd_repo.close();
	}
 
	public SQLiteDatabase getBDDPath(){
		return bdd_files;
	}
	public SQLiteDatabase getBDDSha(){
		return bdd_repo;
	}
 
	public long addPath(String Path){
		//Creating a ContentValues (Same as HashMap)
		ContentValues values = new ContentValues();
		//Setting values
		values.put(ROW_PATH, Path);
		//Adding the value in the BDD
		return bdd_files.insert(TASKS_DATA, null, values);
	}
	
	public long addRepo(String repoName, String sha){
		//Creating a ContentValues (Same as HashMap)
		ContentValues values = new ContentValues();
		//Setting values
		values.put(ROW_NAME, repoName);
		values.put(ROW_SHA, sha);
		//Adding the value in the BDD
		return bdd_repo.insert(SHA_DATA, null, values);
	}
 
 
	public int removePath(String Path){
		//Deleting a path
		return bdd_files.delete(TASKS_DATA, ROW_PATH + " = '" +Path+"'", null);
	}
	
	public int removeSha(String nameRepo){
		//Deleting a repo and its sha
		return bdd_repo.delete(SHA_DATA, ROW_NAME + " = '" +nameRepo+"'", null);
	}
	
	//If the sha has changed
	public int updateSha(String nameRepo, String newSha){
		ContentValues values = new ContentValues();
		values.put(ROW_NAME, nameRepo);
		values.put(ROW_SHA, newSha);
		return bdd_repo.update(SHA_DATA, values, ROW_NAME + " = " +nameRepo, null);
	}
 
	//Return True if the path is find, false otherwise
	public Boolean findPath(String Path){
		//Finding a path in the database
		//String[] or String ???
		Cursor c = bdd_files.query(TASKS_DATA, new String[] {ROW_PATH}, ROW_PATH + " LIKE \"" + Path +"\"", null, null, null, null);
		return checkPath(c);
	}
 
	//Return the sha corresponding to the name
	public String getSha(String repoName){
		Cursor c = bdd_repo.query(SHA_DATA, new String[] {ROW_NAME, ROW_SHA}, ROW_NAME + " LIKE \"" + repoName +"\"", null, null, null, null);
		return cursorToSha(c);
	}
	
	private String cursorToSha(Cursor c){
		//If we can't find we return null
		// TODO : Throw an exception but this should not happen
		if (c.getCount() == 0)
			return null;
 
		//On the first element, since it's a primary key the is only one
		c.moveToFirst();
		String sha;
		//C.getString(0) contain the name of the repo and (1) the sha
		sha = c.getString(1);
		//closing the cursor
		c.close();
 
		//returning the sha
		return sha;
	}
	
	
	//Return True if the path is find, false otherwise
	private Boolean checkPath(Cursor c){
		//If the path isn't found
		if (c.getCount() == 0)
		{
			c.close();
			return false;
		}
		else
		{
			c.close();
			return true;
		}	
	}

}
