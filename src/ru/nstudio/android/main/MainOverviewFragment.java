package ru.nstudio.android.main;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;

public class MainOverviewFragment extends Fragment
		implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks
{
	private ListView _lv;
	private View _vFooter;
	private FinanceAdapter _fAdapter;

	public static MainOverviewFragment getInstance()
	{
		return new MainOverviewFragment();
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View v = inflater.inflate( R.layout.list_total_operation_overview, container, false );

		_lv = ( ListView) v.findViewById( R.id.lvMain );
		_vFooter = inflater.inflate( R.layout.tip_add_new_details, container, false );
		_vFooter.setLayoutParams( new ListView.LayoutParams( ListView.LayoutParams.MATCH_PARENT, 48 ) );

		createListView();

		return v;
	}

	private void createListView()
	{
		String [] args = new String[]{};

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
		} // catch
	}

	@Override
	public void onItemClick( AdapterView<?> adapterView, View view, int i, long l )
	{

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
}
