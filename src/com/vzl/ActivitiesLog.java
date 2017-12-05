package com.vzl;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ActivitiesLog extends ListActivity{

	Cursor cursorC;
	ActivitiesLogCursorAdapter adaptr;
	
	AlertDialog actions;
	TextView intervalTextView;
	long clickedListItemId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_list);
		
		AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
		alBuilder.setTitle("Choose Action");
		String[] options={"Edit","Insert","Remove"};
		alBuilder.setItems(options, onAlItemClick);
		actions=alBuilder.create();
		
		getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		getListView().setStackFromBottom(true);
		
		intervalTextView=(TextView)findViewById(R.id.bl_interval_textView1);
	}
	
	DialogInterface.OnClickListener onAlItemClick = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int which) {
			// TODO Auto-generated method stub
			switch(which)
			{
			case 0:
			case 1:
				Intent i =new Intent(ActivitiesLog.this, EditLogRecord.class);
				i.putExtra(GC.CLICKED_ITEM_ID, clickedListItemId);
				i.putExtra(GC.ACTION_OP_CODE, which);
				startActivity(i);				
				break;
			case 2:
				//stopManagingCursor(cursorC);
				
				String rqs=new String("delete from "+DBD.TN_ACTIVITIES+
									" where "+ DBD.CT_ID+ " = "+String.valueOf(clickedListItemId));
				DBD.dbw.execSQL(rqs);
				
				//cursorC=DBD.dbr.rawQuery("select * from "+DBD.TN_ACTIVITIES, null);
				//startManagingCursor(cursorC);
				//adaptr.changeCursor(cursorC);
				cursorC.requery();
				adaptr.notifyDataSetChanged();
				//adaptr.notifyDataSetInvalidated();
				break;
			}
			
		}
	};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		long msStartDate, msEndDate;
		
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());		
		int intervalLog=sp.getInt(GC.PV_KEY_INTERVAL_LOG, 0);
		if(intervalLog == GC.MF_CUSTOM_INTERVAL)
		{
			msStartDate=GC.getSavedTime(getApplicationContext(), GC.MF_TARGET_TYPE_LOG, true);
			msEndDate=GC.getSavedTime(getApplicationContext(), GC.MF_TARGET_TYPE_LOG, false);
		}
		else
		{
			msStartDate=GC.getStartDate(intervalLog);
			Calendar cldr=Calendar.getInstance();
			msEndDate=cldr.getTimeInMillis();
		}

		intervalTextView.setText(GC.getIntervalName(intervalLog, getResources())+": ("+(String) DateFormat.format("MM/dd/yy",msStartDate)+
				" - "+(String) DateFormat.format("MM/dd/yy",msEndDate)+")");
		
		cursorC=DBD.dbr.rawQuery("select * from "+DBD.TN_ACTIVITIES+
					" where "+DBD.CT_END_TIME+" > "+String.valueOf(msStartDate)+
					  " and "+DBD.CT_START_TIME+" < "+String.valueOf(msEndDate)+
					" order by "+DBD.CT_START_TIME, null);
		startManagingCursor(cursorC);
		
		adaptr=new ActivitiesLogCursorAdapter(this, R.layout.activ_view, 
				cursorC, 
				new String[]{DBD.CT_ID,DBD.CT_START_TIME,DBD.CT_END_TIME}, 
				new int[]{R.id.name_textView2,R.id.start_textView3,R.id.end_textView4});
		this.setListAdapter(adaptr);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		clickedListItemId=id;
		
		actions.show();
	}
	
}
