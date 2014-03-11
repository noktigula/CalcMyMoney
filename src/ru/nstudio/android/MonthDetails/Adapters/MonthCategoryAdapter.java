package ru.nstudio.android.MonthDetails.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import ru.nstudio.android.DBHelper;
import ru.nstudio.android.R;

/**
 * Created by noktigula on 02.03.14.
 */
public class MonthCategoryAdapter extends CursorAdapter
{
	private String _moneyFormat;
	private int	_layoutId;

	public MonthCategoryAdapter( Context context, Cursor c, int layout )
	{
		super( context, c, 0 );
		_layoutId = layout;
		_moneyFormat = context.getResources().getString( R.string.money_format );
	}

	@Override
	public View newView( Context context, Cursor cursor, ViewGroup parentGroup )
	{
		Log.d( "nTag", "MonthCategoryAdapter - new view" );
		LayoutInflater inflater = LayoutInflater.from( context );
		return inflater.inflate( _layoutId, parentGroup, false );
	}

	@Override
	public void bindView( View view, Context context, Cursor cursor )
	{
		Log.d( "nTag", "MonthCategoryAdapter - new view" );

		String title = cursor.getString( cursor.getColumnIndex( DBHelper.Category.NAME ) );
		BigDecimal cost = BigDecimal.valueOf( cursor.getDouble( cursor.getColumnIndex( "cost" ) ) );

		TextView tvCategoryName = (TextView)view.findViewById( R.id.tvCategoryName );
		tvCategoryName.setText( title );

		TextView tvCategoryCost = (TextView)view.findViewById( R.id.tvCategoryTotal );
		tvCategoryCost.setText( String.format( _moneyFormat, cost ) );
	}
}
