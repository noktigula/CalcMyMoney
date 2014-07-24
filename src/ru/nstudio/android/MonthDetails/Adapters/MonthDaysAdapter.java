package ru.nstudio.android.MonthDetails.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.TextView;

import ru.nstudio.android.DateParser;
import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;

/**
 * Created by noktigula on 23.07.14.
 */
public class MonthDaysAdapter extends CursorAdapter
{
	public MonthDaysAdapter( Context context, Cursor c, int flags )
	{
		super( context, c, flags );
	}

	@Override
	public View newView( Context context, Cursor cursor, ViewGroup parent )
	{
		LayoutInflater inflater = LayoutInflater.from( context );
		return inflater.inflate( R.layout.list_item_month_details_day, parent);
	}

	@Override
	public void bindView( View view, Context context, Cursor cursor )
	{
		int dayIndex = cursor.getColumnIndex( MoneyContract.ViewMonthDays.DAY_NUMBER );
		int dayIncomeIndex = cursor.getColumnIndex( MoneyContract.ViewMonthDays.DAY_INCOME );
		int dayExpendIndex = cursor.getColumnIndex( MoneyContract.ViewMonthDays.DAY_EXPEND );

		String dayNumber = cursor.getString( dayIndex );
		String day = DateParser.format( dayNumber, DateParser.CALCMONEY_FORMAT );

		double dayIncome = cursor.getDouble( dayIncomeIndex );
		double dayExpend = cursor.getDouble( dayExpendIndex );

		((TextView )view.findViewById( R.id.tvDayNumber )).setText(day);
		((TextView)view.findViewById( R.id.tvDayIncome )).setText( Double.toString( dayIncome ) );
		((TextView)view.findViewById( R.id.tvDayExpend )).setText( Double.toString( dayExpend ) );
	}
}
