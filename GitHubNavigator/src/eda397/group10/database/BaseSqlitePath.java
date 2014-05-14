package eda397.group10.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;


//This class create the database and initialize the Row
public class BaseSqlitePath extends SQLiteOpenHelper {
	 
	private static final String TASKS_DATA = "Tasks_Data";
	private static final String ROW_PATH = "Path";
 
	private static final String CREATE_BDD = "CREATE TABLE " + TASKS_DATA + " ("
	 + ROW_PATH + " TEXT NOT NULL);";
 
	public BaseSqlitePath(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		//Creation of the DB with the string CREATE_BDD
		db.execSQL(CREATE_BDD);
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//In case of a new version we create the table again
		//To prevent from SQL changes
		db.execSQL("DROP TABLE " + TASKS_DATA + ";");
		onCreate(db);
	}
}
