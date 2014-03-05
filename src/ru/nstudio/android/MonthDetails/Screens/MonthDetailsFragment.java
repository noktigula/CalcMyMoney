package ru.nstudio.android.MonthDetails.Screens;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ru.nstudio.android.DBHelper;
import ru.nstudio.android.MonthDetails.Adapters.MonthDetailsAdapter;
import ru.nstudio.android.R;

/**
 * Created by noktigula on 16.02.14.
 */
public class MonthDetailsFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>
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

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View v = inflater.inflate( R.layout.month_operations, container, false );

		int idItem = savedInstanceState.getInt( KEY_ID_ITEM );
		_month = idItem % 100;
		_year = idItem / 100;

		StringBuilder monthTitleBuilder = new StringBuilder(  );
		monthTitleBuilder.append( savedInstanceState.getString( KEY_MONTH_TITLE ) );
		monthTitleBuilder.append( " " );
		monthTitleBuilder.append( Integer.toString( _year ) );

		this._tvMonthDescription = (TextView)v.findViewById( R.id.tvMonthDescription );
		this._tvMonthDescription.setText( monthTitleBuilder.toString() );

		this.initDatabase();

		this._vFooter = inflater.inflate(R.layout.tip_add_new_details, null);

		createListView( v, inflater );

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

	private void createListView(View v, LayoutInflater inflater)
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
				"f." + DBHelper.Finance.ID 		+ ", " +
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

		_adapter = new MonthDetailsAdapter( getActivity(), c, R.layout.list_item_month_details_operations, 0 );

		_lvAddFinances.setAdapter(_adapter);
		_lvAddFinances.setOnItemClickListener( this );

		c.close();
		this._db.close();
	}

	@Override
	public Loader<Cursor> onCreateLoader( int i, Bundle bundle )
	{
		return new CursorLoader( getActivity() );
	}

	@Override
	public void onLoadFinished( Loader<Cursor> cursorLoader, Cursor cursor )
	{
		_adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset( Loader<Cursor> cursorLoader )
	{
		_adapter.swapCursor( null );
	}

	@Override
	public void onItemClick( AdapterView<?> adapterView, View view, int i, long l )
	{

	}
}
