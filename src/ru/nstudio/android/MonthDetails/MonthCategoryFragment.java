package ru.nstudio.android.MonthDetails;

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

import ru.nstudio.android.DBHelper;
import ru.nstudio.android.MonthDetailsAdapter;
import ru.nstudio.android.R;

/**
 * Created by noktigula on 26.02.14.
 */
public class MonthCategoryFragment extends Fragment
{
	private ListView _lv;
	private View _vFooter;

	private DBHelper _dbHelper;
	private SQLiteDatabase _db;

	private int _year;
	private int _month;
	public boolean _wasChanges;

	private String _monthTitle;

	private Context _context;
	private AdapterView.OnItemClickListener _listener;

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		return null;
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

	public MonthCategoryFragment getInstance( Context context, int idItem, String monthTitle )
	{
		MonthCategoryFragment mc = new MonthCategoryFragment();
		_context = context;
		_listener = (AdapterView.OnItemClickListener)context;

		_month = idItem % 100;
		_year = idItem / 100;

		_monthTitle = monthTitle;

		return mc;
	}

	private void createListView( View parent, LayoutInflater inflater )
	{
		this.initDatabase();
		if (this._lv != null)
		{
			this._lv.removeFooterView( this._vFooter );
		}
		this._lv = null;
		this._lv = (ListView)parent.findViewById( R.id.listOperations );

		this._lv.addFooterView(this._vFooter, null, true);

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

		//TODO implement correct query

//		String query = " SELECT " +
//				"f." + DBHelper.Finance.ID 		+ ", " +
//				"f." + DBHelper.Finance.REASON 	+ ", " +
//				"f." + DBHelper.Finance.PRICE 	+ ", " +
//				"f." + DBHelper.Finance.QUANTITY + ", " +
//				"f." + DBHelper.Finance.DATE 	+ ", " +
//				"f." + DBHelper.Finance.TYPE 	+ ", " +
//				"c." + DBHelper.Category.NAME    +
//				" FROM " + DBHelper.Finance.TABLE_NAME + " AS f " +
//				" INNER JOIN " + DBHelper.Category.TABLE_NAME +  " AS c " +
//				"ON f." + DBHelper.Finance.CATEGORY + " = c." + DBHelper.Category.ID +
//				" WHERE strftime('%Y', " + DBHelper.Finance.DATE + ") = ? " +
//				"AND strftime('%m', " + DBHelper.Finance.DATE + ") = ? " +
//				" ORDER BY strftime('%d', " + DBHelper.Finance.DATE + "), " + DBHelper.Finance.ID;


		Cursor c = this._db.rawQuery(query, whereArgs);

		MonthDetailsAdapter mda = new MonthDetailsAdapter(_context, inflater, c);

		_lv.setAdapter(mda);
		_lv.setOnItemClickListener( _listener );

		c.close();
		this._db.close();
	}
}
