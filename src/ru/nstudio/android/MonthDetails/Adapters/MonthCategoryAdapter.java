package ru.nstudio.android.MonthDetails.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;

import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;

/**
 * Created by noktigula on 02.03.14.
 */
public class MonthCategoryAdapter extends CursorAdapter
{
	private static final String TAG = MonthCategoryAdapter.class.getName();
	private String _moneyFormat;
	private int	_layoutId;

	public MonthCategoryAdapter( Context context, Cursor c, int layout )
	{
		super( context, c, 0 );
		Log.d( TAG, "Cursor count = " + c.getCount() );
		_layoutId = layout;
		_moneyFormat = context.getResources().getString( R.string.money_format );
	}

	@Override
	public View newView( Context context, Cursor cursor, ViewGroup parentGroup )
	{
		LayoutInflater inflater = LayoutInflater.from( context );
		return inflater.inflate( _layoutId, parentGroup, false );
	}

	@Override
	public void bindView( View view, Context context, Cursor cursor )
	{
		String title = cursor.getString( cursor.getColumnIndex( MoneyContract.Category.NAME ) );
		BigDecimal cost = BigDecimal.valueOf( cursor.getDouble( cursor.getColumnIndex( MoneyContract.ViewMonthCategories.CATEGORY_SUM ) ) );

		TextView tvCategoryName = (TextView)view.findViewById( R.id.tvCategoryName );
		tvCategoryName.setText( title );

		TextView tvCategoryCost = (TextView)view.findViewById( R.id.tvCategoryTotal );
		tvCategoryCost.setText( String.format( _moneyFormat, cost ) );

		String groupType = cursor.getString( cursor.getColumnIndex( MoneyContract.ViewMonthCategories.CATEGORY_TYPE ) );
		int type = Integer.parseInt( groupType );

		boolean isIncome = type == MoneyContract.ViewMonthCategories.TYPE_INCOME;

		int colorRes = isIncome ? R.color.income_view_background : R.color.expend_view_background;

		view.setBackgroundColor( context.getResources().getColor( colorRes ) );

		int id = cursor.getInt( cursor.getColumnIndex( "_id" ) );
		view.setId( id );
	}
}
