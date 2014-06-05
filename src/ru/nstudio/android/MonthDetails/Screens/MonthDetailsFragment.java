package ru.nstudio.android.MonthDetails.Screens;

import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.nstudio.android.details.DetailsActivity;
import ru.nstudio.android.IDialogListener;
import ru.nstudio.android.MonthDetails.Adapters.MonthDetailsAdapter;
import ru.nstudio.android.MonthDetails.ChangeMonthActivity;
import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;
import ru.nstudio.android.dialogs.MyAlertDialog;

/**
 * Created by noktigula on 16.02.14.
 */
public class MonthDetailsFragment extends Fragment
		implements AdapterView.OnItemClickListener, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>, IDialogListener
{
	private static final int DIALOG_ID = 1;

	private ListView 		_lvAddFinances;

	private int 			_month;
	private int				_year;
	private MonthDetailsAdapter _adapter;

	private ContentObserver _observer;

	private ActionMode _actionMode;
	private ActionMode.Callback _callback = new ActionMode.Callback()
	{
		@Override
		public boolean onCreateActionMode( ActionMode actionMode, Menu menu )
		{
			actionMode.getMenuInflater().inflate( R.menu.action_mode_month_details, menu );
			return true;
		}

		@Override
		public boolean onPrepareActionMode( ActionMode actionMode, Menu menu )
		{
			return false;
		}

		@Override
		public boolean onActionItemClicked( ActionMode actionMode, MenuItem menuItem )
		{
			switch( menuItem.getItemId() )
			{
				case R.id.menuEdit:
				{
					showOperationDetails( _lvAddFinances.getChildAt( _selectedItem ).getId() );
					actionMode.finish();
					return true;
				}
				case R.id.menuDelete:
				{
					deleteOperation( _lvAddFinances.getChildAt( _selectedItem ).getId() );
					actionMode.finish();
					return true;
				}
				default: return false;
			}
		}

		@Override
		public void onDestroyActionMode( ActionMode actionMode )
		{
			if( _lvAddFinances.getChildCount() > _selectedItem )
			{
				_lvAddFinances.getChildAt( _selectedItem ).setBackgroundResource( Color.TRANSPARENT );
			}
			_actionMode = null;
		}
	};

	private static final String KEY_ID_ITEM = "IdItem";

	private static final int LOADER_ID = 0;

	private int _selectedItem;

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View v = inflater.inflate( R.layout.list_month_operations, container, false );

		int idItem = getArguments().getInt( KEY_ID_ITEM );
		_month = idItem % 100;
		_year = idItem / 100;

		createListView( v );

		getLoaderManager().initLoader( LOADER_ID, null, this );
		_observer = new ContentObserver(new Handler())
		{
			@Override
			public void onChange( boolean selfChange )
			{
				Log.d(getActivity().getResources().getString( R.string.TAG ), "MonthDetailsView was changed");
				getActivity().getSupportLoaderManager().restartLoader( LOADER_ID, null, MonthDetailsFragment.this );
			}
		};

		return v;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		getActivity().getContentResolver()
				.registerContentObserver( MoneyContract.ViewMonthOperations.CONTENT_URI, true, _observer );
	}

	@Override
	public void onPause()
	{
		super.onPause();
		getActivity().getContentResolver().unregisterContentObserver( _observer );
	}

	public static MonthDetailsFragment getInstance( int idItem)
	{
		MonthDetailsFragment fragment = new MonthDetailsFragment();

		Bundle arguments = new Bundle();
		arguments.putInt( KEY_ID_ITEM, idItem );
		fragment.setArguments( arguments );

		return fragment;
	}

	private String getMonthWithLeadingZero()
	{
		String monthWithLeadingZero;
		if ((this._month / 10) == 0)
		{
			monthWithLeadingZero = new String("0" + Integer.toString(this._month ));
		}
		else
		{
			monthWithLeadingZero = new String(Integer.toString(this._month ));
		}
		return monthWithLeadingZero;
	}

	private void createListView(View v)
	{
		this._lvAddFinances = (ListView)v.findViewById( R.id.listOperations );

		String[] whereArgs = new String[] { Integer.toString(this._year ), getMonthWithLeadingZero() };
		String where = new String("strftime('%Y', " + MoneyContract.ViewMonthOperations.DATE + ") = ? " +
				"AND strftime('%m', " + MoneyContract.ViewMonthOperations.DATE + ") = ? ");

		ContentResolver cr = getActivity().getContentResolver();
		Cursor c = cr.query( MoneyContract.ViewMonthOperations.CONTENT_URI,
				null, where, whereArgs, null );

		_adapter = new MonthDetailsAdapter( getActivity(), c, R.layout.list_item_month_details_operations );

		_lvAddFinances.setAdapter(_adapter);
		_lvAddFinances.setOnItemClickListener( this );
		_lvAddFinances.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick( AdapterView<?> adapterView, View view, int position, long id )
			{
				if( _actionMode != null )
				{
					return false;
				}
				_selectedItem = position;
				_actionMode = ((ChangeMonthActivity)getActivity()).startSupportActionMode( _callback );
				view.setBackgroundColor( getResources().getColor(android.R.color.holo_blue_light) );
				view.setSelected( true );
				return true;
			}
		} );
	}

	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader( int i, Bundle bundle )
	{
		String[] whereArgs = new String[] { Integer.toString(this._year ), getMonthWithLeadingZero() };
		String where = new String("strftime('%Y', " + MoneyContract.ViewMonthOperations.DATE + ") = ? " +
				"AND strftime('%m', " + MoneyContract.ViewMonthOperations.DATE + ") = ? ");

		return new android.support.v4.content.CursorLoader(
				getActivity(), MoneyContract.ViewMonthOperations.CONTENT_URI, null,
				where, whereArgs, null );
	}

	@Override
	public void onLoadFinished( android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor )
	{
		_adapter.swapCursor( cursor );
	}

	@Override
	public void onLoaderReset( android.support.v4.content.Loader<Cursor> cursorLoader )
	{
		_adapter.swapCursor( null );
	}

	@Override
	public void onItemClick( AdapterView<?> adapterView, View view, int position, long id )
	{
		showOperationDetails( id );
	}

	private void showOperationDetails( long id )
	{
		Intent intent = new Intent( getActivity(), DetailsActivity.class);
        intent.putExtra( getString( R.string.key_finance_id ), id);

        intent.putExtra( getString( R.string.key_month ), this._month );
        intent.putExtra( getString( R.string.key_year ), this._year );

        startActivity( intent );
	}

	private void deleteOperation( long id )
	{
		DialogFragment deleteDialog = MyAlertDialog.getInstance( R.string.deleteDialogTitle, R.string.deleteDialogFinalAsk );
		deleteDialog.setTargetFragment( this, DIALOG_ID );
		deleteDialog.show( getActivity().getSupportFragmentManager(), MyAlertDialog.class.toString() );
	}

	@Override
	public void onDialogPositiveClick( DialogFragment dialog )
	{
		if( dialog instanceof MyAlertDialog )
		{
			long id = _lvAddFinances.getChildAt( _selectedItem ).getId();
			Uri uri = Uri.withAppendedPath( MoneyContract.Finance.CONTENT_URI, Long.toString( id ) );
			getActivity().getContentResolver().delete( uri, null, null);
			getActivity().getSupportLoaderManager().restartLoader( LOADER_ID, null, this );
		}
	}

	@Override
	public void onDialogNegativeClick( DialogFragment dialog )
	{
		return;
	}
}
