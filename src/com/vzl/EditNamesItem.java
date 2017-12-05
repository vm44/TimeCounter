package com.vzl;

import afzkl.development.mColorPicker.ColorPickerActivity;
//import afzkl.development.mColorPicker.R;
import afzkl.development.mColorPicker.views.ColorPanelView;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class EditNamesItem extends Activity{
	
	private static final int ACTIVITY_COLOR_PICKER_REQUEST_CODE = 1000;
	
	private ColorPanelView mColorPanel;
	private EditText mNameEditText;
	private Spinner itemTypeSpinner;
	Cursor cursorC;

	int dbItemId = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_names_item);
		
		TextView pathTextView=(TextView)findViewById(R.id.path_textView3);
		
		Bundle b = getIntent().getExtras();
		
		//dbItemId=getIntent().getIntExtra(name, defaultValue)

		mNameEditText=(EditText)findViewById(R.id.item_name_editText1);
		mColorPanel = (ColorPanelView) findViewById(R.id.color_panel);				

		itemTypeSpinner=(Spinner)findViewById(R.id.item_type_spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        		R.array.item_type , android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemTypeSpinner.setAdapter(adapter);
		
		if (b != null) {
			dbItemId = b.getInt(GC.NAMES_ITEM_ID);
			pathTextView.setText(b.getCharSequence(GC.CURRENT_PATH));			
		}
		
		if(dbItemId != 0)
		{
			String rqs=new String("select * from "+DBD.TN_FOLDERS+
				" where "+DBD.CT_ID+" = "+String.valueOf(dbItemId));
			cursorC=DBD.dbr.rawQuery(rqs, null);
			cursorC.moveToFirst();
		
			mNameEditText.setText(cursorC.getString(1));
			itemTypeSpinner.setSelection(cursorC.getInt(2));
			itemTypeSpinner.setEnabled(false);
			int color=cursorC.getInt(3);
			if(color == 0)color=0xffffffff;
			mColorPanel.setColor(color);
		}
		else
		{
			mNameEditText.setText("");
			itemTypeSpinner.setSelection(b.getInt(GC.ITEM_TYPE, 0));
			mColorPanel.setColor(0xff333333);
		}
		
		Button btnSetColor=(Button)findViewById(R.id.set_color_button3);
		btnSetColor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startSelectColor();
			}
		});		
		
		Button acceptButton=(Button)findViewById(R.id.Accept_button1);
		acceptButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ContentValues cv=new ContentValues();
				cv.put(DBD.CT_NAME, mNameEditText.getText().toString());
				cv.put(DBD.CT_LINK_TYPE, itemTypeSpinner.getSelectedItemPosition());
				cv.put(DBD.CT_COLOR, mColorPanel.getColor());
				
				if(dbItemId == 0)
				{
					cv.put(DBD.CT_OWNER_ID, DBD.ownerId);					
					DBD.dbw.insert(DBD.TN_FOLDERS, null, cv);
				}
				else
				{
					String []args={String.valueOf(dbItemId)};
					DBD.dbw.update(DBD.TN_FOLDERS, cv, DBD.CT_ID+" = ?", args);//new String[]{String.valueOf(dbItemId)});
					//String rqs=new String("update "+DBD.TN_FOLDERS+
						//	" set "+DBD.CT_COLOR+" = "+String.valueOf(0xffffffff)+
							//" where "+DBD.CT_ID+" = "+String.valueOf(dbItemId));	
					//DBD.dbw.execSQL(rqs);
				}
				finish();
					
			}
		});
		
		Button cancelButton=(Button)findViewById(R.id.Cancel_button2);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	void startSelectColor()
	{
		Intent i = new Intent(this, ColorPickerActivity.class);
		i.putExtra(ColorPickerActivity.INTENT_DATA_INITIAL_COLOR, mColorPanel.getColor());
		startActivityForResult(i,ACTIVITY_COLOR_PICKER_REQUEST_CODE);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ACTIVITY_COLOR_PICKER_REQUEST_CODE
				&& resultCode == Activity.RESULT_OK) 
		{

			mColorPanel.setColor(data.getIntExtra(ColorPickerActivity.RESULT_COLOR, 0xff000000));

		}

	}
	

}

