package ru.nstudio.android.MonthDetails.Screens;

import android.content.Context;
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

import ru.nstudio.android.DBHelper;
import ru.nstudio.android.MonthDetails.Adapters.MonthDetailsAdapter;
import ru.nstudio.android.R;

/**
 * Created by noktigula on 16.02.14.
 */
public class MonthDetailsFragment extends Fragment
{
	private ListView 		_lvAddFinances;
	private View 			_vFooter;
	private DBHelper 		_dbHelper;
	private SQLiteDatabase 	_db;

	private int 			_month;
	private int 			_year;
	public  boolean 		_wasChanges;
	private TextView 		_tvMonthDescription;

	private String			_monthTitle;

	private Context			_context;
	private AdapterView.OnItemClickListener _listener;

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View v = (View)inflater.inflate( R.layout.month_operations, container, false );

		String monthTitle = new String(_monthTitle + " " + Integer.toString( _year ));

		this._tvMonthDescription = (TextView)v.findViewById( R.id.tvMonthDescription );
		this._tvMonthDescription.setText( monthTitle );

		this.initDatabase();

		this._vFooter = inflater.inflate(R.layout.tip_add_new_details, null);

		createListView( v, inflater );

		this._wasChanges = false;

		return v;
	}

	public static MonthDetailsFragment getInstance( Context context, int idItem, String monthTitle )
	{
		MonthDetailsFragment fragment = new MonthDetailsFragment();

		Bundle arguments = new Bundle(  );


		_context = context;
		_listener = ( AdapterView.OnItemClickListener)context;

		_month = idItem % 100;
		_year = idItem / 100;

		_monthTitle = monthTitle;

		return fragment;
	}

	private void initDatabase()
	{
		if(this._dbHelper == null)
		{
			this._dbHelper = new DBHelper( _context, DBHelper.CURRENT_DATABASE_VERSION);
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

		MonthDetailsAdapter mda = new MonthDetailsAdapter(_context, inflater, c);

		_lvAddFinances.setAdapter(mda);
		_lvAddFinances.setOnItemClickListener( _listener );

		c.close();
		this._db.close();
	}
}
