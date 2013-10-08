package ru.nstudio.android;

import android.app.DialogFragment;

/**
 * Created by noktigula on 09.10.13.
 */
public interface IDialogListener
{
	public void onDialogPositiveClick( DialogFragment dialog );
	public void onDialogNegativeClick( DialogFragment dialog );
}
