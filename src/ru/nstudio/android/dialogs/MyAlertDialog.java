package ru.nstudio.android.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import ru.nstudio.android.IDialogListener;
import ru.nstudio.android.R;

/**
 * Created by noktigula on 02.04.14.
 */
public class MyAlertDialog extends DialogFragment implements DialogInterface.OnClickListener
{
	protected  static final String ARG_ID_MESSAGE = "message";
	protected  static final String ARG_ID_TITLE = "title";

	public static MyAlertDialog getInstance( int titleId, int messageId )
	{
		MyAlertDialog dialog = new MyAlertDialog();
		Bundle args = new Bundle( );
		args.putInt( ARG_ID_MESSAGE, messageId );
		args.putInt( ARG_ID_TITLE, titleId );
		dialog.setArguments( args );
		return dialog;
	}

	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState )
	{
		int messageId = getArguments().getInt( ARG_ID_MESSAGE );
		int titleId = getArguments().getInt( ARG_ID_TITLE );

		return new AlertDialog.Builder( getActivity() )
				.setTitle( titleId )
				.setMessage( messageId )
				.setPositiveButton( R.string.dialogOK, this )
				.setNegativeButton( R.string.dialogCancel, this )
				.create();
	}

	@Override
	public void onClick( DialogInterface dialogInterface, int i )
	{
		switch( i )
		{
			case DialogInterface.BUTTON_POSITIVE:
			{
				((IDialogListener)getTargetFragment()).onDialogPositiveClick( this );
				break;
			}
			case DialogInterface.BUTTON_NEGATIVE:
			{
				((IDialogListener)getTargetFragment()).onDialogNegativeClick( this );
				break;
			}
		}
	}
}
