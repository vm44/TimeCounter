package com.vzl;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

public class CountersLinksManager extends ListActivity{

    private static final int DIALOG_LIST = 3;
    private static final int DIALOG_SINGLE_CHOICE = 5;
    private static final int DIALOG_TEXT_ENTRY = 7;	

    private static final int MS_ADD_FOLDER = 0;	
    private static final int MS_ADD_COUNTER = 1;	
    private static final int MS_EDIT_ITEM = 2;	
    private static final int MS_DELETE_ITEM = 3;	

    boolean getResult;
    
    int menuItemId,choiceListId,selectedItemId;
	
	String strName;
	
	ArrayList<Long> backSteps=new ArrayList<Long>();

	Cursor cursorG,cursorC;
	SimpleCursorAdapter adaptr;
	
	TextView path;
	
	Button backButton;
	
	//tmp
	String bName=new String("b");
	
	static final ViewBinder VIEW_BINDER = new ViewBinder() {
		
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// TODO Auto-generated method stub
			if(view.getId() != android.R.id.text1)
				return false;

			String str = null;
			int lt=cursor.getInt(2);
			if(lt == DBD.LT_FOLDER)
				str=new String("[+] ");
			if(lt == DBD.LT_COUNTER)
				str=new String("");
			
			str+=new String(cursor.getString(columnIndex));
			//CharSequence cs=cursor.g
			((TextView) view).setText((CharSequence)str.subSequence(0, str.length()));
			
			view.setBackgroundColor(cursor.getInt(3));
			
			return true;
		}
	};
	
	void setCursorDataAtOwnerId(long id)
	{
		//
		DBD.ownerId=id;
		
		stopManagingCursor(cursorG);
		
		cursorG=DBD.dbr.rawQuery("select * from "+DBD.TN_FOLDERS+
				" where "+DBD.CT_OWNER_ID+" = "+String.valueOf(DBD.ownerId)+
				" order by "+DBD.CT_LINK_TYPE+", "+DBD.CT_NAME, null);
//				" order by "+DBD.CT_NAME+", "+DBD.CT_LINK_TYPE, null);
		
		startManagingCursor(cursorG);		

		adaptr.changeCursor(cursorG);
        adaptr.notifyDataSetChanged();   
      //  adaptr.notifyDataSetInvalidated();
//*/
        //cursorG.requery();
		//adaptr.notifyDataSetChanged();
        
        backButton.setEnabled(id==0?false:true);
                	
	}
	
	
	void removeSubItems(long ownId)
	{
		Cursor cursorL;
		
		cursorL=DBD.dbr.rawQuery("select * from "+DBD.TN_FOLDERS+
				" where "+DBD.CT_OWNER_ID+" = "+String.valueOf(ownId), null);
		startManagingCursor(cursorL);
		cursorL.moveToFirst();
		
		Log.d("removeSubItems, ownId="+ownId,"subItemsCount="+cursorL.getCount());
		
		if(cursorL.getCount()>0)
			do
			{
				removeSubItems(cursorL.getLong(0));
				
				String rqs=new String("delete from "+DBD.TN_FOLDERS+
						" where "+DBD.CT_ID+" = "+String.valueOf(cursorL.getLong(0)));
				DBD.dbw.execSQL(rqs);
				
				rqs=new String("delete from "+DBD.TN_ACTIVITIES+
						" where "+DBD.CT_OWNER_ID+" = "+String.valueOf(cursorL.getLong(0)));
				DBD.dbw.execSQL(rqs);

				Log.d("execSQL",rqs);
			}while(cursorL.moveToNext());				
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.counters_links_manager);
        
        Intent sIntent=getIntent();
        getResult=sIntent.getBooleanExtra("GET_RES", true);

		adaptr=new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,cursorG, new String[]{DBD.CT_NAME}, new int[]{android.R.id.text1});
		setListAdapter(adaptr);	
		adaptr.setViewBinder(VIEW_BINDER);
				
		backButton=(Button)findViewById(R.id.back_button1);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!backSteps.isEmpty())
				{
					Long ownId=backSteps.get(backSteps.size()-1);
					backSteps.remove(backSteps.size()-1);
					
					setCursorDataAtOwnerId(ownId);					
			        path.setText(DBD.getOwnersPath(ownId));
				}
			}
		});
		
		setCursorDataAtOwnerId(0);		
		
		registerForContextMenu(getListView());

		path=(TextView)findViewById(R.id.path_textView1);
		path.setText(DBD.getOwnersPath(DBD.ownerId));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		
		MenuInflater menuInflater=getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		//return super.onOptionsItemSelected(item);
		menuItemId=item.getItemId();
		selectedItemId=0;
		switch(item.getItemId())
		{
		case R.id.add_folder:
			menuItemId=MS_ADD_FOLDER;
			editItem(menuItemId);
			return true;
		case R.id.add_counter:
			menuItemId=MS_ADD_COUNTER;
			editItem(menuItemId);
			return true;
		}
		return false;
	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Edit List");
        menu.add(0,MS_ADD_FOLDER,0,"Add Folder");
        menu.add(0,MS_ADD_COUNTER,0,"Add Counter");
        menu.add(0,MS_EDIT_ITEM,0,"Edit");
        menu.add(0,MS_DELETE_ITEM,0,"Delete");
    }

    void editItem(int menuItemId)
    {
    	switch(menuItemId)
    	{
    	case MS_ADD_FOLDER:
    	case MS_ADD_COUNTER:
    	case MS_EDIT_ITEM:
    		
    		Intent i=new Intent(this, EditNamesItem.class);

    		if(menuItemId != MS_EDIT_ITEM)
    		{	
    			selectedItemId=0;
        		//int itemType = 0;
        		//if(menuItemId==R.id.add_folder)itemType=0;
        		//if(menuItemId==)itemType=1;
        		i.putExtra(GC.ITEM_TYPE, menuItemId);
    		}
    		
    		i.putExtra(GC.NAMES_ITEM_ID, selectedItemId);
    		i.putExtra(GC.CURRENT_PATH, path.getText());
    		startActivity(i);
    		break;

    	case MS_DELETE_ITEM:
    		//showDialog(DIALOG_SINGLE_CHOICE);
    		//break;
    		
    		removeSubItems(selectedItemId);
    		
    		String rqs=new String("delete from "+DBD.TN_FOLDERS+
    				" where "+DBD.CT_ID+" = "+String.valueOf(selectedItemId));
    		DBD.dbw.execSQL(rqs);

			rqs=new String("delete from "+DBD.TN_ACTIVITIES+
					" where "+DBD.CT_OWNER_ID+" = "+String.valueOf(selectedItemId));
			DBD.dbw.execSQL(rqs);
    		
    		setCursorDataAtOwnerId(DBD.ownerId);
    		break;
    	}        	        	
    	
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	
    	menuItemId=item.getItemId();
    	
    	AdapterContextMenuInfo itemInfo=(AdapterContextMenuInfo) item.getMenuInfo();
    	selectedItemId=(int) itemInfo.id;
    	
    	editItem(menuItemId);
    	
        return true;
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	// TODO Auto-generated method stub
    	//return super.onCreateDialog(id);
        // This example shows how to add a custom layout to an AlertDialog
    switch(id)
    {
    case DIALOG_LIST:
        return new AlertDialog.Builder(CountersLinksManager.this)
            .setTitle(R.string.select_dialog)
            .setItems(R.array.countersLinksManager_ContextMenuItems, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	editItem(which);
                }
            })
            .create();
    
    
    case DIALOG_SINGLE_CHOICE:
    	choiceListId=0;
        return new AlertDialog.Builder(CountersLinksManager.this)
            //.setIconAttribute(android.R.attr.alertDialogIcon)
            .setTitle(R.string.alert_dialog_single_choice)
            .setSingleChoiceItems(R.array.select_dialog_items2, 0, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    /* User clicked on a radio button do some stuff */
                   	choiceListId=whichButton;
                }
            })
            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    /* User clicked Yes so do some stuff */
                	//removeItems();
                }
            })
            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked No so do some stuff */
                }
            })
           .create();
    case DIALOG_TEXT_ENTRY:
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
        return new AlertDialog.Builder(this)//AlertDialogSamples.this)
            //.setIconAttribute(android.R.attr.dialogIcon)
            .setTitle(R.string.alert_dialog_text_entry)
            .setView(textEntryView)
            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    /* User clicked OK so do some stuff */
                	final EditText et=(EditText)textEntryView.findViewById(R.id.username_edit);
                	strName=et.getText().toString();
                	addListItem();
                }
            })
            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked cancel so do some stuff */
                }
            })
            .create();     
        
    	}
    	return null;
    }
    
    void addListItem()
    {
		ContentValues cv=new ContentValues();
		cv.put(DBD.CT_NAME, strName);
		cv.put(DBD.CT_OWNER_ID, DBD.ownerId);
		
    	switch(menuItemId)
    	{
    	case R.id.add_folder:
    		cv.put(DBD.CT_LINK_TYPE, DBD.LT_FOLDER);
    		break;
    		
    	case R.id.add_counter:                        
    		cv.put(DBD.CT_LINK_TYPE, DBD.LT_COUNTER);
    		break;
   		}

    	DBD.dbw.insert(DBD.TN_FOLDERS, null, cv);

    	setCursorDataAtOwnerId(DBD.ownerId);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	// TODO Auto-generated method stub
    	//super.onListItemClick(l, v, position, id);
    	//Toast.makeText(this, String.valueOf(id), Toast.LENGTH_SHORT).show();
    	
    	cursorG.moveToPosition(position);
    	    	
    	if(cursorG.getLong(2) == DBD.LT_FOLDER)
    	{    	
	    	backSteps.add(DBD.ownerId);
	    	setCursorDataAtOwnerId(id);
	    	//getPath(id);
	    	path.setText(DBD.getOwnersPath(id));
    	}
    	else
    	{
        	if(cursorG.getLong(2) == DBD.LT_COUNTER)
        	{
        		if(getResult)
        		{
	        		setResult((int) id);
	        		finish();
        		}
        		else
        		{
        			selectedItemId=(int) id;
        			showDialog(DIALOG_LIST);
        		}
        	}
    	}
    }
}
