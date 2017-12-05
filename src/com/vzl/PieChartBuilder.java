/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vzl;

/*
 * diagr types*/

import java.util.Calendar;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chartdemo.demo.R;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PieChartBuilder extends Activity {
	
	Cursor cursorL;
	
  public static final String TYPE = "type";

  //private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN };

  private CategorySeries mSeries = new CategorySeries("");

  private DefaultRenderer mRenderer = new DefaultRenderer();

  private String mDateFormat;

  private GraphicalView mChartView;
  
  TextView intervalTextView;

  @Override
  protected void onRestoreInstanceState(Bundle savedState) {
    super.onRestoreInstanceState(savedState);
    mSeries = (CategorySeries) savedState.getSerializable("current_series");
    mRenderer = (DefaultRenderer) savedState.getSerializable("current_renderer");
    mDateFormat = savedState.getString("date_format");
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable("current_series", mSeries);
    outState.putSerializable("current_renderer", mRenderer);
    outState.putString("date_format", mDateFormat);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.report_chart_view);
    
    mRenderer.setApplyBackgroundColor(true);
    mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
    mRenderer.setChartTitleTextSize(20);
    mRenderer.setLabelsTextSize(15);
    mRenderer.setLegendTextSize(15);
    //mRenderer.setMargins(new int[] { 20, 30, 45, 0 });
    mRenderer.setStartAngle(270);
    
    intervalTextView=(TextView)findViewById(R.id.rc_interval_textView1);

  }

  @Override
  protected void onResume() {
    super.onResume();
    
	long msStartDate, msEndDate;
	
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());		
	int intervalLog=sp.getInt(GC.PV_KEY_INTERVAL_REPORT, 0);
	if(intervalLog == GC.MF_CUSTOM_INTERVAL)
	{
		msStartDate=GC.getSavedTime(getApplicationContext(), GC.MF_TARGET_TYPE_REPORT, true);
		msEndDate=GC.getSavedTime(getApplicationContext(), GC.MF_TARGET_TYPE_REPORT, false);
	}
	else
	{
		msStartDate=GC.getStartDate(intervalLog);
		Calendar cldr=Calendar.getInstance();
		msEndDate=cldr.getTimeInMillis();
	}

	intervalTextView.setText(GC.getIntervalName(intervalLog, getResources())+": ("+(String) DateFormat.format("MM/dd/yy",msStartDate)+
			" - "+(String) DateFormat.format("MM/dd/yy",msEndDate)+")");

	//intervalTextView.setText("from: "+(String) DateFormat.format("MM/dd/yy",msStartDate)+
		//	" to: "+(String) DateFormat.format("MM/dd/yy",msEndDate));
    
    mSeries.clear();
 	int rends=mRenderer.getSeriesRendererCount();
 	for(int i=0;i<rends;i++)
 		mRenderer.removeSeriesRenderer(mRenderer.getSeriesRendererAt(0));
    
         
    String rqs=new String("select * from "+DBD.TN_FOLDERS+
			" where "+DBD.CT_LINK_TYPE +" = "+String.valueOf(DBD.LT_COUNTER));    
    Cursor cursorC=DBD.dbr.rawQuery(rqs, null);    
        
    if(cursorC.getCount() == 0)
    	return;
    //startManagingCursor(cursorC);
    cursorC.moveToFirst();
    
    do{
    	rqs=new String("select sum("+DBD.CT_END_TIME+"-"+DBD.CT_START_TIME+")"+
    			" from "+DBD.TN_ACTIVITIES+
    			" where "+DBD.TN_ACTIVITIES+"."+DBD.CT_OWNER_ID+
    					" = "+String.valueOf(cursorC.getInt(0))+
    					" and "+DBD.CT_END_TIME+" > "+String.valueOf(msStartDate)+
    					" and "+DBD.CT_START_TIME+" < "+String.valueOf(msEndDate)
    					);
    	cursorL=DBD.dbr.rawQuery(rqs, null);
    	//startManagingCursor(cursorL);
    	cursorL.moveToFirst();
    	long tdlt=cursorL.getLong(0);
    	if(tdlt!=0)
    	{
		    mSeries.add(GC.getElapsedTimeInSeconds(tdlt), cursorC.getString(DBD.CTI_NAME), tdlt);
		    SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
		    renderer.setColor(DBD.getColor(cursorC.getInt(0)));
		    mRenderer.addSeriesRenderer(renderer);
    	}
    }while(cursorC.moveToNext());
    
    if (mChartView == null) {
      LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
      mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
      
      layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
          LayoutParams.FILL_PARENT));
    } else {
      mChartView.repaint();
    }
  }
}
