package com.vzl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HelperDataBase extends SQLiteOpenHelper{
	
	String dbName,createQuery;
	Boolean stringsDefined=false;
/*
	public HeaderDataBase(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	public HelperDataBase(Context context, String dbName, String createQuery) {
		super(context, dbName, null, 1);
		// TODO Auto-generated constructor stub
		this.dbName=dbName;
		this.createQuery=createQuery;
		stringsDefined=true;
	}
	*/
	public HelperDataBase(Context context) {
		super(context, DBD.DB_FILE_NAME, null, 3);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		if(stringsDefined)
			db.execSQL(createQuery);
		else
		{
			db.execSQL(DBD.Q_CREATE_FOLDERS);
//			db.execSQL(DBD.Q_CREATE_COUNTERS);
			db.execSQL(DBD.Q_CREATE_ACTIVITIES_TABLE);
		}
			//db.execSQL("create table headers (id integer primary key autoincrement," +
				//"cat text not null, busy text not null);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if(stringsDefined)
			db.execSQL("drop table if exists "+dbName);
		else
		{
//			db.execSQL("alter table if exists "+DBD.TN_FOLDERS);
	//		db.execSQL("alter table if exists "+DBD.TN_COUNTERS);
		//	db.execSQL("alter table if exists "+DBD.TN_ACTIVITIES);
		}
		onCreate(db);
		
	}

}
