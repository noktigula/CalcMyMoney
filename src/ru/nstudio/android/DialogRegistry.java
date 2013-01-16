/*package ru.nstudio.android;

import android.app.Dialog;
import android.util.SparseArray;

/**
 * @class DialogRegisty класс-регистратор диалоговых окон
 * @author Noktigula (Pro Android 3)
 * 
public class DialogRegistry 
{
	SparseArray<IDialogProtocol> idForDialog = new SparseArray();
	
	public void registerDialog(IDialogProtocol dialog)
	{
		idForDialog.put(dialog.getDialogId(), dialog);
	} // registerDialog
	
	public Dialog get(int id)
	{
		IDialogProtocol idp = idForDialog.get(id);
		if(idp == null) return null;
		
		return idp.create();
	} // create
	
	public void prepare(Dialog dialog, int id)
	{
		IDialogProtocol idp = idForDialog.get(id);
		if (idp == null)
			throw new RuntimeException("Dialog not registered " + id);
		
		idp.prepare(dialog);
	} // prepare
} // class DialogRegisty*/
