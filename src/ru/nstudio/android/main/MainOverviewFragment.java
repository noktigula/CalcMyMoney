package ru.nstudio.android.main;

import android.support.v4.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ru.nstudio.android.details.DetailsActivity;
import ru.nstudio.android.IDialogListener;
import ru.nstudio.android.MonthDetails.ChangeMonthActivity;
import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;
import ru.nstudio.android.dialogs.MyAlertDialog;

public class MainOverviewFragment extends Fragment
		implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks, IDialogListener
{
	private static final int DIALOG_ID = 1;

	private ListView _lv;
	private FinanceAdapter _fAdapter;

	private int _selectedPos;

	private ActionMode _actionMode;
	private ActionMode.Callback _actionModeCallback = new ActionMode.Callback()
	{
		@Override
		public boolean onCreateActionMode( ActionMode actionMode, Menu menu )
		{
			MenuInflater menuInflater = actionMode.getMenuInflater();
			menuInflater.inflate( R.menu.menu_context, menu );
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
					View target = _lv.getChildAt( _selectedPos );
					TextView tvMonth = (TextView)target.findViewById(R.id.tvMainMonthTitle);
					Intent intent = getIntentForChange(target.getId(), tvMonth.getText().toString());
					runChangeActivity( intent );
					actionMode.finish();
					return true;
				}
				case R.id.menuDelete:
				{
					deleteMonthInfo();
					actionMode.finish();
					return true;
				}
				default: return false;
			}
		}

		@Override
		public void onDestroyActionMode( ActionMode actionMode )
		{
			if( _lv.getChildCount() > _selectedPos )
			{
				_lv.getChildAt( _selectedPos ).setBackgroundResource( Color.TRANSPARENT );
			}
			_actionMode = null;
		}
	};

	private static final int LOADER_ID = 2;

	public static MainOverviewFragment getInstance()
	{
		return new MainOverviewFragment();
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		//Log.d(getActivity().getResources().getString( R.string.TAG ), container.getClass().getName());
		View v = inflater.inflate( R.layout.list_total_operation_overview, container, false );

		_lv = ( ListView) v.findViewById( R.id.lvMain );

		getLoaderManager().initLoader( LOADER_ID, null, this );

		createListView();

		return v;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().getSupportLoaderManager().restartLoader( LOADER_ID, null, MainOverviewFragment.this );
	}

	private void createListView()
	{
		ContentResolver cr = getActivity().getContentResolver();
		Cursor c = cr.query( MoneyContract.ViewYear.CONTENT_URI, null, null, null, null );

		this._fAdapter = new FinanceAdapter(getActivity(), getActivity().getLayoutInflater(), c);

		try
		{
			_lv.setAdapter( this._fAdapter );
			_lv.setOnItemClickListener( this );
		}
		catch(Exception e)
		{
			Log.d( getActivity().getResources().getString( R.string.TAG ), e.getMessage() );
			Toast.makeText( getActivity(), "Что-то пошло не так", Toast.LENGTH_LONG ).show();
		}

		_lv.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick( AdapterView<?> adapterView, View view, int position, long id )
			{
				if( _actionMode != null )
				{
					return false;
				}
				_selectedPos = position;
				_actionMode = ((MainActivity)getActivity()).startSupportActionMode( _actionModeCallback );
				view.setSelected( true );
				view.setBackgroundColor( getResources().getColor(android.R.color.holo_blue_light) );

				return true;
			}
		} );
	}

	@Override
	public void onItemClick( AdapterView<?> adapterView, View target, int position, long id )
	{
		Intent intent;
		if (id == -1)
		{
			intent = new Intent( getActivity(), DetailsActivity.class );
			runChangeActivity( intent );
		}
		else
		{
			TextView tvMonth = (TextView)target.findViewById(R.id.tvMainMonthTitle);
			intent = getIntentForChange(target.getId(), tvMonth.getText().toString());
		}

		runChangeActivity(intent);
	}

	public void runChangeActivity(Intent intent)
	{
		try
		{
			startActivity( intent );
		}
		catch(IllegalArgumentException iae)
		{
			Toast.makeText( getActivity(), iae.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void deleteMonthInfo(  )
	{
		DialogFragment dialogFragment = MyAlertDialog.getInstance( R.string.deleteDialogTitle, R.string.deleteDialogFinalAsk );
		dialogFragment.setTargetFragment( this, DIALOG_ID );
		dialogFragment.show( getActivity().getSupportFragmentManager(), MyAlertDialog.class.toString() );
	}

	public Intent getIntentForChange(int id, String monthTitle)
	{
		Intent intent = new Intent( getActivity(), ChangeMonthActivity.class );
		intent.putExtra(getString( R.string.key_selected_item ), id);
		intent.putExtra(getString( R.string.key_month_title ), monthTitle);
		return intent;
	}

	@Override
	public Loader onCreateLoader( int i, Bundle bundle )
	{
		Uri uri = MoneyContract.ViewYear.CONTENT_URI;
		return new CursorLoader( getActivity(), uri, null, null, null, null );
	}

	@Override
	public void onLoadFinished( Loader loader, Object o )
	{
        _fAdapter.swapCursor( (Cursor)o );
	}

	@Override
	public void onLoaderReset( Loader loader )
	{
		_fAdapter.swapCursor( null );
	}

	@Override
	public void onDialogPositiveClick( DialogFragment dialog )
	{
		View view = _lv.getChildAt( _selectedPos );
		int id = view.getId();

		int year = id / 100;
		int month = id % 100;

		String whereClause = "CAST (strftime('%Y', " + MoneyContract.Finance.DATE + " ) AS INTEGER) = ? AND " +
				"CAST (strftime('%m', " + MoneyContract.Finance.DATE + " ) AS INTEGER) = ? ";
		String[] whereArgs = new String[]{ Integer.toString( year ), Integer.toString( month ) };

		ContentResolver cr = getActivity().getContentResolver();
		cr.delete( MoneyContract.Finance.CONTENT_URI, whereClause, whereArgs );

		getActivity().getSupportLoaderManager().restartLoader( LOADER_ID, null, this );
	}

	@Override
	public void onDialogNegativeClick( DialogFragment dialog )
	{
		return;
	}
}
