package ru.nstudio.android.MonthDetails.Screens;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import ru.nstudio.android.MonthDetails.Adapters.MonthDetailsAdapter;
import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;

/**
 * Created by noktigula on 16.02.14.
 */
public class MonthDetailsFragment extends Fragment
		implements AdapterView.OnItemClickListener, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>
{
	private ListView 		_lvAddFinances;
	private View 			_vFooter;

	private int 			_month;
	private int				_year;
	public  boolean 		_wasChanges;
	private TextView 		_tvMonthDescription;
	private MonthDetailsAdapter _adapter;

	private static final String KEY_MONTH_TITLE = "MonthTitle";
	private static final String KEY_ID_ITEM = "IdItem";

	private final String INTENT_ACTION_SHOW_DETAILS = "ru.nstudio.android.showDetails";
	private static int RESULT_FIRST_USER_DETAIL = 11;

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View v = inflater.inflate( R.layout.list_month_operations, container, false );

		int idItem = getArguments().getInt( KEY_ID_ITEM );
		_month = idItem % 100;
		_year = idItem / 100;

		StringBuilder monthTitleBuilder = new StringBuilder(  );
		monthTitleBuilder.append( getArguments().getString( KEY_MONTH_TITLE ) );
		monthTitleBuilder.append( " " );
		monthTitleBuilder.append( Integer.toString( _year ) );

		this._tvMonthDescription = (TextView)v.findViewById( R.id.tvMonthDescription );
		this._tvMonthDescription.setText( monthTitleBuilder.toString() );

		this._vFooter = inflater.inflate(R.layout.tip_add_new_details, null);

		createListView( v );

		this._wasChanges = false;

		return v;
	}

	public static MonthDetailsFragment getInstance( int idItem, String monthTitle )
	{
		MonthDetailsFragment fragment = new MonthDetailsFragment();

		Bundle arguments = new Bundle();
		arguments.putInt( KEY_ID_ITEM, idItem );
		arguments.putString( KEY_MONTH_TITLE, monthTitle );
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
		if (this._lvAddFinances != null)
		{
			this._lvAddFinances.removeFooterView( this._vFooter );
		}
		this._lvAddFinances = null;
		this._lvAddFinances = (ListView)v.findViewById( R.id.listOperations );

		this._lvAddFinances.addFooterView(this._vFooter, null, true);

		String[] whereArgs = new String[] { Integer.toString(this._year ), getMonthWithLeadingZero() };
		String where = new String("strftime('%Y', " + MoneyContract.ViewMonthOperations.DATE + ") = ? " +
				"AND strftime('%m', " + MoneyContract.ViewMonthOperations.DATE + ") = ? ");

		ContentResolver cr = getActivity().getContentResolver();
		Cursor c = cr.query( MoneyContract.ViewMonthOperations.CONTENT_URI,
				null, where, whereArgs, null );

		_adapter = new MonthDetailsAdapter( getActivity(), c, R.layout.list_item_month_details_operations );

		_lvAddFinances.setAdapter(_adapter);
		_lvAddFinances.setOnItemClickListener( this );
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
		Intent intent = new Intent(this.INTENT_ACTION_SHOW_DETAILS);
        intent.putExtra("ru.nstudio.android.idFinance", id);

        intent.putExtra("ru.nstudio.android._month", this._month );
        intent.putExtra("ru.nstudio.android._year", this._year );

        startActivityForResult(intent, RESULT_FIRST_USER_DETAIL);
	}
}
