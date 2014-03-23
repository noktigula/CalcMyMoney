package ru.nstudio.android.main;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;

public class MainOverviewFragment extends Fragment
		implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks
{
	private ListView _lv;
	private View _vFooter;
	private FinanceAdapter _fAdapter;

	private static final int LOADER_ID = 2;

	public static MainOverviewFragment getInstance()
	{
		return new MainOverviewFragment();
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		Log.d(getActivity().getResources().getString( R.string.TAG ), container.getClass().getName());
		View v = inflater.inflate( R.layout.list_total_operation_overview, container, false );

		_lv = ( ListView) v.findViewById( R.id.lvMain );
		_vFooter = inflater.inflate( R.layout.tip_add_new_details, container, false );
		int viewHeight = (int)TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 48, getActivity().getResources().getDisplayMetrics() );
		_vFooter.setLayoutParams( new ListView.LayoutParams( ListView.LayoutParams.MATCH_PARENT, viewHeight ) );
		_vFooter.setId( -1 );

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

		_lv.addFooterView( this._vFooter, null, true );

		this._fAdapter = new FinanceAdapter(getActivity(), getActivity().getLayoutInflater(), c, 0);

		try
		{
			_lv.setAdapter( this._fAdapter );
			_lv.setOnItemClickListener( this );
		} // try
		catch(Exception e)
		{
			Log.d( getActivity().getResources().getString( R.string.TAG ), e.getMessage() );
			Toast.makeText( getActivity(), "Что-то пошло не так", 10000000 ).show();
		}
	}

	@Override
	public void onItemClick( AdapterView<?> adapterView, View target, int position, long id )
	{
		Intent intent;
		if (id == -1)
		{
			intent = new Intent( getActivity().getResources().getString( R.string.INTENT_ACTION_ADD ) );
		}
		else
		{
			TextView tvMonth = (TextView)target.findViewById(R.id.tvMainMonthTitle);
			intent = getIntentForChange((int)id, tvMonth.getText().toString());
		}

		runChangeActivity(intent);
	}

	public void runChangeActivity(Intent intent)
	{
		try
		{
			startActivityForResult( intent, ((MainActivity)getActivity()).RESULT_FIRST_USER_MAIN );
		}
		catch(IllegalArgumentException iae)
		{
			Toast.makeText( getActivity(), iae.getMessage(), 10000).show();
		}
	}

	public Intent getIntentForChange(int id, String monthTitle)
	{
		Intent intent = new Intent( getActivity().getResources().getString( R.string.INTENT_ACTION_CHANGE ) );
		intent.putExtra("ru.nstudio.android.selectedItem", id);
		intent.putExtra("ru.nstudio.android.monthTitle", monthTitle);
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
		Log.d( getActivity().getResources().getString( R.string.TAG ), this.getClass().getName() + ": onLoadFinished" );
		_fAdapter.swapCursor( (Cursor)o );
	}

	@Override
	public void onLoaderReset( Loader loader )
	{
		_fAdapter.swapCursor( null );
	}
}
