package com.vzl;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

public class GC {
	
	public final static int RES_ID = 1;
	public final static int MF_CUSTOM_INTERVAL = 5;
	public final static int MF_TARGET_TYPE_LOG = 0;
	public final static int MF_TARGET_TYPE_REPORT = 1;
	
	public static final String SAVED_TIME_PREF = "SAVED_TIME_PREF_";	
	
	public static final String CLICKED_ITEM_ID="CLICKED_ITEM_ID";
	public static final String ITEM_COLOR="ITEM_COLOR";
	public static final String ITEM_TYPE="ITEM_TYPE";
	public static final String CURRENT_PATH="CURRENT_PATH";
	public static final String NAMES_ITEM_ID="NAMES_ITEM_ID";
	public static final String ACTION_OP_CODE="ACTION_OP_CODE";
	
	public static final String PV_KEY_TARGET_TYPE = "PV_KEY_TARGET_TYPE";	 
	public static final String PV_KEY_INTERVAL_REPORT = "PV_KEY_INTERVAL_REPORT";	 
	public static final String PV_KEY_INTERVAL_LOG = "PV_KEY_INTERVAL_LOG";	 
	
	public static String getElapsedTimeInSeconds(long msElapsedTime)
	{
		String sd1 = new String(
				(String) DateUtils.formatElapsedTime((msElapsedTime) / 1000));
		String sts[]=sd1.split(":");		
		String sd2=new String();
		for(int i=0;i<sts.length;i++)
		{
			if(i>0)sd2+=":";
			sd2+=sts[i].trim();
		}
		//String sd2=new String(sd1.replace('\n',' '));
		
		return sd2;		
	}
	
	public static long getStartDate(int interval)
	{
		long time = 0;
		Calendar cldr=Calendar.getInstance();

		cldr.set(Calendar.HOUR_OF_DAY, 0);
		cldr.set(Calendar.MINUTE, 0);
		cldr.set(Calendar.SECOND, 0);
		cldr.set(Calendar.MILLISECOND, 0);

		switch(interval)
		{
		case 0:
			time=cldr.getTimeInMillis();
			break;
		case 1:
			cldr.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			time=cldr.getTimeInMillis();
			break;
		case 2:
			time=cldr.getTimeInMillis();
			time-=7*24*60*60*1000;
			break;
		case 3:
			cldr.set(Calendar.DAY_OF_MONTH, 1);
			time=cldr.getTimeInMillis();
			break;
		case 4:
			time=cldr.getTimeInMillis();
			time-=(long)30*24*60*60*1000;
			break;
		}
		return time;		
	}
	
	static long getSavedTime(Context context,int targetType,boolean isStart)
	{
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);		
        String sprqs=new String(SAVED_TIME_PREF+String.valueOf(targetType)+(isStart ? "_START":"_END"));
        return sp.getLong(sprqs, 0);
	}

	static String getSavedTimeString(Context context,int targetType,boolean isStart)
	{
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);		
        String sprqs=new String(SAVED_TIME_PREF+String.valueOf(targetType)+(isStart ? "_START":"_END"));
        return (String) DateFormat.format("MM/dd/yy",sp.getLong(sprqs, 0));
	}
	
	static String getIntervalName(int intervalIndex, Resources res)
	{
		String[] intervalsNames=res.getStringArray(R.array.filter_intervals);		
		return intervalsNames[intervalIndex];		
	}
}
