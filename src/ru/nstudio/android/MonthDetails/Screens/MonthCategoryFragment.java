package ru.nstudio.android.MonthDetails.Screens;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import ru.nstudio.android.MonthDetails.Adapters.MonthCategoryAdapter;
import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;

/**
 * Created by noktigula on 26.02.14.
 */
public class MonthCategoryFragment extends Fragment
		implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks
{
	private ListView _lv;
	private View _vFooter;

	private int _year;
	private int _month;

	private MonthCategoryAdapter _adapter;
	private ContentObserver _observer;

	private TextView _tvMonthDescription;

	private static final String KEY_ITEM_ID = "IdItem";
	private static final String KEY_MONTH_TITLE = "MonthTitle";
	private static final int LOADER_ID = 1;
	private static final int RESULT_FIRST_USER_DETAIL = 11;

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View v = inflater.inflate( R.layout.list_month_operations, container, false );

		int itemId = getArguments().getInt( KEY_ITEM_ID );
		_month = itemId % 100;
		_year = itemId / 100;

		StringBuilder stringBuilder = new StringBuilder(  );
		stringBuilder.append( getArguments().getString( KEY_MONTH_TITLE ) );
		stringBuilder.append( " " );
		stringBuilder.append( Integer.toString( _year ) );

		this._tvMonthDescription = (TextView )v.findViewById( R.id.tvMonthDescription );
		_tvMonthDescription.setText( stringBuilder.toString() );

		_vFooter = inflater.inflate(R.layout.tip_add_new_details, null);
		_vFooter.setId( -1 );

		getLoaderManager().initLoader( LOADER_ID, null, this );
		_observer = new ContentObserver(new Handler())
		{
			@Override
			public void onChange( boolean selfChange )
			{
				getActivity().getSupportLoaderManager().restartLoader( LOADER_ID, null, MonthCategoryFragment.this );
			}
		};

		createListView( v );

		return v;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().getContentResolver()
				.registerContentObserver( MoneyContract.ViewMonthCategories.CONTENT_URI, true, _observer );
	}

	@Override
	public void onPause()
	{
		super.onPause();
		getActivity().getContentResolver()
				.unregisterContentObserver( _observer );
	}

	public static MonthCategoryFragment getInstance( int idItem, String monthTitle )
	{
		MonthCategoryFragment mc = new MonthCategoryFragment();

		Bundle args = new Bundle();
		args.putInt( KEY_ITEM_ID, idItem );
		args.putString( KEY_MONTH_TITLE, monthTitle );
		mc.setArguments( args );;

		return mc;
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

	private void createListView( View parent )
	{
		if (this._lv != null)
		{
			this._lv.removeFooterView( this._vFooter );
		}
		this._lv = null;
		this._lv = (ListView)parent.findViewById( R.id.listOperations );

		this._lv.addFooterView(this._vFooter, null, true);

		String where = new String("strftime( '%Y', " + MoneyContract.Finance.DATE + ") = ? " +
				"AND strftime( '%m', " + MoneyContract.Finance.DATE + ") = ? " );
		String[] whereArgs = new String[] {Integer.toString(this._year ), getMonthWithLeadingZero()};

		Cursor c = getActivity().getContentResolver().query(
				MoneyContract.ViewMonthCategories.CONTENT_URI, null, where, whereArgs, null );

		_adapter = new MonthCategoryAdapter(getActivity(), c, R.layout.list_item_month_details_category);

		_lv.setAdapter( _adapter );
		_lv.setOnItemClickListener( this );
	}

	@Override
	public Loader onCreateLoader( int i, Bundle bundle )
	{
		String where = new String("strftime( '%Y', " + MoneyContract.Finance.DATE + ") = ? " +
				"AND strftime( '%m', " + MoneyContract.Finance.DATE + ") = ? ");
		String[] whereArgs = new String[] {Integer.toString(this._year ), getMonthWithLeadingZero()};

		return new CursorLoader( getActivity(), MoneyContract.ViewMonthCategories.CONTENT_URI, null,
				where, whereArgs, null );
	}

	@Override
	public void onLoadFinished( Loader loader, Object o )
	{
		Cursor c = (Cursor) o;
		_adapter.swapCursor( c );
	}

	@Override
	public void onLoaderReset( Loader loader )
	{
		_adapter.swapCursor( null );
	}

	@Override
	public void onItemClick( AdapterView<?> adapterView, View view, int position, long id )
	{
		if( id == -1 )
		{
			Intent intent = new Intent( getActivity().getResources().getString( R.string.INTENT_ACTION_SHOW_DETAILS ) );
			intent.putExtra("ru.nstudio.android.idFinance", id);

			intent.putExtra("ru.nstudio.android._month", this._month );
			intent.putExtra("ru.nstudio.android._year", this._year );

			startActivityForResult( intent, RESULT_FIRST_USER_DETAIL );
		}
	}


}
