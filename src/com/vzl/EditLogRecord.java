package com.vzl;

import java.util.Calendar;
import java.util.Date;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

public class EditLogRecord extends Activity {
	
	static final int SETUP_DATE_TIME_DIALOG_ID=1;
	static final int DIALOG_LIST=2;
	static final int SETUP_TIME_SPAN_DIALOG_ID=4;
	static final int CHANGE_START=2;
	static final int CHANGE_END=3;
	
	AlertDialog alertDialog,ts_AlertDialog;
	TimePicker timePicker;
	DatePicker datePicker;
	EditText mTimeSpanEditText;
	RadioGroup mRadioGroupTimeSpan;
	Button acceptButton;
	
	boolean insertMode;
	
	int change_mode=0;
	long s_time,e_time;
	long itemId,ownerId,initOwnerId;
	int color;
	
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Put your code here
		setContentView(R.layout.edit_log_record_table);
		
		acceptButton=(Button)findViewById(R.id.elr_Accept_button2);
		
		itemId=getIntent().getLongExtra(GC.CLICKED_ITEM_ID, 0);
		insertMode=(getIntent().getIntExtra(GC.ACTION_OP_CODE, 0))==1?true:false;
		
		String rqs=new String("select * from "+DBD.TN_ACTIVITIES+
				" where "+DBD.CT_ID+" = "+String.valueOf(itemId));
		
		Cursor cursor=DBD.dbr.rawQuery(rqs, null);		
		cursor.moveToFirst();

		s_time=cursor.getLong(DBD.CTI_START_TIME);
		e_time=cursor.getLong(DBD.CTI_END_TIME);
		showDateTime(R.id.elr_StartTime_textView3,s_time);
		showDateTime(R.id.elr_EndTime_textView32,e_time);
		
		((TextView)findViewById(R.id.elr_TimeSpan_textView32)).setText(GC.getElapsedTimeInSeconds(e_time-s_time));
		
		ownerId=cursor.getLong(DBD.CTI_OWNER_ID);
		initOwnerId=ownerId;
		
		if(!insertMode)
		{
			showPathName(ownerId);		
			setColor();
		}
		else
		{
			((TextView)findViewById(R.id.elr_Name_textView4)).setText(R.string.select_activity_name);
			((TextView)findViewById(R.id.elr_Name_textView4)).setTextColor(0xffff0000);
			((TextView)findViewById(R.id.elr_Path_textView5)).setText("");		
			((LinearLayout)findViewById(R.id.elr_path_name_linearLayout3)).setBackgroundColor(0);
			acceptButton.setEnabled(false);
		}
		
		((Button)findViewById(R.id.elr_change_button1)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DIALOG_LIST);
				
			}
		});
		/*
		((Button)findViewById(R.id.elr_name_change_button1)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showSelectCounterList();
			}
		});
		
		((Button)findViewById(R.id.s_time_change_button1)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				change_mode=CHANGE_START;
				showDialog(SETUP_DATE_TIME_DIALOG_ID);
				alertDialog.setTitle("Set start date/time");
				initDateTimePickers(s_time);
			}
		});

		((Button)findViewById(R.id.e_time_change_button12)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				change_mode=CHANGE_END;
				showDialog(SETUP_DATE_TIME_DIALOG_ID);
				alertDialog.setTitle("Set end date/time");
				initDateTimePickers(e_time);
			}
		});
		
		((Button)findViewById(R.id.time_span_change_button12)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(SETUP_TIME_SPAN_DIALOG_ID);
			}
		});
		*/
		
		((Button)findViewById(R.id.elr_Accept_button2)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateDB();
				finish();
			}
		});
		
		((Button)findViewById(R.id.elr_Cancel_button3)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();				
			}
		});
	}
	
	void showSelectCounterList() 
	{
		Intent getCounterId = new Intent(this, CountersLinksManager.class);
		startActivityForResult(getCounterId, GC.RES_ID);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode != 0)
		{
			if(insertMode)
			{
				if(initOwnerId==resultCode)
				{
					acceptButton.setEnabled(false);
					return;
				}
				acceptButton.setEnabled(true);
			}
			ownerId=resultCode;
			showPathName(ownerId);
			setColor();
		}
		//super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	void showDateTime(int id,long s_time)
	{
		Date tDate=new Date();
		tDate.setTime(s_time);
		((TextView)findViewById(id))
		.setText((String) DateFormat.format(" MM/dd/yy kk:mm:ss",tDate));		
	}
	
	void showPathName(long ownerId)
	{
		((TextView)findViewById(R.id.elr_Name_textView4)).setText(DBD.getName(ownerId));
		((TextView)findViewById(R.id.elr_Path_textView5)).setText(DBD.getPath(ownerId));		
	}
	
	void setColor()
	{
		color=DBD.getColor(ownerId);
		LinearLayout ll=((LinearLayout)findViewById(R.id.elr_path_name_linearLayout3));
		ll.setBackgroundColor(color);
	}
	
	void initDateTimePickers(long i_time)
	{
		Calendar cldr=Calendar.getInstance();
		cldr.setTimeInMillis(i_time);
		datePicker.init(cldr.get(Calendar.YEAR),
						cldr.get(Calendar.MONTH),
						cldr.get(Calendar.DAY_OF_MONTH),null);
		timePicker.setCurrentHour(cldr.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(cldr.get(Calendar.MINUTE));		
	}
	
	void updateDB()
	{
		String rqs=new String("select * from "+DBD.TN_ACTIVITIES+
				" where "+DBD.CT_START_TIME+" < "+String.valueOf(e_time)+
				" and "+DBD.CT_END_TIME+" > "+String.valueOf(s_time));
		
		Cursor cursorL=DBD.dbr.rawQuery(rqs, null);
		
		if(cursorL.getCount()>0)
		{
			cursorL.moveToFirst();
			
			do{
				long currentDbId=cursorL.getLong(DBD.CTI_ID);
								
				if((!insertMode) && (currentDbId == itemId))
					continue;
				
				long ts_time=cursorL.getLong(DBD.CTI_START_TIME);
				long te_time=cursorL.getLong(DBD.CTI_END_TIME);
				
				if((ts_time > s_time) && (te_time < e_time))
				{
					//delete record
					DBD.dbw.delete(DBD.TN_ACTIVITIES,  
									new String(DBD.CT_ID+" = ?"), 
									new String[]{String.valueOf(currentDbId)});
				}
				else
				{
					boolean update=false;
					
					if(ts_time<s_time)
					{
						te_time=s_time;
						update=true;
					}
		
					if(te_time>e_time)
					{
						ts_time=e_time;
						update=true;
					}
					
					if(update)
					{
						ContentValues cv=new ContentValues();
						cv.put(DBD.CT_START_TIME, ts_time);
						cv.put(DBD.CT_END_TIME, te_time);
						DBD.dbw.update(DBD.TN_ACTIVITIES,cv,
								DBD.CT_ID+" = ?",new String[]{String.valueOf(currentDbId)});
					}
				}
				
			}
			while(cursorL.moveToNext());
		}
		cursorL.close();

		ContentValues cv=new ContentValues();
		cv.put(DBD.CT_START_TIME, s_time);
		cv.put(DBD.CT_END_TIME, e_time);
		cv.put(DBD.CT_OWNER_ID, ownerId);
		
		String []args={String.valueOf(itemId)};
		
		if(!insertMode)
		{
			DBD.dbw.update(DBD.TN_ACTIVITIES, cv, DBD.CT_ID+" = ?", args);
		}
		else
		{
			DBD.dbw.insert(DBD.TN_ACTIVITIES, null, cv);
		}
	}
	
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {

        case DIALOG_LIST:
            return new AlertDialog.Builder(EditLogRecord.this)
                .setTitle(R.string.elr_select_dialog)
                .setItems(R.array.elr_changeItemSelect, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    	
                    	switch(which)
                    	{
                    	case 0:
            				showSelectCounterList();
                    		break;
                    	case 1:
            				change_mode=CHANGE_START;
            				showDialog(SETUP_DATE_TIME_DIALOG_ID);
            				alertDialog.setTitle("Set start date/time");
            				initDateTimePickers(s_time);
            				break;
                    	case 2:
            				change_mode=CHANGE_END;
            				showDialog(SETUP_DATE_TIME_DIALOG_ID);
            				alertDialog.setTitle("Set end date/time");
            				initDateTimePickers(e_time);
            				break;
                    	case 3:
            				showDialog(SETUP_TIME_SPAN_DIALOG_ID);
            				break;
                    	}
                        /* User clicked so do some stuff */
                    	/*
                        String[] items = getResources().getStringArray(R.array.elr_changeItemSelect);
                        new AlertDialog.Builder(EditLogRecord.this)
                                .setMessage("You selected: " + which + " , " + items[which])
                                .show();
                        */
                    }
                })
                .create();
        
        case SETUP_DATE_TIME_DIALOG_ID:
        	
        	AlertDialog.Builder builder;

        	Context mContext = this;//getApplicationContext();
        	LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        	final View layout = inflater.inflate(R.layout.date_time_picker_dialog,null);
        	datePicker=(DatePicker)layout.findViewById(R.id.datePicker1);
        	timePicker=(TimePicker)layout.findViewById(R.id.timePicker1);
        	timePicker.setIs24HourView(true);        	

        	builder = new AlertDialog.Builder(mContext);
        	builder.setTitle("Set Date/Time");
        	builder.setView(layout);
        	builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Calendar cldr=Calendar.getInstance();
					cldr.set(Calendar.YEAR, datePicker.getYear());
					cldr.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
					cldr.set(Calendar.MONTH, datePicker.getMonth());
					cldr.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
					cldr.set(Calendar.MINUTE, timePicker.getCurrentMinute());
					cldr.set(Calendar.SECOND, 0);
					cldr.set(Calendar.MILLISECOND, 0);
					
					if(change_mode == CHANGE_START)
					{
						s_time=cldr.getTimeInMillis();
						showDateTime(R.id.elr_StartTime_textView3, s_time);		
						((TextView)findViewById(R.id.elr_StartTime_textView3)).setTextColor(0xffff0000);
					}

					if(change_mode == CHANGE_END)
					{
						e_time=cldr.getTimeInMillis();
						showDateTime(R.id.elr_EndTime_textView32, e_time);		
						((TextView)findViewById(R.id.elr_EndTime_textView32)).setTextColor(0xffff0000);
					}
				}
			});
        	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});

        	alertDialog = builder.create();    
        	                	
        	return alertDialog;
        
        	
        case SETUP_TIME_SPAN_DIALOG_ID:
        	
        	mContext = this;//getApplicationContext();
        	inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        	final View layout_ts = inflater.inflate(R.layout.edit_time_span,null);
        	mTimeSpanEditText=(EditText)layout_ts.findViewById(R.id.ts_editText1);
        	mRadioGroupTimeSpan=(RadioGroup)layout_ts.findViewById(R.id.ts_radioGroup1);

        	builder = new AlertDialog.Builder(mContext);
        	builder.setTitle("Set Time Span (HH:MM:SS)");
        	builder.setView(layout_ts);
        	mTimeSpanEditText.setText(((TextView)findViewById(R.id.elr_TimeSpan_textView32)).getText());
        	builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					String st=mTimeSpanEditText.getText().toString();
					((TextView)findViewById(R.id.elr_TimeSpan_textView32)).setText(st);
					((TextView)findViewById(R.id.elr_TimeSpan_textView32)).setTextColor(0xffff0000);
					String sTime[]=st.split(":");
					long tSum=0;
					long mult=1;
					for(int i=sTime.length;i>0;i--)
					{
						Long tValue=new Long(sTime[i-1]);
						tSum+=(tValue*mult);
						mult*=60;
					}
					
					//Long msNewTimeSpanHH=new Long(sTime[0]);
					//Long msNewTimeSpanMM=new Long(sTime[1]);
					long msNewTimeSpan=tSum*1000;
					int fromWhere=mRadioGroupTimeSpan.getCheckedRadioButtonId();
					if(fromWhere == R.id.from_start_radio0)
					{
						e_time=s_time+msNewTimeSpan;
						showDateTime(R.id.elr_EndTime_textView32,e_time);	
						((TextView)findViewById(R.id.elr_EndTime_textView32)).setTextColor(0xffff0000);
					}
					if(fromWhere == R.id.from_end_radio1)
					{
						s_time=e_time-msNewTimeSpan;
						showDateTime(R.id.elr_StartTime_textView3,s_time);
						((TextView)findViewById(R.id.elr_StartTime_textView3)).setTextColor(0xffff0000);
					}
					
				}
			});
        	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});

        	ts_AlertDialog = builder.create();    
        	                	
        	return ts_AlertDialog;
        
        }	
        return null;
    }             
	
}
