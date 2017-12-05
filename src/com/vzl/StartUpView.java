package com.vzl;

import java.util.Date;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TabHost;

public class StartUpView extends TabActivity{
	
	public static final String PREFS_NAME = "MyPrefsFile";
	public static final String S_TIME_S = "S_TIME_S";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		SharedPreferences sp=getSharedPreferences(PREFS_NAME, 0);
		long cTime=sp.getLong(S_TIME_S,0);
		
		if(cTime == 0)
		{
	    	Date currentDate=new Date(); 
	    	cTime=currentDate.getTime();
	        SharedPreferences.Editor editor = sp.edit();
	        editor.putLong(S_TIME_S,cTime);
	        editor.commit();			
		}

        final TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("Main",getResources().getDrawable(R.drawable.clock_dial))
                .setContent(new Intent(this, TimeCounterActivity.class)));

        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("Log",getResources().getDrawable(R.drawable.log))
                .setContent(new Intent(this, ActivitiesLog.class))); 	
/*
        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator("List")
                .setContent(new Intent(this, BusinessList.class))); 
        */
        tabHost.addTab(tabHost.newTabSpec("tab4")
                .setIndicator("Report",getResources().getDrawable(R.drawable.diagramms))
                .setContent(new Intent(this, PieChartBuilder.class))); 	
        
        tabHost.addTab(tabHost.newTabSpec("tab5")
                .setIndicator("Filters",getResources().getDrawable(R.drawable.filters))
        		.setContent(new Intent(this, MainFilters.class))); 	

        tabHost.addTab(tabHost.newTabSpec("tab6")
                .setIndicator("Options",getResources().getDrawable(R.drawable.tools))
                .setContent(new Intent(this, OptionsActivity.class))); 	
        		//.setContent(new Intent(this, CountersLinksManager.class).putExtra("GET_RES", false))); 	
    }

}
