package eda397.group10.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import eda397.group10.database.BaseSqlite;

//Tool for adding, remove or check paths
public class PathDataBase {
	 
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "Tasks_Data.db";
 
	private static final String TASKS_DATA = "Tasks_Data";
	private static final String ROW_PATH = "Path";
 
	private SQLiteDatabase bdd;
 
	private BaseSqlite MyBaseSqlite;
 
	public PathDataBase(Context context){
		//Creating the DataBase
		MyBaseSqlite = new BaseSqlite(context, TASKS_DATA, null, DB_VERSION);
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
 
 
	public int removeTasks(String Path){
		//Deleting a path
		return bdd.delete(TASKS_DATA, ROW_PATH + " = " +Path, null);
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
}