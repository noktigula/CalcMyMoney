/*package ru.nstudio.android;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * @class ManagedDialog - обща€ реализаци€ всех классов диалоговых окон, готовых реализовать интерфейс IDialogProtocol
 * create() * prepare() переопредел€ютс€ в базовых классах.
 * »нформирует родительское Activity о завершении работы.
 * @author Noktigula (Pro Android 3)
 * 
public class ManagedDialog implements IDialogProtocol, OnClickListener 
{
	private DialogCaller dCaller;
	private int 		 mDialogId;
	
	public ManagedDialog(DialogCaller caller, int dialogId)
	{
		this.dCaller = caller;
		this.mDialogId = dialogId;
	} // ManagedDialog
	
	public void onClick(DialogInterface dialog, int which) 
	{
		onClickHook(which);
		this.dCaller.dialogFinished(this, which);
	} // onClick

	public Dialog create() {
		// TODO Auto-generated method stub
		return null;
	}

	public void prepare(Dialog dialog) {
		// TODO Auto-generated method stub

	}

	public int getDialogId() 
	{
		return this.mDialogId;
	} // getDialogId
 
	public void show() 
	{
		this.dCaller.show(this.mDialogId);
	} // show

	public void onClickHook(int buttonId) {
		// TODO Auto-generated method stub

	}

} // class ManagedDialog */
