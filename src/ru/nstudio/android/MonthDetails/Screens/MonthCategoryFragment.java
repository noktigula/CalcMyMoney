package ru.nstudio.android.MonthDetails.Screens;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import ru.nstudio.android.MonthDetails.Adapters.MonthCategoryAdapter;
import ru.nstudio.android.R;

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
	public boolean _wasChanges;
	private MonthCategoryAdapter _adapter;

	private TextView _tvMonthDescription;

	private static final String KEY_ITEM_ID = "IdItem";
	private static final String KEY_MONTH_TITLE = "MonthTitle";

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View v = (View)inflater.inflate( R.layout.list_month_operations, container, false );

		int itemId = getArguments().getInt( KEY_ITEM_ID );
		_month = itemId % 100;
		_year = itemId / 100;

		StringBuilder stringBuilder = new StringBuilder(  );
		stringBuilder.append( getArguments().getString( KEY_MONTH_TITLE ) );
		stringBuilder.append( " " );
		stringBuilder.append( Integer.toString( _year ) );

		this._tvMonthDescription = (TextView )v.findViewById( R.id.tvMonthDescription );
		this._tvMonthDescription.setText( stringBuilder.toString() );

		this._vFooter = inflater.inflate(R.layout.tip_add_new_details, null);

		createListView( v );

		this._wasChanges = false;

		return v;
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

	private void createListView( View parent )
	{
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

		String where = new String("WHERE strftime( '%Y', " + DBHelper.Finance.DATE + ") = ? " +
				"AND strftime( '%m', " + DBHelper.Finance.DATE + ") = ? " +)
		String[] whereArgs = new String[] {Integer.toString(this._year ), monthWithLeadingZero};

		String query = "SELECT c." + DBHelper.Category.ID + " AS _id, " +
				"c." + DBHelper.Category.NAME + ", " +
				"SUM( " + DBHelper.Finance.PRICE + " * " + DBHelper.Finance.QUANTITY + " ) AS cost " +
				"FROM " + DBHelper.Finance.TABLE_NAME + " AS f " +
				"INNER JOIN " + DBHelper.Category.TABLE_NAME + " AS c " +
				"ON f." + DBHelper.Finance.CATEGORY + " = c." + DBHelper.Category.ID + " " +

				"GROUP BY c." + DBHelper.Category.NAME + " " +
				"ORDER BY c." + DBHelper.Category.ID;

		Cursor c = this._db.rawQuery(query, whereArgs);

		MonthCategoryAdapter mca = new MonthCategoryAdapter(getActivity(), c, R.layout.list_item_month_details_category);

		_lv.setAdapter(mca);
		_lv.setOnItemClickListener( this );
	}

	@Override
	public Loader onCreateLoader( int i, Bundle bundle )
	{
		return new CursorLoader( getActivity() );
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
	public void onItemClick( AdapterView<?> adapterView, View view, int i, long l )
	{

	}
}
