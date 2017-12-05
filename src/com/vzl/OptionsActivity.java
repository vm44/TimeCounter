package com.vzl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class OptionsActivity extends Activity{
	
    private static final int DIALOG_YES_NO_MESSAGE_ALL = 1;
    private static final int DIALOG_YES_NO_MESSAGE = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options_layout);
		
		Button btec=(Button)findViewById(R.id.edit_counters_tree_button1);
		btec.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				Intent editTree=new Intent(OptionsActivity.this,CountersLinksManager.class);
				editTree.putExtra("GET_RES", false);
				startActivity(editTree);    					
			}
		});
		
		findViewById(R.id.remove_all_data_button2).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//DBD.dbw.execSQL("drop table if exists "+DBD.TN_FOLDERS);
				//DBD.dbw.execSQL("drop table if exists "+DBD.TN_ACTIVITIES);
				showDialog(DIALOG_YES_NO_MESSAGE_ALL);				
			}
		});

		findViewById(R.id.remove_values_button2).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				showDialog(DIALOG_YES_NO_MESSAGE);				
			}
		});
	}
	
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_YES_NO_MESSAGE_ALL:
            return new AlertDialog.Builder(OptionsActivity.this)
                //.setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.alert_dialog_two_buttons_title)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

        				DBD.dbw.execSQL("delete from "+DBD.TN_FOLDERS);
        				DBD.dbw.execSQL("delete from "+DBD.TN_ACTIVITIES);

        				ContextWrapper cw=new ContextWrapper(getBaseContext());
        	        	cw.deleteFile("myFile.bin");				
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Cancel so do some stuff */
                    }
                })
                .create();
        case DIALOG_YES_NO_MESSAGE:
            return new AlertDialog.Builder(OptionsActivity.this)
                //.setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.alert_dialog_delete_data)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
        				DBD.dbw.execSQL("delete from "+DBD.TN_ACTIVITIES);
        				ContextWrapper cw=new ContextWrapper(getBaseContext());
        	        	cw.deleteFile("myFile.bin");				
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Cancel so do some stuff */
                    }
                })
                .create();
        }
        return null;
    }
}
