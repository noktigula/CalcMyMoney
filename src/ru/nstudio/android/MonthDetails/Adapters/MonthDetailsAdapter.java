package ru.nstudio.android.MonthDetails.Adapters;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.nstudio.android.DBHelper;
import ru.nstudio.android.DateParser;
import ru.nstudio.android.R;

public class MonthDetailsAdapter extends CursorAdapter //implements OnItemClickListener
{
	private String	_moneyFormat;
	private int 	_layout;

	public MonthDetailsAdapter( Context context, Cursor cursor, int layout )
	{
		super( context, cursor, 0 );
		_moneyFormat = context.getString( R.string.money_format);
		_layout = layout;
	} // MonthDetailsAdapter

	public Object getItem(int position) 
	{
		return null;
	} // getItem

	@Override
	public View newView( Context context, Cursor cursor, ViewGroup parentGroup )
	{
		LayoutInflater inflater = LayoutInflater.from( context );
		return inflater.inflate( _layout, parentGroup, false );
	}

	@Override
	public void bindView( View v, Context context, Cursor c )
	{
		boolean isIncome = (c.getInt(c.getColumnIndex( DBHelper.Finance.TYPE)) == 1);
		String explain = new String(c.getString(c.getColumnIndex(DBHelper.Finance.REASON)));
		Double price = c.getDouble(c.getColumnIndex(DBHelper.Finance.PRICE));
		Integer quant = c.getInt(c.getColumnIndex(DBHelper.Finance.QUANTITY));
		int id = c.getInt(c.getColumnIndex(DBHelper.Finance.ID));
		String date = new String(c.getString(c.getColumnIndex(DBHelper.Finance.DATE)));
		String category = new String(c.getString(c.getColumnIndex(DBHelper.Category.NAME)));

		String dateDescript =
				DateParser.format( date, DateParser.CALCMONEY_FORMAT );

		int color = (isIncome) ? Color.GREEN : Color.RED;

		TextView tvExplain = (TextView) v.findViewById(R.id.tvFinanceOperationDetails1);
		tvExplain.setText(explain);

		TextView tvPrice = (TextView) v.findViewById(R.id.tvShowPrice1);
		tvPrice.setText(String.format( _moneyFormat, price ) );
		tvPrice.setTextColor(color);

		TextView tvQuant = (TextView) v.findViewById(R.id.tvShowQuant1);
		tvQuant.setText(quant.toString());
		tvQuant.setTextColor(color);

		TextView tvBalance = (TextView) v.findViewById(R.id.tvShowTotalCost1);
		tvBalance.setText(String.format( _moneyFormat, price * quant ) );
		tvBalance.setTextColor(color);

		TextView tvDate = (TextView) v.findViewById(R.id.tvShowDate1);
		tvDate.setText(dateDescript);

		TextView tvCategory = (TextView) v.findViewById(R.id.tvShowCategory);
		tvCategory.setText(category);

		v.setId(id);
	}
} // class MonthDetailsAdapter
