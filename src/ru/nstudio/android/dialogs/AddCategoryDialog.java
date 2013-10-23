package ru.nstudio.android.dialogs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ru.nstudio.android.IDialogListener;
import ru.nstudio.android.R;

/**
 * Created by noktigula on 09.10.13.
 */
@TargetApi( Build.VERSION_CODES.HONEYCOMB )
public class AddCategoryDialog extends DialogFragment implements DialogInterface.OnClickListener
{
	private IDialogListener _listener;
	private EditText		_etCategory;

	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );
		try
		{
			_listener = (IDialogListener)activity;
		}
		catch( ClassCastException e )
		{
			throw new ClassCastException( activity.toString() + " must implement IDialogListener " );
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate( R.layout.dialog_add_category, null );

		_etCategory = (EditText)v.findViewById( R.id.etAddCategory );

		builder.setView( v )
				.setTitle( getActivity().getResources().getString( R.string.dialog_category_title ) )
				.setPositiveButton(  R.string.button_ok , this )
				.setNegativeButton(  R.string.button_cancel , this );

		return builder.create();
	}

	public String getCategory()
	{
		return _etCategory.getText().toString();
	}

	@Override
	public void onClick( DialogInterface dialogInterface, int i )
	{
		switch ( i )
		{
			case DialogInterface.BUTTON_POSITIVE:
				_listener.onDialogPositiveClick( AddCategoryDialog.this );
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				_listener.onDialogNegativeClick( AddCategoryDialog.this );
				break;

			default:
				break;
		}
	}
}
