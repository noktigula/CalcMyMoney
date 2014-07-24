package ru.nstudio.android.MonthDetails.Screens;

import android.support.v4.app.LoaderManager;
import android.content.ContentResolver;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.ListView;

import ru.nstudio.android.MonthDetails.Adapters.MonthDaysAdapter;
import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;

/**
 * Created by noktigula on 23.07.14.
 */
public class MonthDayFragment extends Fragment implements LoaderManager.LoaderCallbacks
{
	private static final String KEY_ITEM = "idItem";
	private static final int LOADER_ID = 5;

	private ListView m_lv;
	private int m_year;
	private int m_month;
	private CursorAdapter m_monthDaysAdapter;
	private ContentObserver m_observer;

	public static MonthDayFragment getInstance(int idItem)
	{
		MonthDayFragment fragment = new MonthDayFragment();
		Bundle args = new Bundle();
		args.putInt( KEY_ITEM, idItem );
		fragment.setArguments( args );
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState)
	{
		View view = inflater.inflate( R.layout.fragment_month_days, parentView, false );

		int idItem = getArguments().getInt( KEY_ITEM );
		m_year = idItem / 100;
		m_month = idItem % 100;

		createListView(view);

		getLoaderManager().initLoader( LOADER_ID, null, this );
		m_observer = new ContentObserver(new Handler())
		{
			@Override
			public void onChange( boolean selfChange )
			{
				getActivity().getSupportLoaderManager().restartLoader( LOADER_ID, null, MonthDayFragment.this );
			}
		};

		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().getContentResolver().registerContentObserver( MoneyContract.ViewMonthDays.CONTENT_URI, true, m_observer );
	}

	@Override
	public void onPause()
	{
		super.onPause();
		getActivity().getContentResolver().unregisterContentObserver( m_observer );
	}

	private void createListView(View view)
	{
		m_lv = (ListView)view.findViewById( R.id.lvDaysOperations );

		String where = "strftime('%m', " + MoneyContract.Finance.DATE + ") = ? AND " +
				"strftime('%y', " + MoneyContract.Finance.DATE + ") = ?";
		String[] whereArgs = new String[]{Integer.toString( m_month ), Integer.toString( m_year )};

		ContentResolver resolver = getActivity().getContentResolver();
		Cursor c = resolver.query( MoneyContract.ViewMonthDays.CONTENT_URI, null, where, whereArgs, null );
		m_monthDaysAdapter = new MonthDaysAdapter( getActivity().getApplicationContext(), c, 0 );
		m_lv.setAdapter( m_monthDaysAdapter );
	}


	@Override
	public Loader onCreateLoader( int id, Bundle args )
	{
		String where = "strftime('%m', " + MoneyContract.Finance.DATE + ") = ? AND " +
				"strftime('%y', " + MoneyContract.Finance.DATE + ") = ?";
		String[] whereArgs = new String[]{Integer.toString( m_month ), Integer.toString( m_year )};

		return new CursorLoader( getActivity(), MoneyContract.ViewMonthDays.CONTENT_URI, null, where,
				whereArgs, null);
	}

	@Override
	public void onLoadFinished( Loader loader, Object data )
	{
		m_monthDaysAdapter.swapCursor( (Cursor)data );
	}

	@Override
	public void onLoaderReset( Loader loader )
	{
		m_monthDaysAdapter.swapCursor( null );
	}

}
