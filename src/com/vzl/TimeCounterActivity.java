package com.vzl;

/*
 time project
 new
 pick
 start
 pause
 resume
 delete
 clear

 category edit
 add
 delete
 change

 switch

 detailed view
 day
 week
 month
 period
 layout by time
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chartdemo.demo.R;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import afzkl.development.mColorPicker.ColorPickerActivity;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TimeCounterActivity extends Activity {

	public final String BS_KEY_CURRENT_BUSINESS_ID = "BS_KEY_CURRENT_BUSINESS_ID";
	// public final String BS_KEY_BUSINESS_NAME="BS_KEY_BUSINESS_NAME";
	public final String BS_KEY_BUSINESS_TIME = "BS_KEY_BUSINESS_TIME";
	public final String BS_KEY_CNT_TIME = "BS_KEY_CNT_TIME";
	public final String PV_KEY_TODAY_PIE_CHART_MODE = "PV_KEY_TODAY_PIE_CHART_MODE";

	final long TOTAL_DAY_TIME_IN_SECONDS=24*60*60/2;
	//long
	
	Boolean countingTime = false;
	String currentBusinessName = new String();
	long msCurrentTime, msActivityStartTime,msElapsedTime;//,// msRunningTime,
			//;// ,cntSecs;
	int currentActivityID,todayPieChartMode,todayPieChartUpdatePrescaler=0;
	long lastRecordID=0;
	
	TextView currentActivityTitle, currentActivityStartTime,
			currentActivityRunningTime;
	
	DrawView drawView;

	Cursor cursorH, cursorC;

	private CategorySeries mSeries = new CategorySeries("");
	private DefaultRenderer mRenderer = new DefaultRenderer();
	private GraphicalView mChartView;
	
	int tcnt=0;
	
	static boolean updating=false;

	private Runnable doUpdateGUI = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			if(updating)
			{
				Log.d("updateGUI", "reEnter!!!!!!");
				return;
			}
			
			updating=true;
			
			if(currentActivityID != 0)
			{				
				Date currentDate = new Date();
				msCurrentTime = currentDate.getTime();
	
				msElapsedTime=msCurrentTime - msActivityStartTime;
				
				currentActivityRunningTime.setText(GC.getElapsedTimeInSeconds(msElapsedTime));
				
				if(todayPieChartUpdatePrescaler == 0)
				{	
					updatePieChart();
					todayPieChartUpdatePrescaler=9;

					ContentValues cv = new ContentValues();
					cv.put(DBD.CT_END_TIME, msCurrentTime);
					
					DBD.dbw.update(DBD.TN_ACTIVITIES, cv, 
							new String(DBD.CT_ID+" = ?"), 
							new String[]{String.valueOf(lastRecordID)});	
					
					//Toast.makeText(getApplicationContext(), String.valueOf(lastRecordID), Toast.LENGTH_SHORT).show();
				}
				else
					todayPieChartUpdatePrescaler--;
			}
			
			//Canvas pCanvas=((LinearLayout)findViewById(R.id.chart)).g;
			drawView.postInvalidate();
			
			mHandler.postDelayed(this, 1000);
			updating=false;
		}
	};

	Handler mHandler;

	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //String st[]={"33","22"};
        //Long l=new Long(st[1]);
        
        // Use MODE_WORLD_READABLE and/or MODE_WORLD_WRITEABLE to grant access to other applications
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        todayPieChartMode = preferences.getInt(PV_KEY_TODAY_PIE_CHART_MODE, 0);        
        
        currentActivityTitle=(TextView)findViewById(R.id.business_name);
        currentActivityStartTime=(TextView)findViewById(R.id.start_time);
        currentActivityRunningTime=(TextView)findViewById(R.id.running_time);
        
		DBD.pDB=new HelperDataBase(this);
		DBD.dbr=DBD.pDB.getReadableDatabase();
		DBD.dbw=DBD.pDB.getWritableDatabase();
		
		/*
		if(savedInstanceState == null)
        {
        	Toast.makeText(this, "onCreate: saved ins. state = null", Toast.LENGTH_SHORT).show();
        	        	        	
        }
        else
        {
        	Toast.makeText(this, "onCreate: restoring from Bundle", Toast.LENGTH_SHORT).show();
        	
        	currentActivityID=savedInstanceState.getInt(BS_KEY_CURRENT_BUSINESS_ID);
           	msActivityStartTime=savedInstanceState.getLong(BS_KEY_BUSINESS_TIME);
        }
        */      
		
        Button switchButton=(Button)findViewById(R.id.button_switch_to);
        switchButton.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showSelectCounterList();
				
				/*/test Charts
		        mSeries.add("Series " + (mSeries.getItemCount() + 1), 200);
		        SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
		        renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
		        mRenderer.addSeriesRenderer(renderer);
				*/
			}
		});
        
        Spinner todayPieChartModeSpinner=(Spinner)findViewById(R.id.today_pie_chart_mode_spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        		R.array.today_pie_chart_modes , android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        todayPieChartModeSpinner.setAdapter(adapter);
        todayPieChartModeSpinner.setSelection(todayPieChartMode);
        todayPieChartModeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int pos, long arg3) {
				// TODO Auto-generated method stub
				//Toast.makeText(parent.getContext(), "The planet is " +
				//          parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
				todayPieChartMode=pos;
				// Use MODE_WORLD_READABLE and/or MODE_WORLD_WRITEABLE to grant access to other applications
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt(PV_KEY_TODAY_PIE_CHART_MODE, pos);
				editor.commit();		
				updatePieChart();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        
        //test Charts
	    mRenderer.setApplyBackgroundColor(true);
	    mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
	    mRenderer.setChartTitleTextSize(20);
	    mRenderer.setLabelsTextSize(15);
	    mRenderer.setLegendTextSize(15);
	    mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
	    //mRenderer.setZoomButtonsVisible(true);
	    mRenderer.setStartAngle(270);	
	    mRenderer.setFitLegend(false);
        
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
          //      WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //requestWindowFeature(Window.LE);

        LinearLayout ll=(LinearLayout)findViewById(R.id.chart);
        //int top=ll.get;
        
        drawView = new DrawView(this,ll);
        addContentView(drawView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));	           

	}

	String getBusinessFullName(int id) {
		String cName=new String(DBD.getName(id));
		String cPath=new String(DBD.getPath(id));
		
		if(!cPath.contentEquals(""))
			cName += (" (" + DBD.getPath(id) + ")");
		
		return cName;
	}

	void showSelectCounterList() {
		/*
		Intent i = new Intent(this, ColorPickerActivity.class);
		i.putExtra(ColorPickerActivity.INTENT_DATA_INITIAL_COLOR, 0xffff0000);//prefs
			//	.getInt("activity", 0xff000000));
		startActivityForResult(i,ACTIVITY_COLOR_PICKER_REQUEST_CODE);
		*/
		Intent getCounterId = new Intent(this, CountersLinksManager.class);
		startActivityForResult(getCounterId, GC.RES_ID);
	}
	
	void addPie(String title,String titleLegend, long value,int color)
	{
        mSeries.add(title,titleLegend, value);
        SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
        renderer.setColor(color);
        mRenderer.addSeriesRenderer(renderer);		
	}

	long  addPie(long startDisplayTime,long startActTime,long endActTime,String title,String titleLegend,int color)
	{
 		if(startActTime<startDisplayTime)
 		{
 			startActTime=startDisplayTime;
 		}
 		
 		long span=(endActTime-startActTime);///1000;
 		 		
 		addPie(GC.getElapsedTimeInSeconds(span),titleLegend,span,color);
 		
 		return span;
	}
	
	String get__ElapsedTimeInSeconds(long msElapsedTime)
	{
		String sd1 = new String(
				(String) DateUtils.formatElapsedTime((msElapsedTime) / 1000));
		String sd2=new String(sd1.replace('\n',' '));
		
		return sd2;		
	}
	
	void updatePieChart()
	{
		float stAngle = 0;
		long nowMinutes,msSumOfLastTimePeriodActions;
		
	 	mSeries.clear();
	 	int rends=mRenderer.getSeriesRendererCount();
	 	for(int i=0;i<rends;i++)
	 		mRenderer.removeSeriesRenderer(mRenderer.getSeriesRendererAt(0));
		
		if(currentActivityID != 0)
		{
			Calendar cldr=Calendar.getInstance();
			
		 	nowMinutes=(cldr.get(Calendar.HOUR)*60+cldr.get(Calendar.MINUTE));
					
			Date currentDate = new Date();
			msCurrentTime = currentDate.getTime();
			msElapsedTime = msCurrentTime - msActivityStartTime;
			
			//long msTotalInPie=(todayPieChartMode == 0?24:12)*60*60*1000;
		 	
		 	switch(todayPieChartMode)
		 	{
		 	case 0:	 		
		 	case 1:	 		
			 	if(todayPieChartMode == 0)
			 		cldr.set(Calendar.HOUR_OF_DAY, 0);
			 	else
			 		cldr.set(Calendar.HOUR, 0);
			 	
			 	cldr.set(Calendar.MINUTE, 0);
			 	cldr.set(Calendar.SECOND, 0);
			 	cldr.set(Calendar.MILLISECOND, 0);
			 	
			 	stAngle=270;//360/(float)(12*60)*nowMinutes-90;
			 	
			 	long startDisplayTime=cldr.getTimeInMillis();//-12*60*60*1000;//last 12 hours
			 	
			 	String rqs=new String("select sum("+DBD.CT_END_TIME+"-"+DBD.CT_START_TIME+")"+
		    			" from "+DBD.TN_ACTIVITIES+
		    			" where "+DBD.CT_END_TIME+" > "+String.valueOf(startDisplayTime)+
		    			" and "+DBD.CT_ID+" != "+String.valueOf(lastRecordID));
		    	Cursor cursorL=DBD.dbr.rawQuery(rqs, null);
		    	
		    	//startManagingCursor(cursorL);
		    	cursorL.moveToFirst();
		    	msSumOfLastTimePeriodActions=cursorL.getLong(0);///1000;
			 			 	
			 	rqs=new String("select * from "+DBD.TN_ACTIVITIES+
			 			" where "+DBD.CT_END_TIME+" > "+String.valueOf(startDisplayTime)+
			 			" and "+DBD.CT_ID+" != "+String.valueOf(lastRecordID)+
			 			" order by "+DBD.CT_START_TIME);
			 	cursorL=DBD.dbr.rawQuery(rqs, null);
			 	cursorL.moveToFirst();
			 	
			 	int c=cursorL.getCount();
			 	long sumAct=0;
			 	/*
			 	if(msElapsedTime <0)// msTotalInPie)
			 	{
			 		long msToDraw=msElapsedTime%msTotalInPie;
			 		
			 		addPie(String.valueOf(msToDraw), msToDraw, 0xffff0000);
			 		addPie(String.valueOf(msTotalInPie-msToDraw), msTotalInPie-msToDraw, 0xffcccccc);
			 	}
			 	else
			 	{
				 */
			 		if(c>0)
				 	{	
				 		if(msSumOfLastTimePeriodActions<(msActivityStartTime-startDisplayTime))///1000)
				 		{
				 			cursorL.moveToFirst();
				 			long startActTime=cursorL.getLong(DBD.CTI_START_TIME);
					 		sumAct+=addPie(startDisplayTime, startDisplayTime, startActTime,//msCurrentTime-tdlt*1000,
					 				GC.getElapsedTimeInSeconds(msCurrentTime-startDisplayTime), "Uncounted" ,0xff444444);			 		
				 		}
				 		
					 	do{	 	
					 		long id=cursorL.getLong(0);
					 		if(id != lastRecordID)
					 		{
					 		long startActTime=cursorL.getLong(1);
					 		long endActTime=cursorL.getLong(2);
					 		int color=DBD.getColor(cursorL.getLong(4));
					 		
					 		sumAct+=addPie(startDisplayTime, startActTime, endActTime,
					 				GC.getElapsedTimeInSeconds(endActTime-startActTime),
					 				DBD.getName(cursorL.getLong(DBD.CTI_OWNER_ID)), color);
					 				//String.valueOf(id)+DBD.getName(cursorL.getLong(DBD.CTI_OWNER_ID)), color);
					 		}
					 		
					 	}while(cursorL.moveToNext());
				 	}
				 	else
				 	{
				 		if(startDisplayTime < msActivityStartTime)
				 			sumAct+=addPie(startDisplayTime, startDisplayTime, msActivityStartTime,
				 					GC.getElapsedTimeInSeconds(msCurrentTime-startDisplayTime),"Uncounted", 0xffaaaaaa);
				 				//(String)DateUtils.formatElapsedTime((msCurrentTime-startDisplayTime)/1000), 0xffaaaaaa);
				 		
				 	}
		
			 		sumAct+=addPie(startDisplayTime, msActivityStartTime, msCurrentTime,
			 				GC.getElapsedTimeInSeconds(msCurrentTime-msActivityStartTime),"Proceeding", 0xffff0000);
			 				//(String)DateUtils.formatElapsedTime((msCurrentTime-msActivityStartTime)/1000), 0xffff0000);
				 	
			        int rest=(int) ((todayPieChartMode == 0?24:12)*60*60*1000-sumAct);
				 	addPie(GC.getElapsedTimeInSeconds(rest),"Rest", rest, 0xffcccccc);
			 	//}
			 	cursorL.close();
		 		
		 		break;
		 			 		
		 	case 2:
		 		
		 		boolean bigSpan=false;
	
			 	stAngle=360/(float)(12*60)*nowMinutes-90;
			 	
			 	startDisplayTime=cldr.getTimeInMillis()-12*60*60*1000;//last 12 hours
		 
			 	rqs=new String("select sum("+DBD.CT_END_TIME+"-"+DBD.CT_START_TIME+")"+
		    			" from "+DBD.TN_ACTIVITIES+
		    			" where "+DBD.CT_END_TIME+" > "+String.valueOf(startDisplayTime)+
		    			" and "+DBD.CT_ID+" != "+String.valueOf(lastRecordID));
		    	cursorL=DBD.dbr.rawQuery(rqs, null);
		    	//startManagingCursor(cursorL);
		    	cursorL.moveToFirst();
		    	msSumOfLastTimePeriodActions=cursorL.getLong(0);///1000;
		 		 	
			 	rqs=new String("select * from "+DBD.TN_ACTIVITIES+
			 			" where "+DBD.CT_END_TIME+" > "+String.valueOf(startDisplayTime)+
			 			" and "+DBD.CT_ID+" != "+String.valueOf(lastRecordID)+
			 			" order by "+DBD.CT_START_TIME);
			 	cursorL=DBD.dbr.rawQuery(rqs, null);
			 	cursorL.moveToFirst();
			 	
			 	c=cursorL.getCount();
			 	sumAct=0;
			 	long span=0,preGap=0;
			 	int spanColor = 0;
			 	String spanName = null;
			 	//boolean overlapStartTime=false;
			 		 	
			 	if(c>0)
			 	{		 		
			 		//if(msSumOfLastTimePeriodActions<12*60*60*1000)
			 		///{
			 		//	sumAct=(12*60*60*1000-msSumOfLastTimePeriodActions-msElapsedTime);///1000;
			 		//}
			 		//else
			 		//{
				 		if(msSumOfLastTimePeriodActions<(msActivityStartTime-startDisplayTime))///1000)
				 		{
				 			cursorL.moveToFirst();
				 			long startActTime=cursorL.getLong(DBD.CTI_START_TIME);
					 		preGap=addPie(startDisplayTime, startDisplayTime, startActTime,//msCurrentTime-tdlt*1000,
					 				GC.getElapsedTimeInSeconds(msCurrentTime-startDisplayTime), "Uncounted", 0xff444444);	
					 		//preGap=sumAct;
				 		}
				 		
				 		if(preGap < 2*60*60*1000)
				 		{		 		
						 	while(sumAct<(2*60*60*1000-preGap))
					 		{	 				 		
						 		long startActTime=cursorL.getLong(DBD.CTI_START_TIME);
						 		long endActTime=cursorL.getLong(DBD.CTI_END_TIME);
						 		
						 		//overlapStartTime=false;
						 		if(startActTime<startDisplayTime)
						 		{
						 			startActTime=startDisplayTime;
						 			//overlapStartTime=true;
						 		}
						 		
						 		span=(endActTime-startActTime);///1000;
						 		
						 		//if(overlapStartTime)
							 		if(span>3*60*60*1000)
							 		{
							 			bigSpan=true;
							 			spanColor=DBD.getColor(cursorL.getLong(DBD.CTI_OWNER_ID));
							 			spanName=DBD.getName(cursorL.getLong(DBD.CTI_OWNER_ID));
							 			
							 		}
						 		
						 		sumAct+=span;		
						 		c--;
						 		if(!cursorL.moveToNext())
						 			break;
						 	}
			
				 		//}
				 		
				 		if(bigSpan)
				 		{
				 			sumAct-=span;
				 			addPie(GC.getElapsedTimeInSeconds(sumAct+2*60*60*1000-preGap),"Gap",sumAct+2*60*60*1000-preGap,0xffcccccc);
				 			addPie(GC.getElapsedTimeInSeconds(span-2*60*60*1000-preGap),
				 					spanName,
				 					span-2*60*60*1000-preGap,spanColor);
				 		}
				 		else
				 		{			 	
				 			addPie(GC.getElapsedTimeInSeconds(sumAct-preGap),"Gap",sumAct-preGap,0xffcccccc);
				 		}
				 	}
				 		
				 	if(c>0)
				 	{
					 	do{	 					 		
					 		long id=cursorL.getLong(0);
					 		if(id != lastRecordID)
					 		{
					 		long startActTime=cursorL.getLong(1);
					 		long endActTime=cursorL.getLong(2);
					 		int color=DBD.getColor(cursorL.getLong(4));
					 						 		
					 		sumAct+=addPie(startDisplayTime, startActTime, endActTime,
							 		GC.getElapsedTimeInSeconds(endActTime-startActTime),
							 		DBD.getName(cursorL.getLong(DBD.CTI_OWNER_ID)),color);				 		
					 				//(String)DateUtils.formatElapsedTime((endActTime-startActTime)/1000), color);
					 		}
					 	}while(cursorL.moveToNext());
				 	}
			 	}
			 	else
			 	{
			 	//if(c == 0)//empty db		 	
			 		msElapsedTime=12*60*60*1000-(msCurrentTime-msActivityStartTime);
			 		//msElapsedTime/=1000;
			 		
			 		if(msElapsedTime<=0)
			 		{
			 			msElapsedTime=20*60*1000;
			 			startDisplayTime+=20*60*1000;
			 		}
			 		addPie(GC.getElapsedTimeInSeconds(msElapsedTime),"Gap", msElapsedTime, 0xffcccccc);
	//		 		addPie("[gap] "+String.valueOf(msElapsedTime), msElapsedTime, 0xffcccccc);
			 	}
				 	
		 		addPie(startDisplayTime, msActivityStartTime, msCurrentTime,
				 		GC.getElapsedTimeInSeconds(msCurrentTime-msActivityStartTime),"Proceeding", 0xffff0000);				 			 				
		 				//(String)DateUtils.formatElapsedTime((msCurrentTime-msActivityStartTime)/1000), 0xffff0000);
		
			 	cursorL.close();
		 		break;
		 	}
		 	
		}
		 	
        mRenderer.setStartAngle(stAngle);
        
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
			mRenderer.setClickEnabled(true);
			mRenderer.setSelectableBuffer(20);
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	 	
		int i=Math.round(mRenderer.getLegendHeight());
		drawView.setShift(i);
		//Log.d("LEGEND_SIZE",String.valueOf(i));
	}

	protected void onActivityResult(int requestCode, int resCode, Intent intent) {

		//Toast.makeText(this, "onActivityResult:", Toast.LENGTH_SHORT).show();
		
		if (resCode == 0)
			return;

		if (currentActivityID == resCode) {
			//Toast.makeText(this, "Already Running", Toast.LENGTH_SHORT).show();
			return;
		}
/*
		Date currentDate = new Date();
		msCurrentTime = currentDate.getTime();
		// DateFormat dForm=new DateFormat();
		String sd = new String((String) DateFormat.format("MM/dd/yy kk:mm:ss",
				currentDate));
		currentActivityStartTime.setText("Start at: "+sd);

		currentBusinessName = getBusinessFullName(resCode);
		currentActivityTitle.setText("Proceeding: "+currentBusinessName);
*/
		Date currentDate = new Date();
		msCurrentTime = currentDate.getTime();

		if (currentActivityID != 0)//countingTime) {
		{
			// save prev
			/*
			ContentValues cv = new ContentValues();
			cv.put(DBD.CT_START_TIME, msActivityStartTime);
			cv.put(DBD.CT_END_TIME, msCurrentTime);
			cv.put(DBD.CT_OWNER_ID, currentActivityID);
			DBD.dbw.insertOrThrow(DBD.TN_ACTIVITIES, null, cv);
			*/
			ContentValues cv = new ContentValues();
			cv.put(DBD.CT_END_TIME, msCurrentTime);
			
			DBD.dbw.update(DBD.TN_ACTIVITIES, cv, 
					new String(DBD.CT_ID+" = ?"), 
					new String[]{String.valueOf(lastRecordID)});										
		}

		msActivityStartTime = msCurrentTime;
		currentActivityID = resCode;
		countingTime = true;

		ContentValues cv = new ContentValues();
		cv.put(DBD.CT_START_TIME, msActivityStartTime);
		cv.put(DBD.CT_END_TIME, msCurrentTime);
		cv.put(DBD.CT_OWNER_ID, currentActivityID);
		lastRecordID=DBD.dbw.insertOrThrow(DBD.TN_ACTIVITIES, null, cv);

		FileOutputStream fos = null;

		try {
			fos = openFileOutput("myFile.bin", Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DataOutputStream dos = new DataOutputStream(fos);

		try {
			dos.writeLong(msActivityStartTime);
			dos.writeInt(currentActivityID);
			dos.writeLong(lastRecordID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
		//stopManagingCursor(cursorC);
		mHandler.removeCallbacks(doUpdateGUI);
		super.onPause();
	}

	// @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
		super.onResume();
		
		currentActivityID=0;
		
    	FileInputStream fis = null;
    	
    	try {
			fis=openFileInput("myFile.bin");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	if(fis != null)
    	{        	
        	DataInputStream dis = new DataInputStream(fis);
        	
        	try {
        		msActivityStartTime=dis.readLong();
    			currentActivityID=dis.readInt();
    			lastRecordID=dis.readLong();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	
        	try {
    			dis.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	
        	try {
    			fis.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}   
    	}
		
		String sd=new String("00:00");
		String an=new String("Idle");
	 	currentActivityRunningTime.setText(sd);
		
    	if(currentActivityID != 0)
    	{
    		countingTime=true;
    		
    		an=getBusinessFullName(currentActivityID);    		
	 		sd=(String) DateFormat.format("MM/dd/yy kk:mm:ss",msActivityStartTime);
    	}
    	else
    	{
    		//if(mChartView!=null)
    			//mChartView.repaint();
    	}
    	
		currentActivityTitle.setText("Proceeding: "+an+": ");
	 	currentActivityStartTime.setText("Start at: "+sd);
			 	
	 	updatePieChart();

	 	if(mHandler == null)
			mHandler = new Handler();
			
		mHandler.removeCallbacks(doUpdateGUI);
		mHandler.postDelayed(doUpdateGUI, 1000);
	 	
	 	
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
		super.onRestart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
		mHandler.removeCallbacks(doUpdateGUI);
		super.onStop();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedState);
		mSeries = (CategorySeries) savedState.getSerializable("current_series");
		mRenderer = (DefaultRenderer) savedState
				.getSerializable("current_renderer");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt(BS_KEY_CURRENT_BUSINESS_ID, currentActivityID);
		outState.putLong(BS_KEY_BUSINESS_TIME, msActivityStartTime);
		outState.putSerializable("current_series", mSeries);
		outState.putSerializable("current_renderer", mRenderer);

		//Toast.makeText(this, "onSaveInsSt: save", Toast.LENGTH_SHORT).show();
	}
}