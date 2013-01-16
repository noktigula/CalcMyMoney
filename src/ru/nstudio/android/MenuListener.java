package ru.nstudio.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;

/*
public class MenuListener implements OnMenuItemClickListener, DialogInterface.OnClickListener
{
	/*private final int	DIALOG_SET_CURRENCY = 0;	
	private final int	DIALOG_SET_QUANTITY = 1;
	private final int 	DIALOG_DEV_HELP		= 2;
	*/
	/*public static final int RESULT_MENU_PREFERENCES = 15;
	public static final int RESULT_MENU_DEV_HELP	= 16;
	
	private Activity	activity;
	
	public MenuListener(Activity activity)
	{
		this.activity = activity;
	} // MenuListener
	
	public boolean onMenuItemClick(MenuItem item) 
	{
		int id = item.getItemId();
		switch (id)
		{
			/*case R.id.menuCurrency:
			{
				this.activity.showDialog(DIALOG_SET_CURRENCY);
				break;
			} // case R.id.menuCurrency
			
			case R.id.menuQuantity:
			{
				this.activity.showDialog(DIALOG_SET_QUANTITY);
				break;
			} // case menuQuant
		
			case R.id.menuPreferences:
			{
				Intent intent = new Intent("ru.nstudio.android.showPreferences");
				this.activity.startActivityForResult(intent, RESULT_MENU_PREFERENCES);
			} // case Preferences
			
			case R.id.menuDevHelp:
			{
				//this.activity.showDialog(DIALOG_DEV_HELP);
				break;
			} // case devHelp
		} // switch id
		return true;
	} // onMenuItemClick
	
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
			case DIALOG_SET_CURRENCY:
			{
				AlertDialog.Builder adb = new AlertDialog.Builder(this.activity);
				View v = this.activity.getLayoutInflater().inflate(R.layout.currency_dialog, null);
				adb.setView(v);
				adb.setTitle(R.string.currencyDialogTitle);
				adb.setPositiveButton(R.string.button_ok, this);
				adb.setNegativeButton(R.string.button_cancel, this);
				AlertDialog ad = adb.create();
				return ad;
			} // case DIALOG_SET_CURRENCY
		} // switch id
		
		return null;
	} // onCreateDialog

	public void onClick(DialogInterface v, int buttonId) 
	{
		// TODO Auto-generated method stub
		
	}
	
} // class MenuListener*/
