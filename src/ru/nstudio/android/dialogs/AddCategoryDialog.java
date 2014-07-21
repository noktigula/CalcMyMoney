package ru.nstudio.android.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import ru.nstudio.android.IDialogListener;
import ru.nstudio.android.R;

/**
 * Created by noktigula on 09.10.13.
 */
public class AddCategoryDialog extends DialogFragment implements DialogInterface.OnClickListener
{
	private IDialogListener _listener;
	private EditText		_etCategory;
	private boolean _isUpdate;
	private long _itemId;

	public static AddCategoryDialog getInstance( String category, Long itemId )
	{
		Bundle args = new Bundle();
		args.putString( "category", category );
		args.putLong( "itemId", itemId );
		AddCategoryDialog dialog = new AddCategoryDialog();
		dialog.setArguments( args );
		return dialog;
	}

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
		Bundle args = getArguments();
		String inputCategory = args.getString( "category" );

		if( inputCategory != null )
		{
			_etCategory.setText( inputCategory);
			_etCategory.setSelection( inputCategory.length() );
			_etCategory.setFocusable( true );
			_itemId = args.getLong( "itemId" );
		}

		_isUpdate = inputCategory != null;

		String title = _isUpdate ? getString( R.string.dialog_category_title_edit )
								 : getString( R.string.dialog_category_title_add );

		builder.setView( v )
				.setTitle( title )
				.setPositiveButton(  R.string.button_ok , this )
				.setNegativeButton(  R.string.button_cancel , this );

		AlertDialog dialog = builder.create();

		if(_etCategory.requestFocus()) {
			dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}

		return dialog;
	}

	public String getCategory()
	{
		return _etCategory.getText().toString();
	}

	public boolean isUpdate()
	{
		return _isUpdate;
	}

	public long getItemId()
	{
		return _itemId;
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
