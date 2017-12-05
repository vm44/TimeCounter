package com.vzl;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ActivitiesLogCursorAdapter extends SimpleCursorAdapter{

	Context context;
	Cursor c;
		
	public ActivitiesLogCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from,to);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.c=c;	
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row=convertView;

		if (row==null) 
		{
			LayoutInflater inflater=LayoutInflater.from(context);
			row=inflater.inflate(R.layout.activ_view, null);
		}
						
		c.moveToPosition(position);
		Date tDate=new Date();
		//long r_id=c.getLong(DBD.CTI_ID);
		long s_time=c.getLong(1);
		long e_time=c.getLong(2);
		int bgColor=DBD.getColor(c.getLong(4));
		tDate.setTime(s_time);
		((TextView)row.findViewById(R.id.start_textView3)).setText((String) DateFormat.format(" MM/dd kk:mm:ss",tDate));
		((TextView)row.findViewById(R.id.start_textView3)).setTextColor(bgColor^0x00aaaaaa);
		tDate.setTime(e_time);
		((TextView)row.findViewById(R.id.end_textView4)).setText((String) DateFormat.format(" MM/dd kk:mm:ss",tDate));
		//((TextView)row.findViewById(R.id.end_textView4)).setTextColor(bgColor^0x00808080);
		((TextView)row.findViewById(R.id.end_textView4)).setTextColor(bgColor^0x00aaaaaa);
		((TextView)row.findViewById(R.id.elapsed_textView1)).setText(GC.getElapsedTimeInSeconds((e_time-s_time)));
		//((TextView)row.findViewById(R.id.name_textView2)).setText(String.valueOf(c.getLong(4)));
		((TextView)row.findViewById(R.id.name_textView2)).setText(String.valueOf(DBD.getName(c.getLong(4))));
//		((TextView)row.findViewById(R.id.path_textView2)).setText(String.valueOf(r_id)+" "+String.valueOf(DBD.getPath(c.getLong(4))));
		((TextView)row.findViewById(R.id.path_textView2)).setText(String.valueOf(DBD.getPath(c.getLong(4))));
		((TextView)row.findViewById(R.id.path_textView2)).setTextColor(bgColor^0x00808080);
			
		row.setBackgroundColor(DBD.getColor(c.getLong(4)));
		
		return row;
	}

}
