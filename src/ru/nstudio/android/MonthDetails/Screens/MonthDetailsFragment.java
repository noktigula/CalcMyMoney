package ru.nstudio.android.MonthDetails.Screens;

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

import ru.nstudio.android.Storage.DBHelper;
import ru.nstudio.android.MonthDetails.Adapters.MonthDetailsAdapter;
import ru.nstudio.android.R;

/**
 * Created by noktigula on 16.02.14.
 */
public class MonthDetailsFragment extends Fragment
		implements AdapterView.OnItemClickListener, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>
{
	private ListView 		_lvAddFinances;
	private View 			_vFooter;
	private DBHelper 		_dbHelper;
	private SQLiteDatabase 	_db;

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
		View v = inflater.inflate( R.layout.month_operations, container, false );

		int idItem = getArguments().getInt( KEY_ID_ITEM );
		_month = idItem % 100;
		_year = idItem / 100;

		StringBuilder monthTitleBuilder = new StringBuilder(  );
		monthTitleBuilder.append( getArguments().getString( KEY_MONTH_TITLE ) );
		monthTitleBuilder.append( " " );
		monthTitleBuilder.append( Integer.toString( _year ) );

		this._tvMonthDescription = (TextView)v.findViewById( R.id.tvMonthDescription );
		this._tvMonthDescription.setText( monthTitleBuilder.toString() );

		this.initDatabase();

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

	private void initDatabase()
	{
		if(this._dbHelper == null)
		{
			this._dbHelper = new DBHelper( getActivity(), DBHelper.CURRENT_DATABASE_VERSION );
		}
		if(this._db == null)
		{
			this._db = this._dbHelper.getWritableDatabase();
		}

		if(!this._db.isOpen())
			this._db = this._dbHelper.getWritableDatabase();
	}

	private void createListView(View v)
	{
		this.initDatabase();
		if (this._lvAddFinances != null)
		{
			this._lvAddFinances.removeFooterView( this._vFooter );
		}
		this._lvAddFinances = null;
		this._lvAddFinances = (ListView)v.findViewById( R.id.listOperations );

		this._lvAddFinances.addFooterView(this._vFooter, null, true);

		String monthWithLeadingZero;
		if ((this._month / 10) == 0)
		{
			monthWithLeadingZero = new String("0" + Integer.toString(this._month ));
		}
		else
		{
			monthWithLeadingZero = new String(Integer.toString(this._month ));
		}

		String[] whereArgs = new String[] {Integer.toString(this._year ), monthWithLeadingZero};

		String query = " SELECT " +
				"f." + DBHelper.Finance.ID 		+ " AS _id, " +
				"f." + DBHelper.Finance.REASON 	+ ", " +
				"f." + DBHelper.Finance.PRICE 	+ ", " +
				"f." + DBHelper.Finance.QUANTITY + ", " +
				"f." + DBHelper.Finance.DATE 	+ ", " +
				"f." + DBHelper.Finance.TYPE 	+ ", " +
				"c." + DBHelper.Category.NAME    +
				" FROM " + DBHelper.Finance.TABLE_NAME + " AS f " +
				" INNER JOIN " + DBHelper.Category.TABLE_NAME +  " AS c " +
				"ON f." + DBHelper.Finance.CATEGORY + " = c." + DBHelper.Category.ID +
				" WHERE strftime('%Y', " + DBHelper.Finance.DATE + ") = ? " +
				"AND strftime('%m', " + DBHelper.Finance.DATE + ") = ? " +
				" ORDER BY strftime('%d', " + DBHelper.Finance.DATE + "), " + DBHelper.Finance.ID;


		Cursor c = this._db.rawQuery(query, whereArgs);

		_adapter = new MonthDetailsAdapter( getActivity(), c, R.layout.list_item_month_details_operations );

		_lvAddFinances.setAdapter(_adapter);
		_lvAddFinances.setOnItemClickListener( this );
	}

	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader( int i, Bundle bundle )
	{
		return new android.support.v4.content.CursorLoader( getActivity() );
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
