package com.vzl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBD {
	
	private static int callDepth;
	
	public static long ownerId;

	public static HelperDataBase pDB;
	public static SQLiteDatabase dbr,dbw;
	
	public static final int LT_FOLDER=0;
	public static final int LT_COUNTER=1;
	//use for all
	public static final String CT_ID="_id";
	public static final String CT_OWNER_ID="CT_OWNER_ID";
	public static final String CT_FLAGS="CT_FLAGS";
	public static final String CT_COLOR="CT_COLOR";
	
	public static final String DB_FILE_NAME="db_main1";
	
	public static final String TN_COUNTERS="TN_COUNTERS";
	public static final String TN_FOLDERS="TN_FOLDERS";
	public static final String CT_NAME="CT_NAME";
	public static final String CT_LINK_TYPE="CT_LINK_TYPE";

	//for TN_COUNTERS_NAMES
	public static final String CT_USAGE_COUNTER="CT_USAGE_COUNTER";
	public static final String CT_SEQUENCE_COUNTER="CT_SEQUENCE_COUNTER";
	public static final String CT_SUM_TOTAL="CT_SUM_TOTAL";
	public static final String CT_SUM_TEMP="CT_SUM_TEMP";
	
	public static final String TN_ACTIVITIES="TN_ACTIVITIES";
	public static final String CT_START_TIME="CT_START_TIME";
	public static final String CT_END_TIME="CT_END_TIME";
	
	
	public static final String Q_CREATE_ACTIVITIES_TABLE="create table "+TN_ACTIVITIES+
		" ("+CT_ID+" integer primary key autoincrement, "+
			 CT_START_TIME+" integer,"+
			 CT_END_TIME+" integer,"+
			 CT_FLAGS+" integer,"+
			 CT_OWNER_ID+" integer);";

	public static final int CTI_ID=0;
	public static final int CTI_START_TIME=1;
	public static final int CTI_END_TIME=2;
	public static final int CTI_FLAGS=3;
	public static final int CTI_OWNER_ID=4;
	
	public static final String Q_CREATE_FOLDERS="create table "+TN_FOLDERS+
		" ("+CT_ID+" integer primary key autoincrement, "+
			 CT_NAME+" text not null, " +
			 CT_LINK_TYPE+" integer, "+
			 CT_COLOR+" integer, "+
			 CT_FLAGS+" integer, "+
			 CT_OWNER_ID+" integer"+
			 		");";

	//public static final int CTI_ID=0;
	public static final int CTI_NAME=1;
	public static final int CTI_LINK_TIPE=2;
	public static final int CTI_COLOR=3;
	//public static final int CTI_FLAGS=4;
	//public static final int CTI_OWNER_ID=4;
	/*
	public static final String Q_CREATE_COUNTERS="create table "+TN_COUNTERS+
	    " ("+CT_ID+" integer primary key autoincrement, "+
			 CT_NAME+" text not null, " +
			 CT_USAGE_COUNTER+" integer, "+
			 CT_SEQUENCE_COUNTER+" integer, "+
			 CT_SUM_TOTAL+" integer, "+
			 CT_SUM_TEMP+" integer, "+
			 CT_COLOR+" integer, "+
			 CT_FLAGS+" integer, "+
			 CT_OWNER_ID+" integer"+
			 		");";
	*/
	
	public static long getOwnerId(long id)
	{
		Cursor cursorL;
		
		cursorL=dbr.rawQuery("select * from "+DBD.TN_FOLDERS+
				" where "+DBD.CT_ID+" = "+String.valueOf(id), null);
		cursorL.moveToFirst();
		
		if(cursorL.getCount() == 0)
			return 0;
		else
			return cursorL.getLong(5);
	}
	
	public static String getName(long id)
	{
		Cursor cursorL;
		
		cursorL=dbr.rawQuery("select * from "+DBD.TN_FOLDERS+
				" where "+DBD.CT_ID+" = "+String.valueOf(id), null);
		cursorL.moveToFirst();
		
		if(cursorL.getCount() == 0)
			return "!noName!";
		else
			return cursorL.getString(1);
	}

	public static String getOwnersPath(long ownId)
	{
		Cursor cursorL;
		
		cursorL=dbr.rawQuery("select * from "+DBD.TN_FOLDERS+
				" where "+DBD.CT_ID+" = "+String.valueOf(ownId), null);
		//startManagingCursor(cursorL);
		cursorL.moveToFirst();
		
		if(cursorL.getCount() == 0)
			return new String("");
		//if(cursorL.getInt(5) == 0)
			//return new String("/");
		else
		{			
			String cName=new String(cursorL.getString(1));
			if((!cName.contentEquals("")))// && (callDepth>1))
				cName="/"+cName;
			//callDepth++;
			return getOwnersPath(cursorL.getLong(5))+cName;
		}
	}

	public static String getPath(long id)
	{
		callDepth=0;
		return getOwnersPath(getOwnerId(id));		
	}
	
	public static int getColor(long id)
	{
		String rqs=new String("select * from "+DBD.TN_FOLDERS+
				" where "+DBD.CT_ID+" = "+String.valueOf(id));
		Cursor cursorH=DBD.dbr.rawQuery(rqs, null);
		int bgColor=0;
		if(cursorH.getCount()!=0)
		{
			cursorH.moveToFirst();
			bgColor=cursorH.getInt(3);
		}		
		cursorH.close();
		return bgColor;
	}
	
	/*
	public static String getTableNamesTitle(int level)
	{
		return TN_NAMES+String.valueOf(level);
	}
	
	public static String getGroupTableName()
	{
		return TN_NAMES+String.valueOf(level);
	}
	
	public static String getParentTableName()
	{
		return TN_NAMES+String.valueOf(level+1);
	}

	public static String getChildrenTableName()
	{
		return TN_NAMES+String.valueOf(level-1);
	}
	*/
	/*
	public static final String Q_CREATE_NAMES_TABLE="create table "+TN_NAMES+
			 " ("+CT_ID+" integer primary key autoincrement, "+
			 CT_NAME+" text not null, " +
			 CT_FLAGS+" integer, "+
			 CT_OWNER_ID+" integer"+
			 		");";
			 		*/
/*	
	public static final String Q_CREATE_COUNTERS_TABLE="create table "+TN_COUNTERS_VALUES+
			 " ("+CT_ID+" integer primary key autoincrement, "+
			 CT_COUNTER_ID+" integer,"+
			 CT_COUNTER_VALUE+" integer);";
*/
}
