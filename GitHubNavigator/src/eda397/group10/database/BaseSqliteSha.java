package eda397.group10.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;


//This class create the database and initialize the Row
public class BaseSqliteSha extends SQLiteOpenHelper {
	 
	private static final String SHA_DATA = "Sha_Data";
	private static final String ROW_NAME = "Name";
	private static final String ROW_SHA = "Sha";
	
	private static final String CREATE_BDD = "CREATE TABLE " + SHA_DATA + " ("+
			ROW_SHA + " TEXT NOT NULL, " + ROW_NAME + " TEXT NOT NULL);";
 
	public BaseSqliteSha(Context context, String name, CursorFactory factory, int version) {
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
		db.execSQL("DROP TABLE " + SHA_DATA + ";");
		onCreate(db);
	}
}
