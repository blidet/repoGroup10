package eda397.group10.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import eda397.group10.database.BaseSqlite;

//Tool for adding, remove or check paths
public class PathDataBase {
	private static PathDataBase instance = null;
	 
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "Tasks_Data.db";
 
	private static final String TASKS_DATA = "Tasks_Data";
	private static final String ROW_PATH = "Path";
 
	private SQLiteDatabase bdd;
 
	private BaseSqlite MyBaseSqlite;
 
	private PathDataBase(Context context){
		//Creating the DataBase
		MyBaseSqlite = new BaseSqlite(context, TASKS_DATA, null, DB_VERSION);
	}
	
	public static PathDataBase getInstance(Context context) {
		if (instance == null) {
			instance = new PathDataBase(context);
		}
		return instance;
	}
 

	public void open(){
		//Opening the DB in writing
		bdd = MyBaseSqlite.getWritableDatabase();
	}
 
	public void close(){
		//Closing The DB in writing
		bdd.close();
	}
 
	public SQLiteDatabase getBDD(){
		return bdd;
	}
 
	public long addPath(String Path){
		//Creating a ContentValues (Same as HashMap)
		ContentValues values = new ContentValues();
		//Setting values
		values.put(ROW_PATH, Path);
		//Adding the value in the BDD
		return bdd.insert(TASKS_DATA, null, values);
	}
 
 
	public int removePath(String Path){
		//Deleting a path
		return bdd.delete(TASKS_DATA, ROW_PATH + " = '" +Path+"'", null);
	}
 
	//Return True if the path is find, false otherwise
	public Boolean findPath(String Path){
		//Finding a path in the database
		//String[] or String ???
		Cursor c = bdd.query(TASKS_DATA, new String[] {ROW_PATH}, ROW_PATH + " LIKE \"" + Path +"\"", null, null, null, null);
		return checkPath(c);
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

	/**
	 * Get's the SHA of the last commit for the given repository
	 * @param repoName
	 * @return
	 */
	public String getSha(String repoName) {
		// TODO implement!!!!!
		return "7feee30b4cf00e2d591926058473090a0669e568";
	}

	/**
	 * Stores the SHA of the last commit for the given repository
	 * @param repoName
	 * @param newSha
	 */
	public void setSha(String repoName, String newSha) {
		// TODO implement!!!!
		
	}
}
