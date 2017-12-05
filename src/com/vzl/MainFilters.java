package com.vzl;

import java.util.Calendar;
import java.util.Date;

import org.achartengine.chartdemo.demo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainFilters extends Activity{
	
	public static final String PREFS_NAME = "MyPrefsFile";
	static final int DATE_DIALOG_ID = 0;
	public static final String S_TIME_S = "S_TIME_S";


	
	int targetType,intervalReport,intervalLog;
	
	boolean startDateSelected;
	
	Calendar cldr;
	//SharedPreferences sp;
	
	Button buttonSetStart;
	Button buttonSetEnd;
	
	Spinner intervalSpinner;
	
	TextView startDate,customStartDate,customEndDate;
	
	DatePickerDialog datePickerDialog;
	//DatePicker datePicker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_filters);
		
		startDate=(TextView)findViewById(R.id.fl_textView4);
		customStartDate=(TextView)findViewById(R.id.mf_custom_start_textView2);
		customEndDate=(TextView)findViewById(R.id.mf_custom_end_textView2);
		
		//datePicker = datePickerDialog.getDatePicker();
		
		//sp=getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());		
		//long cTime=sp.getLong(S_TIME_S,0);
		targetType=sp.getInt(GC.PV_KEY_TARGET_TYPE, 0);
		//if(targetType == 0)		
			intervalLog=sp.getInt(GC.PV_KEY_INTERVAL_LOG, 0);
		//else
			intervalReport=sp.getInt(GC.PV_KEY_INTERVAL_REPORT, 0);
		
        intervalSpinner=(Spinner)findViewById(R.id.mf_interval_spinner2);
        ArrayAdapter<CharSequence> intervalAdapter = ArrayAdapter.createFromResource(this,
        		R.array.filter_intervals , android.R.layout.simple_spinner_item);
        intervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalSpinner.setAdapter(intervalAdapter);
        //intervalSpinner.setSelection(interval);
        intervalSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				// TODO Auto-generated method stub
				// Use MODE_WORLD_READABLE and/or MODE_WORLD_WRITEABLE to grant access to other applications
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = preferences.edit();
				if(targetType==0)
				{
					editor.putInt(GC.PV_KEY_INTERVAL_LOG, pos);
					intervalLog=pos;
				}
				else
				{
					editor.putInt(GC.PV_KEY_INTERVAL_REPORT, pos);
					intervalReport=pos;
				}
				editor.commit();						

				updateDates();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});

	        
	    Spinner targetSpinner=(Spinner)findViewById(R.id.mf_type_spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        		R.array.filter_targets , android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        targetSpinner.setAdapter(adapter);
        targetSpinner.setSelection(targetType);
        targetSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int pos, long arg3) {
				// TODO Auto-generated method stub
				//Toast.makeText(parent.getContext(), "The planet is " +
				//          parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
				targetType=pos;
				// Use MODE_WORLD_READABLE and/or MODE_WORLD_WRITEABLE to grant access to other applications
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt(GC.PV_KEY_TARGET_TYPE, pos);
				editor.commit();	
				
				if(targetType == 0)
					intervalSpinner.setSelection(intervalLog);
				else
					intervalSpinner.setSelection(intervalReport);
				
				updateDates();
				
				//((TextView)findViewById(R.id.fl_textView4)).setVisibility(View.VISIBLE);
				//((LinearLayout)findViewById(R.id.fl_linearLayout2)).setVisibility(View.INVISIBLE);
				//arg1.setVisibility(View.GONE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		cldr=Calendar.getInstance();
		//cldr.setTimeInMillis(cTime);
		
		buttonSetStart = (Button)findViewById(R.id.set_start_button);
		buttonSetEnd = (Button)findViewById(R.id.set_end_button2);
		
		buttonSetStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setTime(true);				
			}
		});
		
		buttonSetEnd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setTime(false);
			}
		});
	}

	void updateDates()
	{
		int interval;
		
		if(targetType==0)
		{
			interval = intervalLog;
		}
		else
		{
			interval = intervalReport;
		}

		if(interval == 5)
		{
			((LinearLayout)findViewById(R.id.fl_linearLayout1)).setVisibility(View.INVISIBLE);
			((LinearLayout)findViewById(R.id.fl_linearLayout2)).setVisibility(View.VISIBLE);					
			
			customStartDate.setText(GC.getSavedTimeString(getApplicationContext(),targetType, true));
			customEndDate.setText(GC.getSavedTimeString(getApplicationContext(),targetType, false));
		}
		else
		{
			((LinearLayout)findViewById(R.id.fl_linearLayout1)).setVisibility(View.VISIBLE);
			((LinearLayout)findViewById(R.id.fl_linearLayout2)).setVisibility(View.INVISIBLE);	
			
			startDate.setText((String)DateFormat.format("MM/dd/yy", GC.getStartDate(interval)));
		}		
	}

	
	
	
	void setTime(boolean startSelected)
	{
		startDateSelected=startSelected;
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());		
        //String sprqs=new String(SAVED_TIME_PREF+String.valueOf(targetType)+(startDateSelected ? "_START":"_END"));
        //long startTime=sp.getLong(sprqs, 0);
        long time=GC.getSavedTime(getApplicationContext(),targetType,startSelected);
		Calendar t_cldr=Calendar.getInstance();
        if(time != 0)t_cldr.setTimeInMillis(time);
		showDialog(DATE_DIALOG_ID);
		datePickerDialog.updateDate(t_cldr.get(Calendar.YEAR), t_cldr.get(Calendar.MONTH), t_cldr.get(Calendar.DAY_OF_MONTH));
	}
	
	
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) 
                {
                	cldr.set(Calendar.YEAR, year);
                	cldr.set(Calendar.MONTH, monthOfYear);
                	cldr.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                	
        	    	long cTime=cldr.getTimeInMillis();
    		        String sprqs=new String(GC.SAVED_TIME_PREF+String.valueOf(targetType)+(startDateSelected ? "_START":"_END"));
        	    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        	        SharedPreferences.Editor editor = sp.edit();
        	        editor.putLong(sprqs,cTime);
        	        editor.commit();
        			updateDates();
                }
            };
            
            @Override
            protected Dialog onCreateDialog(int id) {
                switch (id) {
                case DATE_DIALOG_ID:
                    datePickerDialog = new DatePickerDialog(this,
                                mDateSetListener,
                                cldr.get(Calendar.YEAR), cldr.get(Calendar.MONTH), cldr.get(Calendar.DAY_OF_MONTH));
                    return datePickerDialog;
                    
                case 2:
                	
                	AlertDialog.Builder builder;
                	AlertDialog alertDialog;

                	Context mContext = this;//getApplicationContext();
                	LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                	final View layout = inflater.inflate(R.layout.date_time_picker_dialog,null);
                	TimePicker timePicker=(TimePicker)layout.findViewById(R.id.timePicker1);
                	timePicker.setIs24HourView(true);

                	builder = new AlertDialog.Builder(mContext);
                	builder.setTitle("Set Date/Time");
                	builder.setView(layout);
                	builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							DatePicker datePicker=(DatePicker)layout.findViewById(R.id.datePicker1);
							int date=datePicker.getDayOfMonth();
							Calendar cldr=Calendar.getInstance();
							cldr.set(Calendar.DAY_OF_MONTH, date);
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
                	/*
            		//Context mContext = getApplicationContext();
            		Dialog dialog = new Dialog(this);

            		dialog.setContentView(R.layout.date_time_picker_dialog);
            		dialog.setTitle("Set Date/Time");	
            		//dialog.set
            		
                    return dialog;*/
                
                }	
                return null;
            }             
}
