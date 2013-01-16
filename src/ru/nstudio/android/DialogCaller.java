/*package ru.nstudio.android;

import android.app.Activity;
import android.app.Dialog;

public class DialogCaller implements IDialogFinishedCallback 
{
	//Реестр управляемых окон
	private DialogRegistry 	dr = new DialogRegistry();
	private Activity	   	activity;
	public static final int	DIALOG_CURRENCY = 0;
	public static final int DIALOG_QUANTITY = 1;
	
	public DialogCaller(Activity activity)
	{
		this.activity = activity;
	} // DialogCaller
	
	public void call(int dialogId)
	{
		Dialog dialog = this.dr.get(dialogId);
		if (dialog == null)
		{
			if (dialogId == DIALOG_CURRENCY)
			{
				dialog = new CurrencyDialog();
				
			} // if
		} // if
	} // call
	
	public void dialogFinished(ManagedDialog dialog, int buttonId) 
	{

	} // dialogFinished

} // class DialogCaller*/
