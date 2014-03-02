package ru.nstudio.android.MonthDetails.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import ru.nstudio.android.DBHelper;
import ru.nstudio.android.MonthDetails.MonthCategoryFragment;
import ru.nstudio.android.R;

/**
 * Created by noktigula on 02.03.14.
 */
public class MonthCategoryAdapter extends BaseAdapter
{
	public static final int ITEM_TYPE = 1;
	public static final int TYPE_COUNT = 1;

	private Cursor _c;
	private LayoutInflater _li;
	private Context _context;
	private ArrayList<View> _views;
	private String _moneyFormat;

	public MonthCategoryAdapter( Context context, LayoutInflater inflater, Cursor c )
	{
		_context = context;
		_li = inflater;
		_c = c;
		_moneyFormat = "%9.2f";
		parseCursor();
	}

	public void parseCursor()
	{
		if( _c == null )
		{
			String err = _context.getString( R.string.errNoCursor);
			throw new IllegalArgumentException(err);
		}

		if( _c.moveToFirst() )
		{
			do
			{
				String title = _c.getString( _c.getColumnIndex( DBHelper.Category.NAME) );
				BigDecimal cost = BigDecimal.valueOf( _c.getDouble( _c.getColumnIndex( "cost" ) ) );

				View v = _li.inflate( R.layout.list_item_month_details_category, null );

				TextView tvCategoryName = (TextView)v.findViewById( R.id.tvCategoryName );
				tvCategoryName.setText( title );

				TextView tvCategoryCost = (TextView)v.findViewById( R.id.tvCategoryTotal );
				tvCategoryCost.setText( String.format( _moneyFormat, cost ) );

				_views.add( v );
			} while(_c.moveToNext());

			_c.close();
		}
	}

	@Override
	public int getCount()
	{
		return _views.size();
	}

	@Override
	public Object getItem( int i )
	{
		return null;
	}

	@Override
	public long getItemId( int position )
	{
		return _views.get( position ).getId();
	}

	@Override
	public View getView( int position, View view, ViewGroup viewGroup )
	{
		return _views.get( position );
	}

	public int getViewTypeCount()
	{
		return this.TYPE_COUNT;
	} // getViewTypeCount

	public int getItemViewType(int position)
	{
		return this.ITEM_TYPE;
	} // getItemViewType
}
